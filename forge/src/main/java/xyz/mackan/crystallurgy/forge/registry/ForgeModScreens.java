package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.forge.gui.FluidSynthesizerScreenHandler;
import xyz.mackan.crystallurgy.forge.gui.ResonanceForgeScreenHandler;

public class ForgeModScreens {
    public static final DeferredRegister<ScreenHandlerType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MOD_ID);

    public static final RegistryObject<ScreenHandlerType<ResonanceForgeScreenHandler>> RESONANCE_FORGE_SCREEN =
            MENUS.register("resonance_forge", () -> IForgeMenuType.create(ResonanceForgeScreenHandler::new));

    public static final RegistryObject<ScreenHandlerType<FluidSynthesizerScreenHandler>> FLUID_SYNTHESIZER_SCREEN_HANDLER =
            MENUS.register("fluid_synthesizer", () -> IForgeMenuType.create(FluidSynthesizerScreenHandler::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
