package xyz.mackan.crystallurgy.registry;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.effect.CorrosionEffect;

public class FabricModDamageTypes {
    public static final RegistryEntry<StatusEffect> CORROSION_STATUS_EFFECT =
            Registry.registerReference(Registries.STATUS_EFFECT, Constants.id("corrosion"), new CorrosionEffect());

    public static void register() {

    }
}
