package xyz.mackan.crystallurgy;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import xyz.mackan.crystallurgy.datagen.ModBlockTagProvider;
import xyz.mackan.crystallurgy.datagen.ModItemTagProvider;
import xyz.mackan.crystallurgy.datagen.ModModelProvider;
import xyz.mackan.crystallurgy.datagen.ModRecipeProvider;

public class CrystallurgyDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModItemTagProvider::new);

	}
}
