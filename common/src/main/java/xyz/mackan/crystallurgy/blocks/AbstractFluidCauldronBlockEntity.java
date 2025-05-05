package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.registry.ModProperties;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

public class AbstractFluidCauldronBlockEntity extends BlockEntity implements ImplementedInventory {
    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public int maxProgress = 100;
    public int progress = 0;

    public AbstractFluidCauldronBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void addToInventory(ItemStack toAdd) {
        if (toAdd.isEmpty()) {
            return;
        }

        // Pass 1: Try to merge with existing stacks
        for (int i = 0; i < this.size(); i++) {
            ItemStack existing = inventory.get(i);
            // Check if stacks are compatible and there's space
            if (ItemStack.canCombine(toAdd, existing) && existing.getCount() < existing.getMaxCount()) {
                int space = existing.getMaxCount() - existing.getCount();
                int toMove = Math.min(toAdd.getCount(), space);

                if (toMove > 0) {
                    existing.increment(toMove); // Modify existing stack in place
                    toAdd.decrement(toMove);
                    markDirty(); // Use this.markDirty() after modification

                    if (toAdd.isEmpty()) {
                        return; // All items added
                    }
                }
            }
        }

        // Pass 2: Place into empty slots
        if (!toAdd.isEmpty()) {
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i).isEmpty()) {
                    int toMove = Math.min(toAdd.getCount(), getMaxCountPerStack()); // Use this.getMaxCountPerStack()
                    ItemStack stackToPlace = toAdd.split(toMove); // split() reduces toAdd and creates the new stack
                    if (!stackToPlace.isEmpty()) {
                        setStack(i, stackToPlace); // Use this.setStack(). setStack handles placing and calls markDirty.
                    }
                    if (toAdd.isEmpty()) {
                        markDirty();

                        return; // All items added
                    }
                }
            }
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markDirty() {
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        super.markDirty();
    }

    public void addItemToCauldron(ItemStack itemStack) {
        if (this.canAcceptItem(itemStack)) this.addToInventory(itemStack);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }


    public void addItemEntityToCauldron(ItemEntity itemEntity) {
        boolean canAccept = this.canAcceptItem(itemEntity.getStack());

        if (canAccept) {
            this.addToInventory(itemEntity.getStack());
            //itemEntity.setDespawnImmediately();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);

        nbt.putInt(String.format("%s.progress", Constants.MOD_ID), progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        inventory.clear();
        super.readNbt(nbt);

        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt(String.format("%s.progress", Constants.MOD_ID));
    }

    public void clearFluid(World world, BlockPos pos) {
        if (world.isClient()) {
            return;
        }
        BlockState state = world.getBlockState(pos);

        // Check if the block is a cauldron and it contains a fluid
        if (state.get(ModProperties.FLUID_LEVEL) > 0) {
            world.setBlockState(pos, state.getBlock().getDefaultState());
        }
    }

    public int getFluidProgress() {
        float normalizedProgress = (float) this.progress / this.maxProgress;

        // Only return 0 when progress is exactly at max
        if (normalizedProgress >= 0.99999f) {
            return 0;
        }

        // Otherwise, map the fluid level to 1-3 based on progress
        float fluidLevel = 1 - normalizedProgress;
        return Math.max(1, Math.min(3, (int)Math.ceil(fluidLevel * 3)));
    }

    public boolean hasFluid(World world) {
        return world.getBlockState(this.pos).get(ModProperties.FLUID_LEVEL) > 0;
    }

    public boolean canAcceptItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false; // Cannot accept an empty stack
        }

        // Get the underlying list of items using the ImplementedInventory method
        DefaultedList<ItemStack> items = this.getItems(); // Use this.getItems()

        // Check 1: Can the item stack merge with any existing stack that has space?
        boolean canCombineWithExisting = items.stream().anyMatch(existingStack ->
                // Use ItemStack.canCombine to check for matching items and existingStack.getCount()
                // to check if there's space by comparing to getMaxStackSize() of the existing stack.
                ItemStack.canCombine(itemStack, existingStack) && existingStack.getCount() < existingStack.getMaxCount()
        );

        // Check 2: Is there any empty slot in the inventory?
        boolean hasEmptySlot = items.stream().anyMatch(ItemStack::isEmpty);

        // The inventory can accept the item if it can combine with an existing stack OR there is an empty slot.
        return canCombineWithExisting || hasEmptySlot;
    }

    public Fluid getFluid() {
        return Fluids.WATER;
    }
}
