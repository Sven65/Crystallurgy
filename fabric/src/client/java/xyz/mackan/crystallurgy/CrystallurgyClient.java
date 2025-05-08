package xyz.mackan.crystallurgy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.fluid.AbstractCoolingFluid;
import xyz.mackan.crystallurgy.fluid.AbstractCrystalFluid;
import xyz.mackan.crystallurgy.gui.FluidSynthesizerScreen;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreen;
import xyz.mackan.crystallurgy.networking.ModNetworking;
import xyz.mackan.crystallurgy.registry.FabricModBlockEntities;
import xyz.mackan.crystallurgy.registry.FabricModFluids;
import xyz.mackan.crystallurgy.registry.FabricModScreens;
import xyz.mackan.crystallurgy.render.FluidCauldronRenderer;

public class CrystallurgyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CrystallurgyCommon.LOGGER.info("Hello Fabric Client");

		FluidRenderHandlerRegistry.INSTANCE.register(
				FabricModFluids.STILL_CRYSTAL_FLUID, FabricModFluids.FLOWING_CRYSTAL_FLUID,
				new SimpleFluidRenderHandler(
						new Identifier("minecraft:block/water_still"),
						new Identifier("minecraft:block/water_flow"),
						AbstractCrystalFluid.getColor()
				)
		);

		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
				FabricModFluids.STILL_CRYSTAL_FLUID, FabricModFluids.FLOWING_CRYSTAL_FLUID);


		FluidRenderHandlerRegistry.INSTANCE.register(
				FabricModFluids.STILL_COOLING_FLUID, FabricModFluids.FLOWING_COOLING_FLUID,
				new SimpleFluidRenderHandler(
						new Identifier("minecraft:block/water_still"),
						new Identifier("minecraft:block/water_flow"),
						AbstractCoolingFluid.getColor()
				)
		);

		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
				FabricModFluids.STILL_COOLING_FLUID, FabricModFluids.FLOWING_COOLING_FLUID);

		HandledScreens.register(FabricModScreens.RESONANCE_FORGE_SCREEN_HANDLER, ResonanceForgeScreen::new);
		HandledScreens.register(FabricModScreens.FLUID_SYNTHESIZER_SCREEN_HANDLER, FluidSynthesizerScreen::new);

		BlockEntityRendererFactories.register(FabricModBlockEntities.CRYSTAL_FLUID_CAULDRON, FluidCauldronRenderer::new);
		BlockEntityRendererFactories.register(FabricModBlockEntities.COOLING_FLUID_CAULDRON, FluidCauldronRenderer::new);

		ModNetworking.registerS2CPackets();
	}
}