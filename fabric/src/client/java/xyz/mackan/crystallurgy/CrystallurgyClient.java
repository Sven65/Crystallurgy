package xyz.mackan.crystallurgy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.gui.FluidSynthesizerScreen;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreen;
import xyz.mackan.crystallurgy.networking.ModNetworking;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModFluids;
import xyz.mackan.crystallurgy.registry.ModScreens;
import xyz.mackan.crystallurgy.render.FluidCauldronRenderer;
import xyz.mackan.crystallurgy.render.FluidSynthesizerRenderer;


public class CrystallurgyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FluidRenderHandlerRegistry.INSTANCE.register(
				ModFluids.STILL_CRYSTAL_FLUID, ModFluids.FLOWING_CRYSTAL_FLUID,
				new SimpleFluidRenderHandler(
						new Identifier("minecraft:block/water_still"),
						new Identifier("minecraft:block/water_flow"),
						0xA1B83BCE
				)
		);

		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
				ModFluids.STILL_CRYSTAL_FLUID, ModFluids.FLOWING_CRYSTAL_FLUID);


		FluidRenderHandlerRegistry.INSTANCE.register(
				ModFluids.STILL_COOLING_FLUID, ModFluids.FLOWING_COOLING_FLUID,
				new SimpleFluidRenderHandler(
						new Identifier("minecraft:block/water_still"),
						new Identifier("minecraft:block/water_flow"),
						0x970A4757
				)
		);

		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
				ModFluids.STILL_COOLING_FLUID, ModFluids.FLOWING_COOLING_FLUID);

		HandledScreens.register(ModScreens.RESONANCE_FORGE_SCREEN_HANDLER, ResonanceForgeScreen::new);
		HandledScreens.register(ModScreens.FLUID_SYNTHESIZER_SCREEN_HANDLER, FluidSynthesizerScreen::new);

		BlockEntityRendererFactories.register(ModBlockEntities.CRYSTAL_FLUID_CAULDRON, FluidCauldronRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.COOLING_FLUID_CAULDRON, FluidCauldronRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.FLUID_SYNTHESIZER, FluidSynthesizerRenderer::new);



		ModNetworking.registerS2CPackets();
	}
}