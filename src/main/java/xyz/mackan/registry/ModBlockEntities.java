package xyz.mackan.registry;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import xyz.mackan.Crystallurgy;
import xyz.mackan.blocks.ResonanceForgeBlockEntity;

public class ModBlockEntities {
    public static BlockEntityType<ResonanceForgeBlockEntity> RESONANCE_FORGE;

    public static void register() {
        RESONANCE_FORGE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Crystallurgy.MOD_ID, "resonance_forge"),
                BlockEntityType.Builder.create(
                        ResonanceForgeBlockEntity::new,
                        ModBlocks.RESONANCE_FORGE
                ).build(null)
        );
    }
}
