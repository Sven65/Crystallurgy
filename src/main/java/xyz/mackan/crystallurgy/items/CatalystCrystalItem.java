package xyz.mackan.crystallurgy.items;

import net.minecraft.item.Item;

public class CatalystCrystalItem extends Item {
    public CatalystCrystalItem(int durability) {
        super(new Item.Settings().maxCount(1).maxDamage(durability));
    }

    @Override
    public boolean isDamageable() {
        return true;
    }
}
