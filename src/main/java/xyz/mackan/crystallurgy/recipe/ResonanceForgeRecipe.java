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
import java.util.List;

public class ResonanceForgeRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final List<Ingredient> recipeItems;

    private final int energyPerTick;
    private final int ticks;

    public ResonanceForgeRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, int energyPerTick, int ticks) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.energyPerTick = energyPerTick;
        this.ticks = ticks;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient()) {
            return false;
        }

        ItemStack catalyst = inventory.getStack(0);
        ItemStack rawMaterial = inventory.getStack(1);

        return recipeItems.get(0).test(catalyst) && recipeItems.get(1).test(rawMaterial);
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(this.recipeItems.size());
        list.addAll(recipeItems);
        return list;
    }

    public int getTicks () {
        return this.ticks;
    }

    public int getEnergyPerTick() {
        return this.energyPerTick;
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ResonanceForgeRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "resonance_forging";
    }

    public static class Serializer implements RecipeSerializer<ResonanceForgeRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "resonance_forging";

        @Override
        public ResonanceForgeRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));
            JsonObject energy = JsonHelper.getObject(json, "energy");

            int ticks = JsonHelper.getInt(energy, "ticks");
            int energyPerTick = JsonHelper.getInt(energy, "energy_per_tick");


            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(2, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new ResonanceForgeRecipe(id, output, inputs, energyPerTick, ticks);
        }

        @Override
        public ResonanceForgeRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();

            int ticks = buf.readInt();
            int energyPerTick = buf.readInt();

            return new ResonanceForgeRecipe(id, output, inputs, ticks, energyPerTick);
        }

        @Override
        public void write(PacketByteBuf buf, ResonanceForgeRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }

            buf.writeItemStack(recipe.getOutput(null));

            buf.writeInt(recipe.ticks);
            buf.writeInt(recipe.energyPerTick);
        }
    }

    @Override
    public String toString() {
        return String.format("[RECIPE] Ingredients: %s, Output: %s, Ticks: %s, Energy Per Tick: %s", this.getIngredients(), this.output, this.ticks, this.energyPerTick);
    }
}
