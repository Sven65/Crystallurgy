package xyz.mackan.crystallurgy.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.registry.ModItems;

import java.util.function.BiConsumer;

public class ModLootTableProvider extends SimpleFabricLootTableProvider {
    public ModLootTableProvider(FabricDataOutput output) {
        super(output, LootContextTypes.CHEST);
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
        LootPool pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(ItemEntry.builder(ModItems.CRYSTAL_SEED))
                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2)))
                    .conditionally(RandomChanceLootCondition.builder(0.3f)) // 30% chance
                .build();

        // Create the full loot table builder
        LootTable.Builder lootTable = LootTable.builder().pool(pool);

        // Export the loot table under the inject path
        exporter.accept(Constants.id("inject/dungeon_additions"), lootTable);
    }
}
