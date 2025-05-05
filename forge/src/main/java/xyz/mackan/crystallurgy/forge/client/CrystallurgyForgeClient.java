package xyz.mackan.crystallurgy.forge.client;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.forge.block.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.forge.client.gui.FluidSynthesizerScreen;
import xyz.mackan.crystallurgy.forge.client.gui.ResonanceForgeScreen;
import xyz.mackan.crystallurgy.forge.client.render.FluidCauldronRenderer;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlockEntities;
import xyz.mackan.crystallurgy.forge.registry.ForgeModScreens;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CrystallurgyForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        CrystallurgyCommon.LOGGER.info("Hello Forge Client");

        BlockEntityRendererFactories.register(ForgeModBlockEntities.CRYSTAL_FLUID_CAULDRON.get(), FluidCauldronRenderer::new);
        BlockEntityRendererFactories.register(ForgeModBlockEntities.COOLING_FLUID_CAULDRON.get(), FluidCauldronRenderer::new);

        event.enqueueWork(() -> {
            HandledScreens.register(ForgeModScreens.RESONANCE_FORGE_SCREEN.get(), ResonanceForgeScreen::new);
            HandledScreens.register(ForgeModScreens.FLUID_SYNTHESIZER_SCREEN_HANDLER.get(), FluidSynthesizerScreen::new);
        });
    }
}
