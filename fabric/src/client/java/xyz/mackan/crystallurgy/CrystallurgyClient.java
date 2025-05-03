package xyz.mackan.crystallurgy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreen;
import xyz.mackan.crystallurgy.networking.ModNetworking;
import xyz.mackan.crystallurgy.registry.FabricModFluids;
import xyz.mackan.crystallurgy.registry.FabricModScreens;

public class CrystallurgyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CrystallurgyCommon.LOGGER.info("Hello Fabric Client");

		FluidRenderHandlerRegistry.INSTANCE.register(
				FabricModFluids.STILL_CRYSTAL_FLUID, FabricModFluids.FLOWING_CRYSTAL_FLUID,
				new SimpleFluidRenderHandler(
						new Identifier("minecraft:block/water_still"),
						new Identifier("minecraft:block/water_flow"),
						0xA1B83BCE
				)
		);

		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
				FabricModFluids.STILL_CRYSTAL_FLUID, FabricModFluids.FLOWING_CRYSTAL_FLUID);


		FluidRenderHandlerRegistry.INSTANCE.register(
				FabricModFluids.STILL_COOLING_FLUID, FabricModFluids.FLOWING_COOLING_FLUID,
				new SimpleFluidRenderHandler(
						new Identifier("minecraft:block/water_still"),
						new Identifier("minecraft:block/water_flow"),
						0x970A4757
				)
		);

		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
				FabricModFluids.STILL_COOLING_FLUID, FabricModFluids.FLOWING_COOLING_FLUID);

		HandledScreens.register(FabricModScreens.RESONANCE_FORGE_SCREEN_HANDLER, ResonanceForgeScreen::new);

		ModNetworking.registerS2CPackets();
	}
}