package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.*;

public class ModBlockEntities {
    public static BlockEntityType<ResonanceForgeBlockEntity> RESONANCE_FORGE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(Crystallurgy.MOD_ID, "resonance_forge"),
            FabricBlockEntityTypeBuilder.create(ResonanceForgeBlockEntity::new,
                    ModBlocks.RESONANCE_FORGE).build()
    );

    public static BlockEntityType<FluidSynthesizerBlockEntity> FLUID_SYNTHESIZER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(Crystallurgy.MOD_ID, "fluid_synthesizer"),
            FabricBlockEntityTypeBuilder.create(FluidSynthesizerBlockEntity::new,
                    ModBlocks.FLUID_SYNTHESIZER).build()
    );

    public static BlockEntityType<CrystalFluidCauldronBlockEntity> CRYSTAL_FLUID_CAULDRON = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(Crystallurgy.MOD_ID, "crystal_fluid_cauldron_entity"),
            FabricBlockEntityTypeBuilder.create(CrystalFluidCauldronBlockEntity::new,
                    ModCauldron.CRYSTAL_CAULDRON).build()
    );

    public static BlockEntityType<CoolingFluidCauldronBlockEntity> COOLING_FLUID_CAULDRON = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(Crystallurgy.MOD_ID, "cooling_fluid_cauldron_entity"),
            FabricBlockEntityTypeBuilder.create(CoolingFluidCauldronBlockEntity::new,
                    ModCauldron.COOLING_CAULDRON).build()
    );

    public static void register() {
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, RESONANCE_FORGE);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, FLUID_SYNTHESIZER);

        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.inputFluidStorage, FLUID_SYNTHESIZER);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.outputFluidStorage, FLUID_SYNTHESIZER);

    }


}
