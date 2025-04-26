package xyz.mackan.crystallurgy.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.registry.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public static TagKey<Block> FLUID_CAULDRON_HEATERS = TagKey.of(RegistryKeys.BLOCK, new Identifier(Crystallurgy.MOD_ID, "fluid_cauldron_heaters"));
    public static TagKey<Block> COOLING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(Crystallurgy.MOD_ID, "cooling_blocks"));

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {


        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.RESONANCE_FORGE);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.RESONANCE_FORGE);

        this.getOrCreateTagBuilder(FLUID_CAULDRON_HEATERS)
                .add(Blocks.FIRE)
                .add(Blocks.LAVA)
                .add(Blocks.CAMPFIRE)
                .add(Blocks.SOUL_CAMPFIRE)
                .add(Blocks.SOUL_FIRE);

        this.getOrCreateTagBuilder(COOLING_BLOCKS)
                .add(Blocks.ICE)
                .add(Blocks.PACKED_ICE)
                .add(Blocks.BLUE_ICE)
                .add(Blocks.POWDER_SNOW)
                .add(Blocks.SNOW_BLOCK);
    }
}
