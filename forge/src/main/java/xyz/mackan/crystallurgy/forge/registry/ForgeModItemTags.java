package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraftforge.eventbus.api.IEventBus;
import xyz.mackan.crystallurgy.Constants;

public class ForgeModItemTags {
    public static final TagKey<Item> RESONATOR_CRYSTALS = TagKey.of(RegistryKeys.ITEM, Constants.id("resonator_crystals"));

    public static void register() {}
}
