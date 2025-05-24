package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.effect.CorrosionEffect;

public class ForgeModDamageType {
    public static final DeferredRegister<StatusEffect> STATUS_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Constants.MOD_ID);

    public static final RegistryObject<StatusEffect> CORROSION =
            STATUS_EFFECTS.register("corrosion", CorrosionEffect::new);

    public static void register(IEventBus eventBus) {
        STATUS_EFFECTS.register(eventBus);
    }
}
