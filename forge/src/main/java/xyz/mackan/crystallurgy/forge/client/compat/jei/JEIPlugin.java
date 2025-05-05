package xyz.mackan.crystallurgy.forge.client.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.forge.client.compat.jei.category.CoolingFluidCauldronCategory;
import xyz.mackan.crystallurgy.forge.client.compat.jei.category.CrystalFluidCauldronCategory;
import xyz.mackan.crystallurgy.forge.client.compat.jei.category.FluidSynthesizerCategory;
import xyz.mackan.crystallurgy.forge.client.compat.jei.category.ResonanceForgeCategory;
import xyz.mackan.crystallurgy.forge.client.gui.FluidSynthesizerScreen;
import xyz.mackan.crystallurgy.forge.client.gui.ResonanceForgeScreen;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlocks;
import xyz.mackan.crystallurgy.forge.registry.ForgeModCauldron;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Constants.id("jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ForgeModBlocks.RESONANCE_FORGE.get()), ModJEIRecipeTypes.RESONANCE_FORGE);
        registration.addRecipeCatalyst(new ItemStack(ForgeModBlocks.FLUID_SYNTHESIZER.get()), ModJEIRecipeTypes.FLUID_SYNTHESIZER);
        registration.addRecipeCatalyst(new ItemStack(ForgeModCauldron.CRYSTAL_CAULDRON.get()), ModJEIRecipeTypes.CRYSTAL_FLUID_CAULDRON);
        registration.addRecipeCatalyst(new ItemStack(ForgeModCauldron.COOLING_CAULDRON.get()), ModJEIRecipeTypes.COOLING_FLUID_CAULDRON);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ResonanceForgeCategory(helper),
                new FluidSynthesizerCategory(helper),
                new CrystalFluidCauldronCategory(helper),
                new CoolingFluidCauldronCategory(helper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        assert MinecraftClient.getInstance().world != null;
        RecipeManager recipeManager = MinecraftClient.getInstance().world.getRecipeManager();

        List<ResonanceForgeRecipe> forgeRecipes = recipeManager.listAllOfType(ResonanceForgeRecipe.Type.INSTANCE);
        registration.addRecipes(ModJEIRecipeTypes.RESONANCE_FORGE, forgeRecipes);

        List<FluidSynthesizerRecipe> synthesizerRecipes = recipeManager.listAllOfType(FluidSynthesizerRecipe.Type.INSTANCE);

        CrystallurgyCommon.LOGGER.info("Synth recipe count: {}", synthesizerRecipes.size());

        registration.addRecipes(ModJEIRecipeTypes.FLUID_SYNTHESIZER, synthesizerRecipes);

        List<CrystalFluidCauldronRecipe> crystalFluidCauldronRecipes = recipeManager.listAllOfType(CrystalFluidCauldronRecipe.Type.INSTANCE);
        registration.addRecipes(ModJEIRecipeTypes.CRYSTAL_FLUID_CAULDRON, crystalFluidCauldronRecipes);

        List<CoolingFluidCauldronRecipe> coolingFluidCauldronRecipes = recipeManager.listAllOfType(CoolingFluidCauldronRecipe.Type.INSTANCE);
        registration.addRecipes(ModJEIRecipeTypes.COOLING_FLUID_CAULDRON, coolingFluidCauldronRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ResonanceForgeScreen.class, 75, 35, 22, 15,
                ModJEIRecipeTypes.RESONANCE_FORGE);

        registration.addRecipeClickArea(FluidSynthesizerScreen.class, 66, 49, 57, 15,
                ModJEIRecipeTypes.FLUID_SYNTHESIZER);
    }
}
