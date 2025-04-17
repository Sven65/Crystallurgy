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
    public static final Item DIAMOND_RESONATOR_CRYSTAL = new CatalystCrystalItem(64);
    public static final Item CRYSTAL_SEED = new CrystalSeed();

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "resonance_forge"), RESONANCE_FORGE);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "diamond_resonator_crystal"), DIAMOND_RESONATOR_CRYSTAL);
        Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "crystal_seed"), CRYSTAL_SEED);
    }

}
