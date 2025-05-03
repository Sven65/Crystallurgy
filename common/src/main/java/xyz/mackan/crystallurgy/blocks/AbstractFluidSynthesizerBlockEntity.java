package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.Optional;

public abstract class AbstractFluidSynthesizerBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    public static final int ENERGY_CAPACITY = 100000;
    public static final int MAX_ENERGY_EXTRACT = 20000;
    public static final int MAX_ENERGY_INSERT = 10000;

    public AbstractFluidSynthesizerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected static final int FLUID_INPUT_SLOT = 0;
    protected static final int FLUID_OUTPUT_SLOT = 1;
    protected static final int MATERIAL_0_SLOT = 2;
    protected static final int MATERIAL_1_SLOT = 3;

    protected PropertyDelegate propertyDelegate;

    private int maxProgress = 100;
    private int progress = 0;

    protected abstract void sendEnergyPacket();
    protected abstract void sendFluidPacket(String slot, Fluid fluid, int amount);

    public abstract void setEnergyLevel(long energyLevel);
    protected abstract <T extends AbstractFluidSynthesizerBlockEntity> void extractEnergy(T entity, long amount);
    protected abstract <T extends AbstractFluidSynthesizerBlockEntity> boolean hasEnoughEnergy(T entity);

    protected abstract void setInputFluidLevel(Fluid fluid, long fluidLevel);
    protected abstract <T extends AbstractFluidSynthesizerBlockEntity> void extractInputFluid(T entity, long amount);
    protected abstract <T extends AbstractFluidSynthesizerBlockEntity> boolean hasEnoughInputFluid(T entity);

    protected abstract void setOutputFluidLevel(Fluid fluid, long fluidLevel);
    protected abstract <T extends AbstractFluidSynthesizerBlockEntity> void extractOutputFluid(T entity, long amount);

    protected abstract <T extends AbstractFluidSynthesizerBlockEntity> void transferFluidToInputStorage(T entity);
    protected abstract <T extends AbstractFluidSynthesizerBlockEntity> void transferFluidFromOutputStorage(T entity);

    protected abstract void craftFluid();

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 100;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt(String.format("%s.progress", Constants.MOD_ID), progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt(String.format("%s.progress", Constants.MOD_ID));
    }

    private boolean hasEnoughItems(int slot) {
        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) return false;
        int count = recipe.get().getCount(slot);
        ItemStack stackInSlot = this.getStack(slot);
        int slotCount = stackInSlot.isEmpty() ? 64 : stackInSlot.getCount();

        return slotCount >= count;
    }

    protected boolean hasRecipe(AbstractFluidSynthesizerBlockEntity entity) {
        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        return recipe.isPresent()
                && hasEnoughItems(MATERIAL_0_SLOT)
                && hasEnoughItems(MATERIAL_1_SLOT)
                && canInsertFluidIntoOutputSlot(recipe.get().getOutputFluid(), recipe.get().getOutputFluidAmount());
    }

    protected abstract boolean canInsertFluidIntoOutputSlot(Fluid fluidOutput, int fluidOutputAmount);

    protected abstract Optional<FluidSynthesizerRecipe> getCurrentRecipe();

    private boolean hasFluidSourceInSlot(AbstractFluidSynthesizerBlockEntity entity) {
        return entity.getStack(FLUID_INPUT_SLOT).getItem() == Items.WATER_BUCKET; // TODO: Check if is fluid bucket.
    }

    private boolean hasBucketInOutputSlot(AbstractFluidSynthesizerBlockEntity entity) {
        return entity.getStack(FLUID_OUTPUT_SLOT).getItem() == Items.BUCKET;
    }

    protected abstract void onCrafingFinished(AbstractFluidSynthesizerBlockEntity entity, Optional<FluidSynthesizerRecipe> recipe);

    public void tick(World world, BlockPos pos, BlockState state, AbstractFluidSynthesizerBlockEntity entity) {
        if (world.isClient()) {
            return;
        }

        if (hasFluidSourceInSlot(entity)) {
            transferFluidToInputStorage(entity);
        }

        if (hasBucketInOutputSlot(entity)) {
            transferFluidFromOutputStorage(entity);
        }

        if (hasRecipe(entity) && hasEnoughEnergy(entity) && hasEnoughInputFluid(entity)) {
            Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

            int recipeTicks = recipe.get().getTicks();
            this.propertyDelegate.set(1, recipeTicks);

            entity.progress++;

            extractEnergy(entity, recipe.get().getEnergyPerTick());
            markDirty();

            if (hasCraftingFinished()) {
                this.craftFluid();

                this.onCrafingFinished(entity, recipe);

                this.resetProgress();
                markDirty();
            }
        } else {
            this.resetProgress();
        }
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }


}
