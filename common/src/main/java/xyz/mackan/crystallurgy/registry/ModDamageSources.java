package xyz.mackan.crystallurgy.registry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Constants;

public class ModDamageSources {
    public static final Identifier CORROSION_ID = Constants.id("corrosion");
    public static final RegistryKey<DamageType> CORROSION_KEY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, CORROSION_ID);

    public static DamageSource corrosion(ServerWorld world) {
        RegistryEntry<DamageType> entry = world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(CORROSION_KEY)
                .orElseThrow(() -> new IllegalStateException("Missing corrosion damage type"));

        return new DamageSource(entry);
    }
}
