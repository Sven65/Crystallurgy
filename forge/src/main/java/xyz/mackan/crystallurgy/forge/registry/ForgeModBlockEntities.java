package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.forge.block.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.forge.block.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.forge.block.ResonanceForgeBlockEntity;

public class ForgeModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<ResonanceForgeBlockEntity>> RESONANCE_FORGE = BLOCK_ENTITIES.register("resonance_forge", () ->
            BlockEntityType.Builder.create(ResonanceForgeBlockEntity::new, ForgeModBlocks.RESONANCE_FORGE.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluidSynthesizerBlockEntity>> FLUID_SYNTHESIZER = BLOCK_ENTITIES.register("fluid_synthesizer", () ->
            BlockEntityType.Builder.create(FluidSynthesizerBlockEntity::new, ForgeModBlocks.FLUID_SYNTHESIZER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CrystalFluidCauldronBlockEntity>> CRYSTAL_FLUID_CAULDRON = BLOCK_ENTITIES.register("crystal_fluid_cauldron_entity", () ->
            BlockEntityType.Builder.create(CrystalFluidCauldronBlockEntity::new, ForgeModCauldron.CRYSTAL_CAULDRON.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
