package xyz.mackan.crystallurgy.registry;


import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import xyz.mackan.crystallurgy.Constants;


public class FabricModLootTables {
    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (id.equals(LootTables.SIMPLE_DUNGEON_CHEST)) {
                LootPool pool = LootPool.builder()
                        .with(LootTableEntry.builder(Constants.id("inject/dungeon_additions")))
                        .build();
                tableBuilder.pool(pool);
            }
        });
    }
}
