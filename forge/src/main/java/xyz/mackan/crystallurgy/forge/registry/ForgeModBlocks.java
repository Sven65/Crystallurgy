package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.forge.block.FluidSynthesizerBlock;
import xyz.mackan.crystallurgy.forge.block.ResonanceForgeBlock;

public class ForgeModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    public static final RegistryObject<Block> RESONANCE_FORGE =
            BLOCKS.register("resonance_forge", ResonanceForgeBlock::new);

    public static final RegistryObject<Block> FLUID_SYNTHESIZER =
            BLOCKS.register("fluid_synthesizer", FluidSynthesizerBlock::new);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}