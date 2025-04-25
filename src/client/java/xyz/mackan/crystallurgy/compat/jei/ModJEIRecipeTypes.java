package xyz.mackan.crystallurgy.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

public interface ModJEIRecipeTypes {
    RecipeType<ResonanceForgeRecipe> RESONANCE_FORGE = RecipeType.create(Crystallurgy.MOD_ID, "resonance_forging", ResonanceForgeRecipe.class);
    RecipeType<FluidSynthesizerRecipe> FLUID_SYNTHESIZER = RecipeType.create(Crystallurgy.MOD_ID, "fluid_synthesizing", FluidSynthesizerRecipe.class);
}