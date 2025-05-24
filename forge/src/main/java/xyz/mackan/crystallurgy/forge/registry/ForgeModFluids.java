package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.fluid.AbstractCoolingFluid;
import xyz.mackan.crystallurgy.fluid.AbstractCrystalFluid;
import xyz.mackan.crystallurgy.fluid.CoolingFluidBlock;
import xyz.mackan.crystallurgy.forge.fluid.BaseFluid;
import xyz.mackan.crystallurgy.forge.fluid.CrystalFluidBlock;

public class ForgeModFluids {
    public static final DeferredRegister<Item> FLUID_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Constants.MOD_ID);
    public static final DeferredRegister<Block> FLUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Constants.MOD_ID);

    public static RegistryObject<Item> CRYSTAL_FLUID_BUCKET;
    public static RegistryObject<FluidType> CRYSTAL_FLUID_TYPE;
    public static RegistryObject<FluidBlock> CRYSTAL_FLUID_BLOCK;
    public static RegistryObject<ForgeFlowingFluid> STILL_CRYSTAL_FLUID;
    public static RegistryObject<ForgeFlowingFluid> FLOWING_CRYSTAL_FLUID;
    public static ForgeFlowingFluid.Properties CRYSTAL_FLUID_PROPERTIES;

    public static RegistryObject<Item> COOLING_FLUID_BUCKET;
    public static RegistryObject<FluidType> COOLING_FLUID_TYPE;
    public static RegistryObject<FluidBlock> COOLING_FLUID_BLOCK;
    public static RegistryObject<ForgeFlowingFluid> STILL_COOLING_FLUID;
    public static RegistryObject<ForgeFlowingFluid> FLOWING_COOLING_FLUID;
    public static ForgeFlowingFluid.Properties COOLING_FLUID_PROPERTIES;


    public static void register(IEventBus eventBus) {
        STILL_CRYSTAL_FLUID =
                FLUIDS.register("crystal_fluid", () -> new ForgeFlowingFluid.Source(CRYSTAL_FLUID_PROPERTIES));

        CRYSTAL_FLUID_BUCKET = FLUID_ITEMS.register("crystal_fluid_bucket", () -> new BucketItem(STILL_CRYSTAL_FLUID, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        CRYSTAL_FLUID_TYPE = FLUID_TYPES.register("crystal_fluid", () -> new BaseFluid(AbstractCrystalFluid.getColor()));

        CRYSTAL_FLUID_BLOCK = FLUID_BLOCKS.register("crystal_fluid_block", () ->
                new CrystalFluidBlock(STILL_CRYSTAL_FLUID.get(), AbstractBlock.Settings.copy(Blocks.WATER))
        );



        FLOWING_CRYSTAL_FLUID =
                FLUIDS.register("flowing_crystal_fluid", () -> new ForgeFlowingFluid.Flowing(CRYSTAL_FLUID_PROPERTIES));

        CRYSTAL_FLUID_PROPERTIES =
                new ForgeFlowingFluid.Properties(
                        CRYSTAL_FLUID_TYPE,
                        STILL_CRYSTAL_FLUID,
                        FLOWING_CRYSTAL_FLUID
                ).bucket(() -> CRYSTAL_FLUID_BUCKET.get())
                        .block(() -> CRYSTAL_FLUID_BLOCK.get());

        // COOLING FLUID

        COOLING_FLUID_BUCKET = FLUID_ITEMS.register("cooling_fluid_bucket", () -> new BucketItem(STILL_COOLING_FLUID, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        COOLING_FLUID_TYPE = FLUID_TYPES.register("cooling_fluid", () -> new BaseFluid(AbstractCoolingFluid.getColor()));

        COOLING_FLUID_BLOCK = FLUID_BLOCKS.register("cooling_fluid_block", () ->
                new CoolingFluidBlock(STILL_COOLING_FLUID.get(), AbstractBlock.Settings.copy(Blocks.WATER))
        );

        STILL_COOLING_FLUID =
                FLUIDS.register("cooling_fluid", () -> new ForgeFlowingFluid.Source(COOLING_FLUID_PROPERTIES));

        FLOWING_COOLING_FLUID =
                FLUIDS.register("flowing_cooling_fluid", () -> new ForgeFlowingFluid.Flowing(COOLING_FLUID_PROPERTIES));

        COOLING_FLUID_PROPERTIES =
                new ForgeFlowingFluid.Properties(
                        COOLING_FLUID_TYPE,
                        STILL_COOLING_FLUID,
                        FLOWING_COOLING_FLUID
                ).bucket(() -> COOLING_FLUID_BUCKET.get())
                        .block(() -> COOLING_FLUID_BLOCK.get());


        FLUID_BLOCKS.register(eventBus);
        FLUID_TYPES.register(eventBus);
        FLUID_ITEMS.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
