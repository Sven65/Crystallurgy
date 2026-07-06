package xyz.mackan.crystallurgy.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import xyz.mackan.crystallurgy.registry.FabricModBlocks;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
    public ModBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(FabricModBlocks.RESONANCE_FORGE);
        addDrop(FabricModBlocks.FLUID_SYNTHESIZER);
    }
}