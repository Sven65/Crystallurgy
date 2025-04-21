package xyz.mackan.crystallurgy.compat;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreen;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.registry.ModBlocks;
import xyz.mackan.crystallurgy.registry.ModRecipes;

public class CrystallurgyREIClientPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ResonanceForgeCategory());
        registry.addWorkstations(ResonanceForgeCategory.RESONANCE_FORGE, EntryStacks.of(ModBlocks.RESONANCE_FORGE));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(ResonanceForgeRecipe.class, ModRecipes.RESONANCE_FORGE_RECIPE_SERIALIZER, ResonanceForgeDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(75, 30, 20, 30), ResonanceForgeScreen.class, ResonanceForgeCategory.RESONANCE_FORGE);
    }
}
