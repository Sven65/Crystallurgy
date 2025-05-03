package xyz.mackan.crystallurgy.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class CoolingFluidBlock extends FluidBlock {
    public CoolingFluidBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient()) return;

        Box box = new Box(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0
        );

        // Find all living entities in this box
        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                box,
                LivingEntity::canFreeze
        );

        // Apply freezing effect to each entity
        for (LivingEntity living : entities) {
            // Increase frozen ticks
            int frozenTicks = living.getFrozenTicks();
            int newFrozenTicks = Math.min(living.getMinFreezeDamageTicks() + 20, frozenTicks + 5);

            // Cap at a reasonable maximum (140 is vanilla maximum)
            if (newFrozenTicks > 140) {
                newFrozenTicks = 140;
            }

            living.setFrozenTicks(newFrozenTicks);

            // Deal damage when fully frozen
            if (living.getFrozenTicks() >= living.getMinFreezeDamageTicks()) {
                if (world.getTime() % 20 == 0) {
                    living.damage(world.getDamageSources().freeze(), 1.0F);
                }
            }
        }
    }
}