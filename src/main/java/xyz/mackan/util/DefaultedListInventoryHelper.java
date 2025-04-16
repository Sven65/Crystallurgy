package xyz.mackan.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class DefaultedListInventoryHelper {
    public static Inventory asInventory(DefaultedList<ItemStack> list) {
        return new Inventory() {
            @Override
            public int size() {
                return list.size();
            }

            @Override
            public boolean isEmpty() {
                for (ItemStack stack : list) {
                    if (!stack.isEmpty()) return false;
                }
                return true;
            }

            @Override
            public ItemStack getStack(int slot) {
                return list.get(slot);
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                ItemStack result = list.get(slot).split(amount);
                return result;
            }

            @Override
            public ItemStack removeStack(int slot) {
                ItemStack result = list.get(slot);
                list.set(slot, ItemStack.EMPTY);
                return result;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                list.set(slot, stack);
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return true;
            }

            @Override
            public void clear() {
                list.clear();
            }

            public void markDirty() {
                // No-op for now
            }
        };
    }
}