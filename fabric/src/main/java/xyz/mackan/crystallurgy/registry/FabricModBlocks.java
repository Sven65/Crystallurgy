package xyz.mackan.crystallurgy.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import xyz.mackan.crystallurgy.Constants;

public class FabricModBlocks {
    public static void register() {
        Registry.register(Registries.BLOCK, Constants.id("resonance_forge"), ModBlocks.RESONANCE_FORGE);
        Registry.register(Registries.BLOCK, Constants.id("fluid_synthesizer"), ModBlocks.FLUID_SYNTHESIZER);
    }
}
