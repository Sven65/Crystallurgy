package xyz.mackan.crystallurgy.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.Crystallurgy;

import java.util.List;

public class CoolingFluidCauldronRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final List<Ingredient> recipeItems;

    private final int ticks;
    private final int coolingScore;

    public CoolingFluidCauldronRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, int ticks, int coolingScore) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.ticks = ticks;
        this.coolingScore = coolingScore;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient()) {
            return false;
        }


        ItemStack first = inventory.getStack(0);

        if (first == null) {
            Crystallurgy.LOGGER.warn("First item in cooling recipe is null?");
        }

        Ingredient firstIngredient = recipeItems.get(0);

        return firstIngredient.test(first);
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(this.recipeItems.size());
        list.addAll(recipeItems);
        return list;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    public int getTicks () {
        return this.ticks;
    }

    public int getCoolingScore() {
        return this.coolingScore;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CoolingFluidCauldronRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return CoolingFluidCauldronRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<CoolingFluidCauldronRecipe> {
        private Type() {}
        public static final CoolingFluidCauldronRecipe.Type INSTANCE = new CoolingFluidCauldronRecipe.Type();
        public static final String ID = "cooling_fluid_cauldron_recipe";
    }

    public static class Serializer implements RecipeSerializer<CoolingFluidCauldronRecipe> {
        public static final CoolingFluidCauldronRecipe.Serializer INSTANCE = new CoolingFluidCauldronRecipe.Serializer();
        public static final String ID = "cooling_fluid_cauldron_recipe";

        @Override
        public CoolingFluidCauldronRecipe read(Identifier id, JsonObject json) {
            Crystallurgy.LOGGER.info("Reading cooling recipe");

            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(1, Ingredient.EMPTY);

            Crystallurgy.LOGGER.info("Read cooling ingredients {}", ingredients);


            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            Crystallurgy.LOGGER.info("Made cooling inputs {}", inputs);


            int ticks = JsonHelper.getInt(json, "ticks");
            int coolingScore = JsonHelper.getInt(json, "cooling_score");


            return new CoolingFluidCauldronRecipe(id, output, inputs, ticks, coolingScore);
        }

        @Override
        public CoolingFluidCauldronRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();

            int ticks = buf.readInt();
            int coolingScore = buf.readInt();

            return new CoolingFluidCauldronRecipe(id, output, inputs, ticks, coolingScore);
        }

        @Override
        public void write(PacketByteBuf buf, CoolingFluidCauldronRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }

            buf.writeItemStack(recipe.getOutput(null));

            buf.writeInt(recipe.ticks);
            buf.writeInt(recipe.coolingScore);
        }
    }

    @Override
    public String toString() {
        return String.format("[Cooling Fluid Recipe] Ingredients: %s, Output: %s, Ticks: %s, Cooling Score: %s", this.getIngredients(), this.output, this.ticks, this.coolingScore);
    }
}
