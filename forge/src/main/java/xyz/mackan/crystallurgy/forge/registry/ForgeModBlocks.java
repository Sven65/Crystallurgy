package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.registry.ModBlocks;

public class ForgeModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);
    public static void register(IEventBus modEventBus) {
        BLOCKS.register("resonance_forge", () -> ModBlocks.RESONANCE_FORGE);
        BLOCKS.register("fluid_synthesizer", () -> ModBlocks.FLUID_SYNTHESIZER);

        BLOCKS.register(modEventBus);
    }
}
