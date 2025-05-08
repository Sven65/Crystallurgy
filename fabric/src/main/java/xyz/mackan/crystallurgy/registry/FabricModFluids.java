package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.fluid.CoolingFluid;
import xyz.mackan.crystallurgy.fluid.CoolingFluidBlock;
import xyz.mackan.crystallurgy.fluid.CrystalFluid;
import xyz.mackan.crystallurgy.fluid.CrystalFluidBlock;

public class FabricModFluids {
    public static FlowableFluid STILL_CRYSTAL_FLUID;
    public static FlowableFluid FLOWING_CRYSTAL_FLUID;
    public static Block CRYSTAL_FLUID_BLOCK;
    public static Item CRYSTAL_FLUID_BUCKET;

    public static FlowableFluid STILL_COOLING_FLUID;
    public static FlowableFluid FLOWING_COOLING_FLUID;
    public static Block COOLING_FLUID_BLOCK;
    public static Item COOLING_FLUID_BUCKET;

    public static void register() {
        STILL_CRYSTAL_FLUID = Registry.register(Registries.FLUID, Constants.id("crystal_fluid"), new CrystalFluid.Still());
        FLOWING_CRYSTAL_FLUID = Registry.register(Registries.FLUID, Constants.id("flowing_crystal_fluid"), new CrystalFluid.Flowing());

        CRYSTAL_FLUID_BLOCK = Registry.register(Registries.BLOCK, Constants.id("crystal_fluid_block"), new CrystalFluidBlock(STILL_CRYSTAL_FLUID, FabricBlockSettings.copyOf(Blocks.WATER)));
        CRYSTAL_FLUID_BUCKET = Registry.register(Registries.ITEM, Constants.id("crystal_fluid_bucket"), new BucketItem(STILL_CRYSTAL_FLUID, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));


        STILL_COOLING_FLUID = Registry.register(Registries.FLUID, Constants.id("cooling_fluid"), new CoolingFluid.Still());
        FLOWING_COOLING_FLUID = Registry.register(Registries.FLUID, Constants.id("flowing_cooling_fluid"), new CoolingFluid.Flowing());

        COOLING_FLUID_BLOCK = Registry.register(Registries.BLOCK, Constants.id("cooling_fluid_block"), new CoolingFluidBlock(STILL_COOLING_FLUID, FabricBlockSettings.copyOf(Blocks.WATER)));
        COOLING_FLUID_BUCKET = Registry.register(Registries.ITEM, Constants.id("cooling_fluid_bucket"), new BucketItem(STILL_COOLING_FLUID, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    }
}
