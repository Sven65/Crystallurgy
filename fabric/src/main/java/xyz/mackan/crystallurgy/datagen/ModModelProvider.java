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
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.registry.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }


    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(FabricModBlocks.RESONANCE_FORGE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(FabricModBlocks.FLUID_SYNTHESIZER);
        generateCauldronBlockState("crystal_cauldron", FabricModCauldron.CRYSTAL_CAULDRON, blockStateModelGenerator);
        generateCauldronBlockState("cooling_cauldron", FabricModCauldron.COOLING_CAULDRON, blockStateModelGenerator);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.CRYSTAL_SEED, Models.GENERATED);
        itemModelGenerator.register(FabricModFluids.CRYSTAL_FLUID_BUCKET, Models.GENERATED);
        itemModelGenerator.register(FabricModFluids.COOLING_FLUID_BUCKET, Models.GENERATED);

        itemModelGenerator.register(ModItems.CRYSTAL_SEED_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.COAL_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.IRON_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOLD_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.DIAMOND_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.NETHERITE_RESONATOR_CRYSTAL, Models.GENERATED);

        itemModelGenerator.register(ModItems.LAPIS_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.EMERALD_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.QUARTZ_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.REDSTONE_RESONATOR_CRYSTAL, Models.GENERATED);

        itemModelGenerator.register(ModItems.UNREFINED_CRYSTAL_SEED_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_COAL_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_IRON_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_GOLD_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_DIAMOND_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_NETHERITE_RESONATOR_CRYSTAL, Models.GENERATED);

        itemModelGenerator.register(ModItems.UNREFINED_LAPIS_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_EMERALD_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_QUARTZ_RESONATOR_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_REDSTONE_RESONATOR_CRYSTAL, Models.GENERATED);
    }

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
                        .put(TextureKey.CONTENT, new Identifier("minecraft:block/cauldron_empty")),
                blockStateModelGenerator
        );

        Identifier level2 = createCauldronModel(String.format("%s_level2", name),
                TextureMap.cauldron(new Identifier("minecraft:block/cauldron_inner"))
                        .put(TextureKey.TOP, new Identifier("minecraft:block/cauldron_top"))
                        .put(TextureKey.BOTTOM, new Identifier("minecraft:block/cauldron_bottom"))
                        .put(TextureKey.SIDE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.PARTICLE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.CONTENT, new Identifier("minecraft:block/cauldron_empty")),
                blockStateModelGenerator
        );

        Identifier level3 = createCauldronModel(String.format("%s_level3", name),
                TextureMap.cauldron(new Identifier("minecraft:block/cauldron_inner"))
                        .put(TextureKey.TOP, new Identifier("minecraft:block/cauldron_top"))
                        .put(TextureKey.BOTTOM, new Identifier("minecraft:block/cauldron_bottom"))
                        .put(TextureKey.SIDE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.PARTICLE, new Identifier("minecraft:block/cauldron_side"))
                        .put(TextureKey.CONTENT, new Identifier("minecraft:block/cauldron_empty")),
                blockStateModelGenerator
        );

        // Create the blockstate definition with variants for each level
        VariantsBlockStateSupplier blockStateSupplier = VariantsBlockStateSupplier.create(cauldronBlock)
                .coordinate(BlockStateVariantMap.create(ModProperties.FLUID_LEVEL)
                        .register(0, BlockStateVariant.create().put(VariantSettings.MODEL, level0))
                        .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, level1))
                        .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, level2))
                        .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, level3)));

        blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);
    }

    private Identifier createCauldronModel(String name, TextureMap textureMap, BlockStateModelGenerator blockStateModelGenerator) {
        Identifier modelId = Constants.id("block/" + name);

        Optional<Identifier> parentId;
        if (name.contains("level0")) {
            parentId = Optional.of(new Identifier("minecraft:block/cauldron"));
        } else if (name.contains("level1")) {
            parentId = Optional.of(new Identifier("minecraft:block/cauldron"));
        } else if (name.contains("level2")) {
            parentId = Optional.of(new Identifier("minecraft:block/cauldron"));
        } else {
            parentId = Optional.of(new Identifier("minecraft:block/cauldron"));
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
