package xyz.mackan.crystallurgy.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;
import xyz.mackan.crystallurgy.util.ColorUtil;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    private void spawnParticles(World world, BlockPos pos, int chance, Vector3f color) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        if(rand.nextInt(chance) == 0) {
            double x = pos.getX() + rand.nextDouble();
            double y = pos.getY() + 1.0 + rand.nextDouble() * 0.2;
            double z = pos.getZ() + rand.nextDouble();

            world.addParticle(
                    new DustParticleEffect(color, 1.0F),
                    x, y, z,
                    0.0, 0.02, 0.0
            );
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isClient) return;

        spawnParticles(world, pos, 70, ColorUtil.argbToVector3f(AbstractCoolingFluid.getColor()));
    }
}