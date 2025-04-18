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
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.fluid.CrystalFluid;

public class ModFluids {
    public static FlowableFluid STILL_CRYSTAL_FLUID;
    public static FlowableFluid FLOWING_CRYSTAL_FLUID;
    public static Block CRYSTAL_FLUID_BLOCK;
    public static Item CRYSTAL_FLUID_BUCKET;

    public static void register() {
        STILL_CRYSTAL_FLUID = Registry.register(Registries.FLUID, new Identifier(Crystallurgy.MOD_ID, "crystal_fluid"), new CrystalFluid.Still());
        FLOWING_CRYSTAL_FLUID = Registry.register(Registries.FLUID, new Identifier(Crystallurgy.MOD_ID, "flowing_crystal_fluid"), new CrystalFluid.Flowing());

        CRYSTAL_FLUID_BLOCK = Registry.register(Registries.BLOCK, new Identifier(Crystallurgy.MOD_ID, "crystal_fluid_block"), new FluidBlock(ModFluids.STILL_CRYSTAL_FLUID, FabricBlockSettings.copyOf(Blocks.WATER)));
        CRYSTAL_FLUID_BUCKET = Registry.register(Registries.ITEM, new Identifier(Crystallurgy.MOD_ID, "crystal_fluid_bucket"), new BucketItem(ModFluids.STILL_CRYSTAL_FLUID, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    }

}
