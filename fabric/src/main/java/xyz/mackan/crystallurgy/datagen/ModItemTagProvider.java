package xyz.mackan.crystallurgy.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.registry.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public static final TagKey<Item> RESONATOR_CRYSTALS = TagKey.of(RegistryKeys.ITEM, Constants.id("resonator_crystals"));

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(RESONATOR_CRYSTALS)
                .add(ModItems.CRYSTAL_SEED_RESONATOR_CRYSTAL)
                .add(ModItems.COAL_RESONATOR_CRYSTAL)
                .add(ModItems.IRON_RESONATOR_CRYSTAL)
                .add(ModItems.GOLD_RESONATOR_CRYSTAL)
                .add(ModItems.DIAMOND_RESONATOR_CRYSTAL)
                .add(ModItems.NETHERITE_RESONATOR_CRYSTAL)
                .add(ModItems.LAPIS_RESONATOR_CRYSTAL)
                .add(ModItems.EMERALD_RESONATOR_CRYSTAL)
                .add(ModItems.QUARTZ_RESONATOR_CRYSTAL)
                .add(ModItems.REDSTONE_RESONATOR_CRYSTAL);
    }
}
