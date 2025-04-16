package xyz.mackan.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import xyz.mackan.Crystallurgy;
import xyz.mackan.blocks.ResonanceForge;

public class ModBlocks {
    public static final Block RESONANCE_FORGE = new ResonanceForge();

    public static void register() {
        Registry.register(Registries.BLOCK, new Identifier(Crystallurgy.MOD_ID, "resonance_forge"), RESONANCE_FORGE);
    }
}
