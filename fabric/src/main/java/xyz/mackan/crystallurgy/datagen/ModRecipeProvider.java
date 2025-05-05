package xyz.mackan.crystallurgy.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.registry.ModItems;
import xyz.mackan.crystallurgy.util.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    private final static List<CrystalRecipeContainer> crystalCauldronRecipes = List.of(
            new CrystalRecipeContainer("seed", ModItems.CRYSTAL_SEED, null, ModItems.UNREFINED_CRYSTAL_SEED_RESONATOR_CRYSTAL, 1, 100),
            new CrystalRecipeContainer("coal", ModItems.CRYSTAL_SEED, Items.COAL, ModItems.UNREFINED_COAL_RESONATOR_CRYSTAL, 1, 100),
            new CrystalRecipeContainer("iron", ModItems.CRYSTAL_SEED, Items.IRON_INGOT, ModItems.UNREFINED_IRON_RESONATOR_CRYSTAL, 1, 100),
            new CrystalRecipeContainer("gold", ModItems.CRYSTAL_SEED, Items.GOLD_INGOT, ModItems.UNREFINED_GOLD_RESONATOR_CRYSTAL, 1, 100),
            new CrystalRecipeContainer("diamond", ModItems.CRYSTAL_SEED, Items.DIAMOND, ModItems.UNREFINED_DIAMOND_RESONATOR_CRYSTAL, 1, 100),
            new CrystalRecipeContainer("netherite", ModItems.CRYSTAL_SEED, Items.NETHERITE_INGOT, ModItems.UNREFINED_NETHERITE_RESONATOR_CRYSTAL, 1, 100),

            new CrystalRecipeContainer("lapis", ModItems.CRYSTAL_SEED, Items.LAPIS_LAZULI, ModItems.UNREFINED_LAPIS_RESONATOR_CRYSTAL, 1, 100),
            new CrystalRecipeContainer("emerald", ModItems.CRYSTAL_SEED, Items.EMERALD, ModItems.UNREFINED_EMERALD_RESONATOR_CRYSTAL, 1, 100),
            new CrystalRecipeContainer("quartz", ModItems.CRYSTAL_SEED, Items.QUARTZ, ModItems.UNREFINED_QUARTZ_RESONATOR_CRYSTAL, 1, 100),
            new CrystalRecipeContainer("redstone", ModItems.CRYSTAL_SEED, Items.REDSTONE, ModItems.UNREFINED_REDSTONE_RESONATOR_CRYSTAL, 1, 100)
    );

    private final static List<CoolingRecipeContainer> coolingCauldronRecipes = List.of(
            new CoolingRecipeContainer("seed", ModItems.UNREFINED_CRYSTAL_SEED_RESONATOR_CRYSTAL, ModItems.CRYSTAL_SEED_RESONATOR_CRYSTAL, 1, 100, 5),
            new CoolingRecipeContainer("coal", ModItems.UNREFINED_COAL_RESONATOR_CRYSTAL, ModItems.COAL_RESONATOR_CRYSTAL, 1, 100, 5),
            new CoolingRecipeContainer("iron", ModItems.UNREFINED_IRON_RESONATOR_CRYSTAL, ModItems.IRON_RESONATOR_CRYSTAL, 1, 100, 5),
            new CoolingRecipeContainer("gold", ModItems.UNREFINED_GOLD_RESONATOR_CRYSTAL, ModItems.GOLD_RESONATOR_CRYSTAL,1, 100, 5),
            new CoolingRecipeContainer("diamond", ModItems.UNREFINED_DIAMOND_RESONATOR_CRYSTAL, ModItems.DIAMOND_RESONATOR_CRYSTAL,1, 100, 5),
            new CoolingRecipeContainer("netherite", ModItems.UNREFINED_NETHERITE_RESONATOR_CRYSTAL, ModItems.NETHERITE_RESONATOR_CRYSTAL, 1, 100, 5),

            new CoolingRecipeContainer("lapis", ModItems.UNREFINED_LAPIS_RESONATOR_CRYSTAL, ModItems.LAPIS_RESONATOR_CRYSTAL,1, 100, 5),
            new CoolingRecipeContainer("emerald", ModItems.UNREFINED_EMERALD_RESONATOR_CRYSTAL, ModItems.EMERALD_RESONATOR_CRYSTAL,1, 100, 5),
            new CoolingRecipeContainer("quartz", ModItems.UNREFINED_QUARTZ_RESONATOR_CRYSTAL, ModItems.QUARTZ_RESONATOR_CRYSTAL,1, 100, 5),
            new CoolingRecipeContainer("redstone", ModItems.UNREFINED_REDSTONE_RESONATOR_CRYSTAL, ModItems.REDSTONE_RESONATOR_CRYSTAL, 1, 100, 5)
    );

    private final static List<ForgeRecipeContainer> forgeRecipes = List.of(
            new ForgeRecipeContainer("seed", ModItems.CRYSTAL_SEED_RESONATOR_CRYSTAL, new ItemStack(Items.CHARCOAL, 64), null, ModItems.CRYSTAL_SEED, 2, 15 * 20, 500),
            new ForgeRecipeContainer("coal", ModItems.COAL_RESONATOR_CRYSTAL, new ItemStack(Items.CHARCOAL, 64), null, Items.COAL, 1, 5 * 20, 100),
            new ForgeRecipeContainer("iron", ModItems.IRON_RESONATOR_CRYSTAL, new ItemStack(Items.COAL, 32), null, Items.IRON_INGOT, 1, 10 * 20, 700),
            new ForgeRecipeContainer("gold", ModItems.GOLD_RESONATOR_CRYSTAL, new ItemStack(Items.IRON_BLOCK, 8), null, Items.GOLD_INGOT, 1, 20 * 20, 1000),
            new ForgeRecipeContainer("diamond", ModItems.DIAMOND_RESONATOR_CRYSTAL, new ItemStack(Items.COAL_BLOCK, 64), null, Items.DIAMOND, 1, 30 * 20, 5000),
            new ForgeRecipeContainer("netherite", ModItems.NETHERITE_RESONATOR_CRYSTAL, new ItemStack(Items.DIAMOND_BLOCK, 8), null, Items.NETHERITE_INGOT, 1, 30 * 20, 10000),


            new ForgeRecipeContainer("lapis", ModItems.LAPIS_RESONATOR_CRYSTAL, new ItemStack(Items.AMETHYST_SHARD, 16), new ItemStack(Items.BLUE_DYE, 1), Items.LAPIS_LAZULI, 8, 7 * 20, 500),
            new ForgeRecipeContainer("emerald", ModItems.EMERALD_RESONATOR_CRYSTAL, new ItemStack(Items.AMETHYST_SHARD, 16), new ItemStack(Items.LIME_DYE, 1), Items.EMERALD, 1, 7 * 20, 2500),
            new ForgeRecipeContainer("quartz", ModItems.QUARTZ_RESONATOR_CRYSTAL, new ItemStack(Items.AMETHYST_SHARD, 16), new ItemStack(Items.WHITE_DYE, 1), Items.QUARTZ, 8, 7 * 20, 500),
            new ForgeRecipeContainer("redstone", ModItems.REDSTONE_RESONATOR_CRYSTAL, new ItemStack(Items.AMETHYST_SHARD, 16), new ItemStack(Items.RED_DYE, 1), Items.REDSTONE, 8, 7 * 20, 500)
    );

    private final static List<FluidSynthesizerRecipeContainer> fluidSynthesizerRecipes = List.of(
            //new FluidSynthesizerRecipeContainer("crystal_fluid", ModItems.CRYSTAL_SEED_RESONATOR_CRYSTAL, new ItemStack(Items.AMETHYST_SHARD, 16), new FluidStack(FluidVariant.of(Fluids.WATER), 1000), new FluidStack(FluidVariant.of(ModFluids.STILL_CRYSTAL_FLUID), 1000), 100, 100)
    );

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    public void generateCrystalFluidCauldronRecipes(Consumer<RecipeJsonProvider> consumer) {
        crystalCauldronRecipes.forEach(recipe -> {
            List<Ingredient> ingredientList = new ArrayList<>(List.of(Ingredient.ofItems(recipe.baseItem)));

            if (recipe.secondItem != null) {
                ingredientList.add(Ingredient.ofItems(recipe.secondItem));
            }

            CrystalFluidCauldronRecipeJsonBuilder
                    .create(
                            ingredientList,
                            recipe.result,
                            recipe.count
                    )
                    .ticks(recipe.ticks)
                    .offerTo(consumer, Constants.id(String.format("cauldron_%s_unfinished_crystal", recipe.recipeId)));
        });
    }

    public void generateCoolingFluidCauldronRecipes(Consumer<RecipeJsonProvider> consumer) {
        coolingCauldronRecipes.forEach(recipe -> {
            List<Ingredient> ingredientList = new ArrayList<>(List.of(Ingredient.ofItems(recipe.baseItem)));

            CoolingFluidCauldronRecipeJsonBuilder
                    .create(
                            ingredientList,
                            recipe.result,
                            recipe.count
                    )
                    .ticks(recipe.ticks)
                    .coolingScore(recipe.coolingScore)
                    .offerTo(consumer, Constants.id(String.format("cooling_cauldron_%s", recipe.recipeId)));
        });
    }

    public void generateForgeRecipes(Consumer<RecipeJsonProvider> consumer) {
        forgeRecipes.forEach(recipe -> {
            List<Ingredient> ingredientList = new ArrayList<>(List.of(
                    Ingredient.ofItems(recipe.baseItem),
                    Ingredient.ofStacks(recipe.secondItem)
            ));

            if (recipe.dyeItem != null) {
                ingredientList.add(Ingredient.ofStacks(recipe.dyeItem));
            }

            ResonanceForgeRecipeJsonBuilder
                    .create(
                            ingredientList,
                            recipe.result,
                            recipe.count
                    )
                    .ticks(recipe.ticks)
                    .energyPerTick(recipe.energyPerTicks)
                    .offerTo(consumer, Constants.id(String.format("resonance_forge_%s", recipe.recipeId)));
        });
    }

    public void generateFluidSynthesizerRecipes(Consumer<RecipeJsonProvider> consumer) {
        fluidSynthesizerRecipes.forEach(recipe -> {
            List<Ingredient> ingredientList = new ArrayList<>(List.of(
                    Ingredient.ofItems(recipe.baseItem),
                    Ingredient.ofStacks(recipe.secondItem)
            ));

            FluidSynthesizerRecipeJsonBuilder
                    .create(ingredientList)
                    .ticks(recipe.ticks)
                    .energyPerTick(recipe.energyPerTicks)
                    .inputFluid(recipe.inputFluid)
                    .outputFluid(recipe.result)
                    .offerTo(consumer, Constants.id(String.format("fluid_synthesizer_%s", recipe.recipeId)));
        });
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        generateForgeRecipes(consumer);

        generateCrystalFluidCauldronRecipes(consumer);
        generateCoolingFluidCauldronRecipes(consumer);
        generateFluidSynthesizerRecipes(consumer);
    }

    private record CrystalRecipeContainer(String recipeId, Item baseItem, @Nullable Item secondItem, Item result, int count, int ticks) {}
    private record CoolingRecipeContainer(String recipeId, Item baseItem, Item result, int count, int ticks, int coolingScore) {}
    private record ForgeRecipeContainer(String recipeId, Item baseItem, ItemStack secondItem, @Nullable ItemStack dyeItem, Item result, int count, int ticks, int energyPerTicks) {}
    private record FluidSynthesizerRecipeContainer(String recipeId, Item baseItem, ItemStack secondItem, FluidStack inputFluid, FluidStack result, int ticks, int energyPerTicks) {}
}