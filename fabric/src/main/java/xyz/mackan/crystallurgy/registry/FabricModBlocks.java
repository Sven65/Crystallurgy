package xyz.mackan.crystallurgy.registry;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.block.FluidSynthesizerBlock;
import xyz.mackan.crystallurgy.block.ResonanceForgeBlock;

public class FabricModBlocks {
    public static final Block RESONANCE_FORGE = new ResonanceForgeBlock();
   public static final Block FLUID_SYNTHESIZER = new FluidSynthesizerBlock();

    public static void register() {
        Registry.register(Registries.BLOCK, Constants.id("resonance_forge"), RESONANCE_FORGE);
        Registry.register(Registries.BLOCK, Constants.id("fluid_synthesizer"), FLUID_SYNTHESIZER);
    }
}
