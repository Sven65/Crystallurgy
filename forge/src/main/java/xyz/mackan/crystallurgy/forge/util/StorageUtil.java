package xyz.mackan.crystallurgy.forge.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraftforge.items.ItemStackHandler;

public class StorageUtil {
    public static ItemStackHandler createItemStorage(int count, DefaultedList<ItemStack> inventory, int inventorySlot, Runnable finalCommit) {
        return new ItemStackHandler(count) {
            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.get(inventorySlot);
            }

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                inventory.set(inventorySlot, stack);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                ItemStack stackInSlot = inventory.get(inventorySlot);

                if (!stackInSlot.isEmpty() && !ItemStack.canCombine(stack, stackInSlot)) {
                    return stack;
                }

                int space = stack.getMaxCount() - stackInSlot.getCount();
                int toInsert = Math.min(stack.getCount(), space);

                if (toInsert <= 0) {
                    return stack;
                }

                if (!simulate) {
                    if (stackInSlot.isEmpty()) {
                        ItemStack newStack = stack.copy();
                        newStack.setCount(toInsert);
                        inventory.set(inventorySlot, newStack);
                    } else {
                        stackInSlot.increment(toInsert);
                        inventory.set(inventorySlot, stackInSlot);
                    }
                }

                ItemStack remainder = stack.copy();
                remainder.decrement(toInsert);

                return remainder;
            }

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(inventorySlot);
                finalCommit.run();
            }
        };
    }
}
