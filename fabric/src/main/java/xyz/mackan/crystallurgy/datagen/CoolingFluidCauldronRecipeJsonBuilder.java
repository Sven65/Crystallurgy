package xyz.mackan.crystallurgy.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

import java.util.List;
import java.util.function.Consumer;

public class CoolingFluidCauldronRecipeJsonBuilder {
    private final List<Ingredient> ingredients;
    private final ItemStack result;
    private int ticks;
    private int coolingScore;

    public CoolingFluidCauldronRecipeJsonBuilder(List<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public static CoolingFluidCauldronRecipeJsonBuilder create(List<Ingredient> ingredients, ItemConvertible resultItem, int count) {
        return new CoolingFluidCauldronRecipeJsonBuilder(ingredients, new ItemStack(resultItem, count));
    }

    public CoolingFluidCauldronRecipeJsonBuilder ticks(int ticks) {
        this.ticks = ticks;
        return this;
    }

    public CoolingFluidCauldronRecipeJsonBuilder coolingScore(int coolingScore) {
        this.coolingScore = coolingScore;
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        exporter.accept(new RecipeJsonProvider() {
            @Override
            public void serialize(JsonObject json) {
                json.addProperty("type", "crystallurgy:cooling_fluid_cauldron_recipe");

                JsonArray ingredientsArray = new JsonArray();
                for (Ingredient ingredient : ingredients) {
                    ingredientsArray.add(ingredient.toJson());
                }
                json.add("ingredients", ingredientsArray);

                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("item", result.getItem().getRegistryEntry().registryKey().getValue().toString());
                if (result.getCount() > 1) {
                    resultObj.addProperty("count", result.getCount());
                }
                json.add("output", resultObj);
                json.addProperty("ticks", ticks);
                json.addProperty("cooling_score", coolingScore);
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