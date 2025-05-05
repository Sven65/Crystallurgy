package xyz.mackan.crystallurgy.forge.client.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

public interface ModJEIRecipeTypes {
    RecipeType<ResonanceForgeRecipe> RESONANCE_FORGE = RecipeType.create(Constants.MOD_ID, "resonance_forging", ResonanceForgeRecipe.class);
    RecipeType<FluidSynthesizerRecipe> FLUID_SYNTHESIZER = RecipeType.create(Constants.MOD_ID, "fluid_synthesizing", FluidSynthesizerRecipe.class);
    RecipeType<CrystalFluidCauldronRecipe> CRYSTAL_FLUID_CAULDRON = RecipeType.create(Constants.MOD_ID, "crystal_fluid_cauldron", CrystalFluidCauldronRecipe.class);
    RecipeType<CoolingFluidCauldronRecipe> COOLING_FLUID_CAULDRON = RecipeType.create(Constants.MOD_ID, "cooling_fluid_cauldron", CoolingFluidCauldronRecipe.class);
}