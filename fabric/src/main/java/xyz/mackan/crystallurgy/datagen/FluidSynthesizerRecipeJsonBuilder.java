package xyz.mackan.crystallurgy.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.util.FluidStack;

import java.util.List;
import java.util.function.Consumer;

public class FluidSynthesizerRecipeJsonBuilder {
    private final List<Ingredient> ingredients;
    private int energyPerTick;
    private int ticks;
    private FluidStack inputFluid;
    private FluidStack outputFluid;

    public FluidSynthesizerRecipeJsonBuilder(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public static FluidSynthesizerRecipeJsonBuilder create(List<Ingredient> ingredients) {
        return new FluidSynthesizerRecipeJsonBuilder(ingredients);
    }

    public FluidSynthesizerRecipeJsonBuilder energyPerTick(int energyPerTick) {
        this.energyPerTick = energyPerTick;
        return this;
    }

    public FluidSynthesizerRecipeJsonBuilder ticks(int ticks) {
        this.ticks = ticks;
        return this;
    }

    public FluidSynthesizerRecipeJsonBuilder inputFluid(FluidStack stack) {
        this.inputFluid = stack;
        return this;
    }

    public FluidSynthesizerRecipeJsonBuilder outputFluid(FluidStack stack) {
        this.outputFluid = stack;
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        exporter.accept(new RecipeJsonProvider() {
            @Override
            public void serialize(JsonObject json) {
                json.addProperty("type", "crystallurgy:fluid_synthesizer");

                JsonArray ingredientsArray = new JsonArray();
                for (Ingredient ingredient : ingredients) {
                    if (ingredient.isEmpty()) continue;
                    JsonObject ingredientJson = ingredient.toJson().getAsJsonObject();
                    ItemStack[] matching = ingredient.getMatchingStacks();

                    if (matching[0].getCount() > 0) {
                        ingredientJson.addProperty("count", matching[0].getCount());
                    }

                    ingredientsArray.add(ingredientJson);
                }
                json.add("ingredients", ingredientsArray);
                json.add("inputFluid", inputFluid.toJson());

                JsonObject resultObj = new JsonObject();
                resultObj.add("fluid", outputFluid.toJson());

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