package xyz.mackan.crystallurgy.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import xyz.mackan.crystallurgy.registry.ModBlocks;
import xyz.mackan.crystallurgy.registry.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //blockStateModelGenerator.registerSimpleState(ModBlocks.RESONANCE_FORGE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.RESONANCE_FORGE);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.DIAMOND_RESONATOR_CRYSTAL, Models.GENERATED);
    }
}
