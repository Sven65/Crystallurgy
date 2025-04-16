package xyz.mackan;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import xyz.mackan.gui.ResonanceForgeScreen;
import xyz.mackan.registry.ModScreens;

public class CrystallurgyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModScreens.register();
		HandledScreens.register(ModScreens.RESONANCE_FORGE_SCREEN_HANDLER, ResonanceForgeScreen::new);

		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}