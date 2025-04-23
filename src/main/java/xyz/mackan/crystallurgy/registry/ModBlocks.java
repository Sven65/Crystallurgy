package xyz.mackan.crystallurgy.registry;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.CrystalFluidCauldron;
import xyz.mackan.crystallurgy.blocks.FluidSynthesizer;
import xyz.mackan.crystallurgy.blocks.ResonanceForge;

public class ModBlocks {
    public static final Block RESONANCE_FORGE = new ResonanceForge();
    public static final Block FLUID_SYNTHESIZER = new FluidSynthesizer();

    public static void register() {
        Registry.register(Registries.BLOCK, new Identifier(Crystallurgy.MOD_ID, "resonance_forge"), RESONANCE_FORGE);
        Registry.register(Registries.BLOCK, new Identifier(Crystallurgy.MOD_ID, "fluid_synthesizer"), FLUID_SYNTHESIZER);
    }
}
