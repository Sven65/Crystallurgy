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
    public static final Item COAL_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item IRON_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item GOLD_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item DIAMOND_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item NETHERITE_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item LAPIS_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item EMERALD_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item QUARTZ_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item REDSTONE_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item CRYSTAL_SEED = new CrystalSeed();
    public static final Item DIAMOND_CRYSTAL_SEED = new CrystalSeed();

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "resonance_forge"), RESONANCE_FORGE);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "crystal_seed"), CRYSTAL_SEED);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "diamond_crystal_seed"), DIAMOND_CRYSTAL_SEED);

        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "coal_resonator_crystal"), COAL_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "iron_resonator_crystal"), IRON_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "gold_resonator_crystal"), GOLD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "diamond_resonator_crystal"), DIAMOND_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "netherite_resonator_crystal"), NETHERITE_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "lapis_resonator_crystal"), LAPIS_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "emerald_resonator_crystal"), EMERALD_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "quartz_resonator_crystal"), QUARTZ_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "redstone_resonator_crystal"), REDSTONE_RESONATOR_CRYSTAL);
    }

}
