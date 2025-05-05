package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.mackan.crystallurgy.Constants;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeModLootTables {
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        Identifier id = event.getName();

        if (id.equals(LootTables.SIMPLE_DUNGEON_CHEST)) {
            LootPool pool = LootPool.builder()
                    .with(LootTableEntry.builder(Constants.id("inject/dungeon_additions")))
                    .build();

            event.getTable().addPool(pool);
        }
    }
}
