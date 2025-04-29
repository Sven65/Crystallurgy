package xyz.mackan.crystallurgy.util;

import net.minecraft.item.ItemStack;

public class CauldronUtil {
    public static <T extends ImplementedInventory> ItemStack getItemStack(T entity) {
        int size = entity.size();

        if (size > 1) {
            ItemStack stack1 = entity.getStack(1);
            if (!stack1.isEmpty()) {
                return stack1;
            }
        }

        if (size > 0) {
            return entity.getStack(0);
        }

        return ItemStack.EMPTY;
    }

}
