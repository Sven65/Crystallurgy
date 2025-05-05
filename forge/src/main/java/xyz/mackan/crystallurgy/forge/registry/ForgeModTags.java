package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import xyz.mackan.crystallurgy.Constants;

public class ForgeModTags {
    public static final TagKey<Item> RESONATOR_CRYSTALS = TagKey.of(RegistryKeys.ITEM, Constants.id("resonator_crystals"));
    public static final TagKey<Block> FLUID_CAULDRON_HEATERS = TagKey.of(RegistryKeys.BLOCK, Constants.id("fluid_cauldron_heaters"));

    public static void register() {}
}
