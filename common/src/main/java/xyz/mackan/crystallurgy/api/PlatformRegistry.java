package xyz.mackan.crystallurgy.api;

import net.minecraft.item.Item;

public class PlatformRegistry {
    public static void registerItem(String id, Item item) {
        PlatformRegistryImpl.registerItem(id, item); // resolves per-loader
    }
}