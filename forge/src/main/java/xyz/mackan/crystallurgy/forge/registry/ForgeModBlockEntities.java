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
import xyz.mackan.crystallurgy.forge.block.ResonanceForgeBlockEntity;

public class ForgeModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<ResonanceForgeBlockEntity>> RESONANCE_FORGE = BLOCK_ENTITIES.register("resonance_forge", () ->
            BlockEntityType.Builder.create(ResonanceForgeBlockEntity::new, ForgeModBlocks.RESONANCE_FORGE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        //EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, RESONANCE_FORGE);
    }
}
