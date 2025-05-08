package xyz.mackan.crystallurgy.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import xyz.mackan.crystallurgy.CrystallurgyCommon;

public class StorageUtil {
    public static SingleVariantStorage<ItemVariant> createItemStorage(DefaultedList<ItemStack> inventory, int slot, Runnable finalCommit) {
        return new SingleVariantStorage<ItemVariant>() {
            @Override
            protected ItemVariant getBlankVariant() {
                return ItemVariant.blank();
            }

            @Override
            protected long getCapacity(ItemVariant itemVariant) {
                return 0;
            }

            @Override
            public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
                ItemStack stackInSlot = inventory.get(slot);

                if (!stackInSlot.isEmpty() && !ItemVariant.of(stackInSlot).equals(variant)) {
                    return 0;
                }

                int inserted = (int) Math.min(maxAmount, inventory.get(slot).getMaxCount() - stackInSlot.getCount());
                if (inserted > 0) {
                    updateSnapshots(transaction);
                    if (variant.isBlank()) {
                        variant = insertedVariant;
                    }

                    inventory.set(slot, variant.toStack(stackInSlot.getCount() + inserted));
                }

                CrystallurgyCommon.LOGGER.info("Inserted: {}", inserted);

                return inserted;
            }

            @Override
            protected void onFinalCommit() {
                finalCommit.run();
                super.onFinalCommit();
            }
        };
    }
}
