package xyz.mackan.crystallurgy.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.effect.CorrosionEffect;
import xyz.mackan.crystallurgy.registry.FabricModDamageTypes;

import java.util.List;

public class CrystalFluidBlock extends FluidBlock {
    private StatusEffectInstance corrosionEffect = new StatusEffectInstance(FabricModDamageTypes.CORROSION_STATUS_EFFECT.value(), 200, 0); // 10 seconds, level 1

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

        // Apply freezing effect to each entity
        for (LivingEntity living : entities) {
            living.addStatusEffect(corrosionEffect);
        }
    }
}