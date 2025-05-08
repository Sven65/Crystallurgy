package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.Optional;

public abstract class AbstractResonanceForgeBlockEntity extends BlockEntity implements ImplementedInventory {
    protected final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    public static final int ENERGY_CAPACITY = 100000;
    public static final int MAX_ENERGY_EXTRACT = 20000;
    public static final int MAX_ENERGY_INSERT = 10000;


    public AbstractResonanceForgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected static final int CATALYST_SLOT = 0;
    protected static final int RAW_MATERIAL_SLOT = 1;
    protected static final int DYE_SLOT = 2;
    protected static final int OUTPUT_SLOT = 3;

    protected PropertyDelegate propertyDelegate;

    protected int maxProgress = 100;
    protected int progress = 0;

    protected abstract void sendEnergyPacket();

    protected abstract void setEnergyLevel(long energyLevel);

    protected abstract <T extends AbstractResonanceForgeBlockEntity> void extractEnergy(T entity, long amount);

    protected abstract <T extends AbstractResonanceForgeBlockEntity> boolean hasEnoughEnergy(T entity);

//    @Override
//    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
//        Direction localDir = this.getWorld().getBlockState(this.pos).get(AbstractResonanceForge.FACING);
//
//        if (side == Direction.UP || side == Direction.DOWN) {
//            return false;
//        }
//
//        // Insert top 1
//        // Right insert 1
//        // Left insert 0
//
//        // TODO: Check item tag on slot 0 to only allow crystals
//        // TODO: Make this work with slot 2
//
//        return switch (localDir) {
//            case EAST ->
//                    side.rotateYClockwise() == Direction.NORTH && slot == 1 ||
//                            side.rotateYClockwise() == Direction.EAST && slot == 1 ||
//                            side.rotateYClockwise() == Direction.WEST && slot == 0;
//            case SOUTH ->
//                    side == Direction.NORTH && slot == 1 ||
//                            side == Direction.EAST && slot == 1 ||
//                            side == Direction.WEST && slot == 0;
//            case WEST ->
//                    side.rotateYCounterclockwise() == Direction.NORTH && slot == 1 ||
//                            side.rotateYCounterclockwise() == Direction.EAST && slot == 1 ||
//                            side.rotateYCounterclockwise() == Direction.WEST && slot == 0;
//            default ->
//                    side.getOpposite() == Direction.NORTH && slot == 1 ||
//                            side.getOpposite() == Direction.EAST && slot == 1 ||
//                            side.getOpposite() == Direction.WEST && slot == 0;
//        };
//    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        Direction localDir = this.getWorld().getBlockState(this.pos).get(AbstractResonanceForge.FACING);

        if(side == Direction.UP) {
            return false;
        }

        // Down extract 3
        if(side == Direction.DOWN) {
            return slot == 3;
        }

        // bottom extract 3
        // right extract 3
        return switch (localDir) {
            case EAST -> side.rotateYClockwise() == Direction.SOUTH && slot == 3 ||
                    side.rotateYClockwise() == Direction.EAST && slot == 3;
            case SOUTH -> side == Direction.SOUTH && slot == 3 ||
                    side == Direction.EAST && slot == 3;
            case WEST -> side.rotateYCounterclockwise() == Direction.SOUTH && slot == 3 ||
                    side.rotateYCounterclockwise() == Direction.EAST && slot == 3;
            default -> side.getOpposite() == Direction.SOUTH && slot == 3 ||
                    side.getOpposite() == Direction.EAST && slot == 3;
        };
    }

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

    public void tick(World world, BlockPos pos, BlockState state, AbstractResonanceForgeBlockEntity entity) {
        if (world.isClient()) {
            return;
        }

        if (isOutputSlotEmptyOrReceivable()) {
            if (this.hasRecipe(entity) & hasEnoughEnergy(entity)) {
                Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();

                int recipeTicks = recipe.get().getTicks();

                this.propertyDelegate.set(1, recipeTicks);

                progress++;
                extractEnergy(entity, recipe.get().getEnergyPerTick());
                markDirty(world, pos, state);

                if (hasCraftingFinished()) {
                    this.craftItem();
                    this.resetProgress();
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            markDirty(world, pos, state);
        }
    }

    private void craftItem() {
        Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();
        ItemStack catalystStack = this.getStack(CATALYST_SLOT);

        if(catalystStack.getDamage() <= catalystStack.getMaxDamage()) {
            catalystStack.setDamage(catalystStack.getDamage() + 1);
        }

        if (catalystStack.getMaxDamage() == catalystStack.getDamage()) {
            this.removeStack(CATALYST_SLOT, 1);
        }

        this.removeStack(RAW_MATERIAL_SLOT, recipe.get().getCount(RAW_MATERIAL_SLOT));

        if (!this.getStack(DYE_SLOT).isEmpty()) {
            this.removeStack(DYE_SLOT, recipe.get().getCount(DYE_SLOT));
        }

        this.setStack(OUTPUT_SLOT,
                new ItemStack(
                        recipe.get().getOutput(null).getItem(),
                        getStack(OUTPUT_SLOT).getCount() + recipe.get().getOutput(null).getCount()
                )
        );
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private boolean hasEnoughItems(int slot) {
        Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) return false;
        int count = recipe.get().getCount(slot);
        ItemStack stackInSlot = this.getStack(slot);
        int slotCount = stackInSlot.isEmpty() ? 64 : stackInSlot.getCount();

        return slotCount >= count;
    }

    protected boolean hasRecipe(AbstractResonanceForgeBlockEntity entity) {
        Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();

        return recipe.isPresent()
                && hasEnoughItems(RAW_MATERIAL_SLOT)
                && hasEnoughItems(DYE_SLOT)
                && canInsertAmountIntoOutputSlot(recipe.get().getOutput(null))
                && canInsertItemIntoOutputSlot(recipe.get().getOutput(null).getItem());
    }

    protected Optional<ResonanceForgeRecipe> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());

        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }

        return getWorld().getRecipeManager().getFirstMatch(ResonanceForgeRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }
}
