package xyz.mackan.items;

import net.minecraft.item.Item;

public class CatalystCrystalItem extends Item {
    public CatalystCrystalItem(int durability) {
        super(new Item.Settings().maxCount(1).maxDamage(durability));
    }
}
