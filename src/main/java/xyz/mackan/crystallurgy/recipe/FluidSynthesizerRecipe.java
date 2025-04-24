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
import xyz.mackan.crystallurgy.util.FluidStack;

import java.util.List;

public class FluidSynthesizerRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final List<Ingredient> recipeItems;
    private final List<Integer> recipeItemCount;

    private final int energyPerTick;
    private final int ticks;

    private final FluidStack inputFluid;
    private final FluidStack outputFluid;

    public FluidSynthesizerRecipe(Identifier id, DefaultedList<Ingredient> recipeItems,
                                  DefaultedList<Integer> recipeItemCount, int energyPerTick, int ticks,
                                  FluidStack inputFluid, FluidStack outputFluid) {
        this.id = id;
        this.recipeItems = recipeItems;
        this.energyPerTick = energyPerTick;
        this.ticks = ticks;
        this.recipeItemCount = recipeItemCount;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
    }


    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient()) {
            return false;
        }

        ItemStack inputSlot1 = inventory.getStack(2);
        ItemStack inputSlot2 = inventory.getStack(3);

        return recipeItems.get(0).test(inputSlot1) && recipeItems.get(1).test(inputSlot2);
    }

    public boolean matchFluid(World world, FluidStack checkFluid) {
        // inputFluid is from the recipe.
        if (world.isClient()) {
            return false;
        }

        // Check fluid match
        if (!this.inputFluid.fluidVariant.equals(checkFluid.fluidVariant)) {
            return false;
        }


        if (inputFluid.getAmount() <= checkFluid.getAmount()) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    public FluidStack getInputFluid() {
        return this.inputFluid;
    }

    public FluidStack getOutputFluid() {
        return this.outputFluid;
    }

    public int getTicks () {
        return this.ticks;
    }

    public int getEnergyPerTick() {
        return this.energyPerTick;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FluidSynthesizerRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return FluidSynthesizerRecipe.Type.INSTANCE;
    }

    public int getCount(int slot) {
        if (slot >= this.recipeItemCount.size()) return 0;
        return this.recipeItemCount.get(slot);
    }

    public static class Type implements RecipeType<FluidSynthesizerRecipe> {
        private Type() {}
        public static final FluidSynthesizerRecipe.Type INSTANCE = new FluidSynthesizerRecipe.Type();
        public static final String ID = "fluid_synthesizer";
    }

    public static class Serializer implements RecipeSerializer<FluidSynthesizerRecipe> {
        public static final FluidSynthesizerRecipe.Serializer INSTANCE = new FluidSynthesizerRecipe.Serializer();
        public static final String ID = "fluid_synthesizer";

        @Override
        public FluidSynthesizerRecipe read(Identifier id, JsonObject json) {
            JsonObject energy = JsonHelper.getObject(json, "energy");

            int ticks = JsonHelper.getInt(energy, "ticks");
            int energyPerTick = JsonHelper.getInt(energy, "energy_per_tick");


            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(2, Ingredient.EMPTY);
            DefaultedList<Integer> countInputs = DefaultedList.ofSize(2, 1);
            JsonObject inputFluid = JsonHelper.getObject(json, "inputFluid");
            JsonObject outputFluid = JsonHelper.getObject(json, "output").getAsJsonObject("fluid");


            for (int i = 0; i < ingredients.size(); i++) {
                int count = ingredients.get(i).getAsJsonObject().get("count").getAsInt();

                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
                countInputs.set(i, count);
            }

            return new FluidSynthesizerRecipe(id, inputs, countInputs, energyPerTick, ticks, FluidStack.fromJson(inputFluid), FluidStack.fromJson(outputFluid));
        }

        @Override
        public FluidSynthesizerRecipe read(Identifier id, PacketByteBuf buf) {
            int size = buf.readInt();

            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(size, Ingredient.EMPTY);
            DefaultedList<Integer> inputCount = DefaultedList.ofSize(size, 1);

            for (int i = 0; i < inputs.size(); i++) {
                Ingredient ingredient = Ingredient.fromPacket(buf);

                inputs.set(i, ingredient);
            }

            int ticks = buf.readInt();
            int energyPerTick = buf.readInt();

            FluidStack inputFluid = FluidStack.fromPacket(buf);
            FluidStack outputFluid = FluidStack.fromPacket(buf);

            // TODO: Read fluid

            return new FluidSynthesizerRecipe(id, inputs, inputCount, energyPerTick, ticks, inputFluid, outputFluid);
        }

        @Override
        public void write(PacketByteBuf buf, FluidSynthesizerRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }

            buf.writeItemStack(recipe.getOutput(null));

            buf.writeInt(recipe.ticks);
            buf.writeInt(recipe.energyPerTick);
            recipe.inputFluid.writePacket(buf);
            recipe.outputFluid.writePacket(buf);
            // TODO: Write fluid
        }
    }

    @Override
    public String toString() {
        return String.format("[FluidSynthesizerRecipe] Ingredients: %s, Output: %s, Ticks: %s, Energy Per Tick: %s", this.getIngredients(), this.outputFluid, this.ticks, this.energyPerTick);
    }
}
