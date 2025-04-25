package xyz.mackan.crystallurgy.compat.jei;

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
import org.jetbrains.annotations.NotNull;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreen;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.registry.ModBlocks;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return new Identifier(Crystallurgy.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESONANCE_FORGE), ModJEIRecipeTypes.RESONANCE_FORGE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ResonanceForgeCategory(helper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        assert MinecraftClient.getInstance().world != null;
        RecipeManager recipeManager = MinecraftClient.getInstance().world.getRecipeManager();

        Crystallurgy.LOGGER.info("type {}", ModJEIRecipeTypes.RESONANCE_FORGE);

        List<ResonanceForgeRecipe> forgeRecipes = recipeManager.listAllOfType(ResonanceForgeRecipe.Type.INSTANCE);
        registration.addRecipes(ModJEIRecipeTypes.RESONANCE_FORGE, forgeRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ResonanceForgeScreen.class, 75, 35, 22, 15,
                ModJEIRecipeTypes.RESONANCE_FORGE);
    }
}
