package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import team.reborn.energy.api.EnergyStorage;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.block.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.block.ResonanceForgeBlockEntity;

public class FabricModBlockEntities {
    public static BlockEntityType<ResonanceForgeBlockEntity> RESONANCE_FORGE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Constants.id("resonance_forge"),
            FabricBlockEntityTypeBuilder.create(ResonanceForgeBlockEntity::new,
                    FabricModBlocks.RESONANCE_FORGE).build()
    );

    public static BlockEntityType<FluidSynthesizerBlockEntity> FLUID_SYNTHESIZER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Constants.id("fluid_synthesizer"),
            FabricBlockEntityTypeBuilder.create(FluidSynthesizerBlockEntity::new,
                    FabricModBlocks.FLUID_SYNTHESIZER).build()
    );

    public static void register() {
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, RESONANCE_FORGE);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, FLUID_SYNTHESIZER);

        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.inputFluidStorage, FLUID_SYNTHESIZER);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.outputFluidStorage, FLUID_SYNTHESIZER);
    }
}
