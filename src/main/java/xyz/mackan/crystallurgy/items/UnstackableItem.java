package xyz.mackan.crystallurgy.items;

import net.minecraft.item.Item;

public class UnstackableItem extends Item {
    public UnstackableItem() {
        super(new Item.Settings().maxCount(1));
    }
}
