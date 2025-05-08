package xyz.mackan.crystallurgy.forge.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;
import xyz.mackan.crystallurgy.fluid.AbstractCrystalFluid;
import xyz.mackan.crystallurgy.forge.registry.ForgeModDamageType;
import xyz.mackan.crystallurgy.util.ColorUtil;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CrystalFluidBlock extends FluidBlock {
    public CrystalFluidBlock(FlowableFluid fluid, Settings settings) {
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
                entity1 -> true
        );

        StatusEffectInstance corrosionEffect = new StatusEffectInstance(ForgeModDamageType.CORROSION.get(), 200, 0); // 10 seconds, level 1


        // Apply freezing effect to each entity
        for (LivingEntity living : entities) {
            living.addStatusEffect(corrosionEffect);
        }
    }

    private void spawnParticles(World world, BlockPos pos, int chance, Vector3f color) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        if(rand.nextInt(chance) == 0) {
            // ~10% chance per tick → average once every 0.5 seconds
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

        spawnParticles(world, pos, 70, ColorUtil.argbToVector3f(AbstractCrystalFluid.getColor()));
    }
}