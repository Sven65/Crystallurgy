package xyz.mackan.crystallurgy.datagen;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.registry.ModBlocks;
import xyz.mackan.crystallurgy.registry.ModCauldron;
import xyz.mackan.crystallurgy.registry.ModFluids;
import xyz.mackan.crystallurgy.registry.ModItems;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }


    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.RESONANCE_FORGE);
        generateCauldronBlockState("crystal_cauldron", ModCauldron.CRYSTAL_CAULDRON, blockStateModelGenerator);
        generateCauldronBlockState("cooling_cauldron", ModCauldron.COOLING_CAULDRON, blockStateModelGenerator);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.CRYSTAL_SEED, Models.GENERATED);
        itemModelGenerator.register(ModFluids.CRYSTAL_FLUID_BUCKET, Models.GENERATED);

        itemModelGenerator.register(ModItems.COAL_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.IRON_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOLD_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.DIAMOND_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.NETHERITE_RESONATOR_CRYSTAL, Models.GENERATED);

        itemModelGenerator.register(ModItems.LAPIS_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.EMERALD_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.QUARTZ_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.REDSTONE_RESONATOR_CRYSTAL, Models.GENERATED);

        itemModelGenerator.register(ModItems.UNREFINED_COAL_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_IRON_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_GOLD_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_DIAMOND_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_NETHERITE_RESONATOR_CRYSTAL, Models.GENERATED);

        itemModelGenerator.register(ModItems.UNREFINED_LAPIS_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_EMERALD_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_QUARTZ_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_REDSTONE_RESONATOR_CRYSTAL, Models.GENERATED);

        registerGrowthCrystal(itemModelGenerator, new Identifier(Crystallurgy.MOD_ID, "item/diamond_crystal_seed"));
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

        JsonObject modelJson = PredicateItemJsonBuilder.createItemModelWithOverrides(itemId, textures, overrides);

        itemModelGenerator.writer.accept(itemId, () -> modelJson);

        for (Pair<Map<String, Number>, Identifier> override : overrides) {
            Identifier overrideModelId = override.getRight();

            Map<TextureKey, Identifier> overrideTextures = Map.of(
                    TextureKey.LAYER0, new Identifier(itemId.getNamespace(), overrideModelId.getPath())
            );

            JsonObject overrideModel = Models.GENERATED.createJson(overrideModelId, overrideTextures);
            itemModelGenerator.writer.accept(overrideModelId, () -> overrideModel);
        }
    }


    // TODO: Make this not be white, aka actually have color
    private void generateCauldronBlockState(String name, Block cauldronBlock, BlockStateModelGenerator blockStateModelGenerator) {
        Identifier level0 = createCauldronModel(String.format("%s_level0", name),
                TextureMap.cauldron(new Identifier("minecraft:block/cauldron_inner"))
                        .put(TextureKey.TOP, new Identifier("minecraft:block/cauldron_top"))
                        .put(TextureKey.BOTTOM, new Identifier("minecraft:block/cauldron_bottom"))
                        .put(TextureKey.SIDE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.PARTICLE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.CONTENT, new Identifier("minecraft:block/cauldron_empty")),
                blockStateModelGenerator
        );

        Identifier level1 = createCauldronModel(String.format("%s_level1", name),
                TextureMap.cauldron(new Identifier("minecraft:block/cauldron_inner"))
                        .put(TextureKey.TOP, new Identifier("minecraft:block/cauldron_top"))
                        .put(TextureKey.BOTTOM, new Identifier("minecraft:block/cauldron_bottom"))
                        .put(TextureKey.SIDE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.PARTICLE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.CONTENT, new Identifier("minecraft:block/water_still")),
                blockStateModelGenerator
        );

        Identifier level2 = createCauldronModel(String.format("%s_level2", name),
                TextureMap.cauldron(new Identifier("minecraft:block/cauldron_inner"))
                        .put(TextureKey.TOP, new Identifier("minecraft:block/cauldron_top"))
                        .put(TextureKey.BOTTOM, new Identifier("minecraft:block/cauldron_bottom"))
                        .put(TextureKey.SIDE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.PARTICLE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.CONTENT, new Identifier("minecraft:block/water_still")),
                blockStateModelGenerator
        );

        Identifier level3 = createCauldronModel(String.format("%s_level3", name),
                TextureMap.cauldron(new Identifier("minecraft:block/cauldron_inner"))
                        .put(TextureKey.TOP, new Identifier("minecraft:block/cauldron_top"))
                        .put(TextureKey.BOTTOM, new Identifier("minecraft:block/cauldron_bottom"))
                        .put(TextureKey.SIDE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.PARTICLE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.CONTENT, new Identifier("minecraft:block/water_still")),
                blockStateModelGenerator
        );

        // Create the blockstate definition with variants for each level
        VariantsBlockStateSupplier blockStateSupplier = VariantsBlockStateSupplier.create(cauldronBlock)
                .coordinate(BlockStateVariantMap.create(ModCauldron.FLUID_LEVEL)
                        .register(0, BlockStateVariant.create().put(VariantSettings.MODEL, level0))
                        .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, level1))
                        .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, level2))
                        .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, level3)));

        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);
    }

    private Identifier createCauldronModel(String name, TextureMap textureMap, BlockStateModelGenerator blockStateModelGenerator) {
        Identifier modelId = new Identifier(Crystallurgy.MOD_ID, "block/" + name);

        Optional<Identifier> parentId;
        if (name.contains("level0")) {
            parentId = Optional.of(new Identifier("minecraft:block/cauldron"));
        } else if (name.contains("level1")) {
            parentId = Optional.of(new Identifier("minecraft:block/template_cauldron_level1"));
        } else if (name.contains("level2")) {
            parentId = Optional.of(new Identifier("minecraft:block/template_cauldron_level2"));
        } else {
            parentId = Optional.of(new Identifier("minecraft:block/template_cauldron_full"));
        }

        Model model = new Model(
                parentId,
                Optional.empty(),
                TextureKey.TOP,
                TextureKey.BOTTOM,
                TextureKey.SIDE,
                TextureKey.PARTICLE,
                TextureKey.CONTENT
        );

        model.upload(modelId, textureMap, blockStateModelGenerator.modelCollector);

        return modelId;
    }
}
