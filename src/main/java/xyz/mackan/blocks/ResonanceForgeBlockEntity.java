package xyz.mackan.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import xyz.mackan.registry.ModBlockEntities;

public class ResonanceForgeBlockEntity extends BlockEntity {
    private int energy;
    private int progress;

    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_FORGE, pos, state);
    }

    public void tick() {
        // Add energy tick, check inputs, process synthesis
    }
}
