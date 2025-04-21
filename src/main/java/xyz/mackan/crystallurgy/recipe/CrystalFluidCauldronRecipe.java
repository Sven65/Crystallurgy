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

public class CrystalFluidCauldronRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final List<Ingredient> recipeItems;

    private final int ticks;

    public CrystalFluidCauldronRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, int ticks) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.ticks = ticks;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient()) {
            return false;
        }

        ItemStack first = inventory.getStack(0);
        ItemStack second = inventory.getStack(1);

        Ingredient firstIngredient = recipeItems.get(0);
        Ingredient secondIngredient = recipeItems.get(1);

        // Check both possible orderings
        return (firstIngredient.test(first) && secondIngredient.test(second)) ||
                (firstIngredient.test(second) && secondIngredient.test(first));
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
        return CrystalFluidCauldronRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return CrystalFluidCauldronRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<CrystalFluidCauldronRecipe> {
        private Type() {}
        public static final CrystalFluidCauldronRecipe.Type INSTANCE = new CrystalFluidCauldronRecipe.Type();
        public static final String ID = "crystal_fluid_cauldron_recipe";
    }

    public static class Serializer implements RecipeSerializer<CrystalFluidCauldronRecipe> {
        public static final CrystalFluidCauldronRecipe.Serializer INSTANCE = new CrystalFluidCauldronRecipe.Serializer();
        public static final String ID = "crystal_fluid_cauldron_recipe";

        @Override
        public CrystalFluidCauldronRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(2, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            int ticks = JsonHelper.getInt(json, "ticks");

            return new CrystalFluidCauldronRecipe(id, output, inputs, ticks);
        }

        @Override
        public CrystalFluidCauldronRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();

            int ticks = buf.readInt();

            return new CrystalFluidCauldronRecipe(id, output, inputs, ticks);
        }

        @Override
        public void write(PacketByteBuf buf, CrystalFluidCauldronRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }

            buf.writeItemStack(recipe.getOutput(null));

            buf.writeInt(recipe.ticks);
        }
    }

    @Override
    public String toString() {
        return String.format("[Crystal Fluid Recipe] Ingredients: %s, Output: %s, Ticks: %s", this.getIngredients(), this.output, this.ticks);
    }
}
