package xyz.mackan;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.mackan.gui.ResonanceForgeScreenHandler;
import xyz.mackan.registry.ModBlockEntities;
import xyz.mackan.registry.ModBlocks;
import xyz.mackan.registry.ModItems;
import xyz.mackan.registry.ModScreens;

public class Crystallurgy implements ModInitializer {
	public static final String MOD_ID = "crystallurgy";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ModScreens.register();
		ModBlocks.register();
		ModBlockEntities.register();
		ModItems.register();

	}
}