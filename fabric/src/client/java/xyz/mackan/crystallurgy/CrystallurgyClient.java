package xyz.mackan.crystallurgy;

import net.fabricmc.api.ClientModInitializer;

public class CrystallurgyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CrystallurgyCommon.LOGGER.info("Hello Fabric Client");
	}
}