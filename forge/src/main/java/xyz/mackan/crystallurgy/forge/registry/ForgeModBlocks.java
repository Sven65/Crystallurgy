package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.forge.block.ResonanceForgeBlock;

public class ForgeModBlocks {
    // 1) Create a DeferredRegister tied to your mod ID
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    // 2) Use RegistryObject to register *and* lazily instantiate your block
    public static final RegistryObject<Block> RESONANCE_FORGE =
            BLOCKS.register("resonance_forge", ResonanceForgeBlock::new);

    // 3) Hook the DeferredRegister into the mod event bus
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}