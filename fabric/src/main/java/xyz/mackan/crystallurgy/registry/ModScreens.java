package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.gui.FluidSynthesizerScreenHandler;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreenHandler;

public class ModScreens {
    public static ScreenHandlerType<ResonanceForgeScreenHandler> RESONANCE_FORGE_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(Crystallurgy.MOD_ID, "resonance_forge"),
            new ExtendedScreenHandlerType<>(ResonanceForgeScreenHandler::new)
    );

    public static ScreenHandlerType<FluidSynthesizerScreenHandler> FLUID_SYNTHESIZER_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(Crystallurgy.MOD_ID, "fluid_synthesizer"),
            new ExtendedScreenHandlerType<>(FluidSynthesizerScreenHandler::new)
    );

    public static void register() {}
}
