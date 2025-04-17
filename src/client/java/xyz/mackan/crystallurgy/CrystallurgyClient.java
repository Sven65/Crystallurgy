package xyz.mackan.crystallurgy;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreen;
import xyz.mackan.crystallurgy.networking.ModNetworking;
import xyz.mackan.crystallurgy.registry.ModScreens;

public class CrystallurgyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreens.RESONANCE_FORGE_SCREEN_HANDLER, ResonanceForgeScreen::new);

		ModNetworking.registerS2CPackets();

		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}