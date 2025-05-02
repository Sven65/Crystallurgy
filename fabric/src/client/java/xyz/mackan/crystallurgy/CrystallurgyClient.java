package xyz.mackan.crystallurgy;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreen;
import xyz.mackan.crystallurgy.networking.ModNetworking;
import xyz.mackan.crystallurgy.registry.FabricModScreens;

public class CrystallurgyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CrystallurgyCommon.LOGGER.info("Hello Fabric Client");

		HandledScreens.register(FabricModScreens.RESONANCE_FORGE_SCREEN_HANDLER, ResonanceForgeScreen::new);

		ModNetworking.registerS2CPackets();
	}
}