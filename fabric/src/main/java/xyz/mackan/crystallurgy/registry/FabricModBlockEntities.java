package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.block.CoolingFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.block.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.block.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.block.ResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.util.BlockUtils;

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

    public static BlockEntityType<CrystalFluidCauldronBlockEntity> CRYSTAL_FLUID_CAULDRON = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Constants.id("crystal_fluid_cauldron_entity"),

            FabricBlockEntityTypeBuilder.create(CrystalFluidCauldronBlockEntity::new,
                    FabricModCauldron.CRYSTAL_CAULDRON).build()
    );

    public static BlockEntityType<CoolingFluidCauldronBlockEntity> COOLING_FLUID_CAULDRON = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Constants.id("cooling_fluid_cauldron_entity"),
            FabricBlockEntityTypeBuilder.create(CoolingFluidCauldronBlockEntity::new,
                    FabricModCauldron.COOLING_CAULDRON).build()
    );


    public static void register() {
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, RESONANCE_FORGE);

        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            BlockUtils.Side side = BlockUtils.getSideFromDirection(blockEntity.getCachedState(), direction);
            if (side == BlockUtils.Side.LEFT) return blockEntity.catalystStorage;
            if (side == BlockUtils.Side.BACK) return blockEntity.rawMaterialStorage;
            if (side == BlockUtils.Side.RIGHT) return blockEntity.dyeStorage;
            if (side == BlockUtils.Side.BOTTOM) return blockEntity.outputStorage;
            return null;
        }, RESONANCE_FORGE);

        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, FLUID_SYNTHESIZER);

        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            BlockUtils.Side side = BlockUtils.getSideFromDirection(blockEntity.getCachedState(), direction);
            if (side == BlockUtils.Side.TOP) return blockEntity.material0StorageVariant;
            if (side == BlockUtils.Side.BACK) return blockEntity.material1StorageVariant;
            return null;
        }, FLUID_SYNTHESIZER);

        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            BlockUtils.Side side = BlockUtils.getSideFromDirection(blockEntity.getCachedState(), direction);
            if (side == BlockUtils.Side.LEFT) return blockEntity.inputFluidStorage;
            if (side == BlockUtils.Side.RIGHT) return blockEntity.outputFluidStorage;
            return null;
        }, FLUID_SYNTHESIZER);
    }
}
