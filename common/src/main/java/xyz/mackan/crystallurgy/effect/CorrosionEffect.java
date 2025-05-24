package xyz.mackan.crystallurgy.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.registry.ModDamageSources;

public class CorrosionEffect extends StatusEffect {
    public CorrosionEffect() {
        super(StatusEffectCategory.HARMFUL, 0x4E9331); // HARMFUL, greenish colour'
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient && entity.getWorld() instanceof ServerWorld serverWorld) {
            entity.damage(ModDamageSources.corrosion(serverWorld), 2f + amplifier);
            for (ItemStack stack : entity.getArmorItems()) {
                if (!stack.isEmpty()) {
                    stack.damage(1 + amplifier, entity.getRandom(), null);
                }
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Damage every 40 ticks (2 seconds
        return duration % 40 == 0;
    }
}
