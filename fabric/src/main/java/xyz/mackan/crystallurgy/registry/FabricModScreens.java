package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreenHandler;

public class FabricModScreens {
    public static ScreenHandlerType<ResonanceForgeScreenHandler> RESONANCE_FORGE_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Constants.id("resonance_forge"),
            new ExtendedScreenHandlerType<>(ResonanceForgeScreenHandler::new)
    );

    public static void register() {}
}
