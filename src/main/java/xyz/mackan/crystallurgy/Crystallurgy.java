package xyz.mackan.crystallurgy;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.mackan.crystallurgy.registry.*;

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


		/*
		 * WARNING! HERE BE DRAGONS
		 * For whatever reason, this all needs to be in this exact order
		 * Do NOT modify if you don't know what you're doing. Things WILL break.
		 */
		ModMessages.register();
		ModScreens.register();
		ModBlocks.register();
		ModItems.register();
		ModRecipes.register();
		ModLootTables.register();
		ModFluids.register();
		ModItemGroup.register();

		ModCauldron.register();
		ModBlockEntities.register();
	}
}