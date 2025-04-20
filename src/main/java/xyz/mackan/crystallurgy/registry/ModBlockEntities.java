package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.CoolingFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.blocks.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.blocks.ResonanceForgeBlockEntity;

public class ModBlockEntities {
    public static BlockEntityType<ResonanceForgeBlockEntity> RESONANCE_FORGE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(Crystallurgy.MOD_ID, "resonance_forge"),
            FabricBlockEntityTypeBuilder.create(ResonanceForgeBlockEntity::new,
                    ModBlocks.RESONANCE_FORGE).build()
    );

    public static BlockEntityType<CrystalFluidCauldronBlockEntity> CRYSTAL_FLUID_CAULDRON = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(Crystallurgy.MOD_ID, "crystal_fluid_cauldron_entity"),
            FabricBlockEntityTypeBuilder.create(CrystalFluidCauldronBlockEntity::new,
                    ModCauldron.CRYSTAL_CAULDRON).build()
    );

    public static BlockEntityType<CoolingFluidCauldronBlockEntity> COOLING_FLUID_CAULDRON;

    public static void register() {
       Crystallurgy.LOGGER.info("Registering block entities.");

       EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, RESONANCE_FORGE);

       COOLING_FLUID_CAULDRON = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Crystallurgy.MOD_ID, "cooling_fluid_cauldron_entity"),
                FabricBlockEntityTypeBuilder.create(CoolingFluidCauldronBlockEntity::new,
                        ModCauldron.COOLING_CAULDRON).build()
        );
    }


}
