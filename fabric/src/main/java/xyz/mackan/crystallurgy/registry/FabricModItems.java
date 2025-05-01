package xyz.mackan.crystallurgy.registry;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import xyz.mackan.crystallurgy.Constants;

public class FabricModItems {
    public static final Item RESONANCE_FORGE = new BlockItem(FabricModBlocks.RESONANCE_FORGE, new Item.Settings());
    //public static final Item FLUID_SYNTHESIZER = new BlockItem(FabricModBlocks.FLUID_SYNTHESIZER, new Item.Settings());

    public static void register() {
        //Registry.register(Registries.ITEM, Constants.id("resonance_forge"), RESONANCE_FORGE);
        //Registry.register(Registries.ITEM, Constants.id("fluid_synthesizer"), FLUID_SYNTHESIZER);

        Registry.register(Registries.ITEM, Constants.id("crystal_seed"), ModItems.CRYSTAL_SEED);

        Registry.register(Registries.ITEM, Constants.id("crystal_seed_resonator_crystal"), ModItems.CRYSTAL_SEED_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("coal_resonator_crystal"), ModItems.COAL_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("iron_resonator_crystal"), ModItems.IRON_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("gold_resonator_crystal"), ModItems.GOLD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("diamond_resonator_crystal"), ModItems.DIAMOND_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("netherite_resonator_crystal"), ModItems.NETHERITE_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("lapis_resonator_crystal"), ModItems.LAPIS_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("emerald_resonator_crystal"), ModItems.EMERALD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("quartz_resonator_crystal"), ModItems.QUARTZ_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("redstone_resonator_crystal"), ModItems.REDSTONE_RESONATOR_CRYSTAL);

        Registry.register(Registries.ITEM, Constants.id("unrefined_crystal_seed_resonator_crystal"), ModItems.UNREFINED_CRYSTAL_SEED_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("unrefined_coal_resonator_crystal"), ModItems.UNREFINED_COAL_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("unrefined_iron_resonator_crystal"), ModItems.UNREFINED_IRON_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("unrefined_gold_resonator_crystal"), ModItems.UNREFINED_GOLD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("unrefined_diamond_resonator_crystal"), ModItems.UNREFINED_DIAMOND_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("unrefined_netherite_resonator_crystal"), ModItems.UNREFINED_NETHERITE_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("unrefined_lapis_resonator_crystal"), ModItems.UNREFINED_LAPIS_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("unrefined_emerald_resonator_crystal"), ModItems.UNREFINED_EMERALD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("unrefined_quartz_resonator_crystal"), ModItems.UNREFINED_QUARTZ_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, Constants.id("unrefined_redstone_resonator_crystal"), ModItems.UNREFINED_REDSTONE_RESONATOR_CRYSTAL);
    }
}
