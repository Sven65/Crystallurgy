package xyz.mackan.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xyz.mackan.Crystallurgy;
import xyz.mackan.gui.ResonanceForgeScreenHandler;

public class ModScreens {
    public static ScreenHandlerType<ResonanceForgeScreenHandler> RESONANCE_FORGE_SCREEN_HANDLER;

    public static void register() {
        RESONANCE_FORGE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Crystallurgy.MOD_ID, "resonance_forge"), new ScreenHandlerType<>(ResonanceForgeScreenHandler::new, FeatureSet.empty()));
    }
}
