package xyz.mackan.crystallurgy.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

import java.util.List;
import java.util.function.Consumer;

public class ResonanceForgeRecipeJsonBuilder {
    private final List<Ingredient> ingredients;
    private final ItemStack result;
    private int energyPerTick;
    private int ticks;

    public ResonanceForgeRecipeJsonBuilder(List<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public static ResonanceForgeRecipeJsonBuilder create(List<Ingredient> ingredients, ItemConvertible resultItem, int count) {
        return new ResonanceForgeRecipeJsonBuilder(ingredients, new ItemStack(resultItem, count));
    }

    public ResonanceForgeRecipeJsonBuilder energyPerTick(int energyPerTick) {
        this.energyPerTick = energyPerTick;
        return this;
    }

    public ResonanceForgeRecipeJsonBuilder ticks(int ticks) {
        this.ticks = ticks;
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        exporter.accept(new RecipeJsonProvider() {
            @Override
            public void serialize(JsonObject json) {
                json.addProperty("type", "crystallurgy:resonance_forging");

                JsonArray ingredientsArray = new JsonArray();
                for (Ingredient ingredient : ingredients) {
                    Crystallurgy.LOGGER.info("INGREDIENT IS {}", ingredient.toJson());
                    JsonObject ingredientJson = ingredient.toJson().getAsJsonObject();
                    ItemStack[] matching = ingredient.getMatchingStacks();

                    if (matching[0].getCount() > 0) {
                        ingredientJson.addProperty("count", matching[0].getCount());
                    }


                    ingredientsArray.add(ingredientJson);
                }
                json.add("ingredients", ingredientsArray);

                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("item", result.getItem().getRegistryEntry().registryKey().getValue().toString());
                if (result.getCount() > 1) {
                    resultObj.addProperty("count", result.getCount());
                }
                json.add("output", resultObj);

                JsonObject energyObject = new JsonObject();
                energyObject.addProperty("energy_per_tick", energyPerTick);
                energyObject.addProperty("ticks", ticks);

                json.add("energy", energyObject);
            }

            @Override
            public Identifier getRecipeId() {
                return recipeId;
            }

            @Override
            public RecipeSerializer<?> getSerializer() {
                return ResonanceForgeRecipe.Serializer.INSTANCE;
            }

            @Override
            public JsonObject toAdvancementJson() {
                return null; // optional
            }

            @Override
            public Identifier getAdvancementId() {
                return null; // optional
            }
        });
    }
}