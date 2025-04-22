package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.items.CatalystCrystalItem;
import xyz.mackan.crystallurgy.items.CrystalSeed;

public class ModItems {
    public static final Item RESONANCE_FORGE = new BlockItem(ModBlocks.RESONANCE_FORGE, new FabricItemSettings());

    public static final Item CRYSTAL_SEED = new CrystalSeed();

    public static final Item CRYSTAL_SEED_RESONATOR      = new CatalystCrystalItem(1);
    public static final Item COAL_RESONATOR_CRYSTAL      = new CatalystCrystalItem(64);
    public static final Item IRON_RESONATOR_CRYSTAL      = new CatalystCrystalItem(64);
    public static final Item GOLD_RESONATOR_CRYSTAL      = new CatalystCrystalItem(64);
    public static final Item DIAMOND_RESONATOR_CRYSTAL   = new CatalystCrystalItem(64);
    public static final Item NETHERITE_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item LAPIS_RESONATOR_CRYSTAL     = new CatalystCrystalItem(64);
    public static final Item EMERALD_RESONATOR_CRYSTAL   = new CatalystCrystalItem(64);
    public static final Item QUARTZ_RESONATOR_CRYSTAL    = new CatalystCrystalItem(64);
    public static final Item REDSTONE_RESONATOR_CRYSTAL  = new CatalystCrystalItem(64);

    public static final Item UNREFINED_CRYSTAL_SEED_RESONATOR_CRYSTAL = new CatalystCrystalItem(1);
    public static final Item UNREFINED_COAL_RESONATOR_CRYSTAL         = new CatalystCrystalItem(1);
    public static final Item UNREFINED_IRON_RESONATOR_CRYSTAL         = new CatalystCrystalItem(1);
    public static final Item UNREFINED_GOLD_RESONATOR_CRYSTAL         = new CatalystCrystalItem(1);
    public static final Item UNREFINED_DIAMOND_RESONATOR_CRYSTAL      = new CatalystCrystalItem(1);
    public static final Item UNREFINED_NETHERITE_RESONATOR_CRYSTAL    = new CatalystCrystalItem(1);
    public static final Item UNREFINED_LAPIS_RESONATOR_CRYSTAL        = new CatalystCrystalItem(1);
    public static final Item UNREFINED_EMERALD_RESONATOR_CRYSTAL      = new CatalystCrystalItem(1);
    public static final Item UNREFINED_QUARTZ_RESONATOR_CRYSTAL       = new CatalystCrystalItem(1);
    public static final Item UNREFINED_REDSTONE_RESONATOR_CRYSTAL     = new CatalystCrystalItem(1);




    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "resonance_forge"), RESONANCE_FORGE);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "crystal_seed"), CRYSTAL_SEED);

        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "crystal_seed_resonator_crystal"), CRYSTAL_SEED_RESONATOR);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "coal_resonator_crystal"), COAL_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "iron_resonator_crystal"), IRON_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "gold_resonator_crystal"), GOLD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "diamond_resonator_crystal"), DIAMOND_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "netherite_resonator_crystal"), NETHERITE_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "lapis_resonator_crystal"), LAPIS_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "emerald_resonator_crystal"), EMERALD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "quartz_resonator_crystal"), QUARTZ_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "redstone_resonator_crystal"), REDSTONE_RESONATOR_CRYSTAL);

        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_crystal_seed_resonator_crystal"), UNREFINED_CRYSTAL_SEED_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_coal_resonator_crystal"), UNREFINED_COAL_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_iron_resonator_crystal"), UNREFINED_IRON_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_gold_resonator_crystal"), UNREFINED_GOLD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_diamond_resonator_crystal"), UNREFINED_DIAMOND_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_netherite_resonator_crystal"), UNREFINED_NETHERITE_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_lapis_resonator_crystal"), UNREFINED_LAPIS_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_emerald_resonator_crystal"), UNREFINED_EMERALD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_quartz_resonator_crystal"), UNREFINED_QUARTZ_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "unrefined_redstone_resonator_crystal"), UNREFINED_REDSTONE_RESONATOR_CRYSTAL);
    }

}
