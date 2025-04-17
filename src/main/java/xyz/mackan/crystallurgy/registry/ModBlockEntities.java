package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.ResonanceForgeBlockEntity;

public class ModBlockEntities {
    public static BlockEntityType<ResonanceForgeBlockEntity> RESONANCE_FORGE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(Crystallurgy.MOD_ID, "resonance_forge"),
            FabricBlockEntityTypeBuilder.create(ResonanceForgeBlockEntity::new,
                    ModBlocks.RESONANCE_FORGE).build()
    );

    public static void register() {
       Crystallurgy.LOGGER.info("Registering block entities.");
    }
}
