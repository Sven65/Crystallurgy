package xyz.mackan.crystallurgy.datagen;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.registry.ModBlocks;
import xyz.mackan.crystallurgy.registry.ModItems;

import java.util.List;
import java.util.Map;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //blockStateModelGenerator.registerSimpleState(ModBlocks.RESONANCE_FORGE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.RESONANCE_FORGE);
    }

    public void registerGrowthCrystal (ItemModelGenerator itemModelGenerator, Identifier itemId) {
        // Base texture
        Map<TextureKey, Identifier> textures = Map.of(
                TextureKey.LAYER0, new Identifier(itemId.getNamespace(), "item/" + itemId.getPath())
        );

        // Overrides
        List<Pair<Map<String, Number>, Identifier>> overrides = List.of(
                new Pair(Map.of("charge", 25), new Identifier(itemId.getNamespace(), itemId.getPath() + "_25")),
                new Pair(Map.of("charge", 50), new Identifier(itemId.getNamespace(), itemId.getPath() + "_50")),
                new Pair(Map.of("charge", 75), new Identifier(itemId.getNamespace(), itemId.getPath() + "_75")),
                new Pair(Map.of("charge", 100), new Identifier(itemId.getNamespace(), itemId.getPath() + "_100"))
        );

        // Call your custom method
        JsonObject modelJson = PredicateItemJsonBuilder.createItemModelWithOverrides(itemId, textures, overrides);

        // Register it with the model generator
        itemModelGenerator.writer.accept(itemId, () -> modelJson);

        for (Pair<Map<String, Number>, Identifier> override : overrides) {
            Identifier overrideModelId = override.getRight(); // e.g. "mymod:item/myitem_variant1"

            Map<TextureKey, Identifier> overrideTextures = Map.of(
                    TextureKey.LAYER0, new Identifier(itemId.getNamespace(), overrideModelId.getPath())
            );

            JsonObject overrideModel = Models.GENERATED.createJson(overrideModelId, overrideTextures);
            itemModelGenerator.writer.accept(overrideModelId, () -> overrideModel);
        }
    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.DIAMOND_RESONATOR_CRYSTAL, Models.GENERATED);

        registerGrowthCrystal(itemModelGenerator, new Identifier(Crystallurgy.MOD_ID, "item/diamond_crystal_seed"));
    }
}
