package xyz.mackan.crystallurgy.registry;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

import java.util.Collection;

public class ModRecipes {


    public static void register() {
       Registry.register(
                Registries.RECIPE_SERIALIZER,
                new Identifier(Crystallurgy.MOD_ID, ResonanceForgeRecipe.Serializer.ID),
                ResonanceForgeRecipe.Serializer.INSTANCE
        );

        Registry.register(Registries.RECIPE_TYPE, new Identifier(Crystallurgy.MOD_ID, ResonanceForgeRecipe.Type.ID), ResonanceForgeRecipe.Type.INSTANCE);

        Registry.register(
                Registries.RECIPE_SERIALIZER,
                new Identifier(Crystallurgy.MOD_ID, CrystalFluidCauldronRecipe.Serializer.ID),
                CrystalFluidCauldronRecipe.Serializer.INSTANCE
        );

        Registry.register(Registries.RECIPE_TYPE, new Identifier(Crystallurgy.MOD_ID, CrystalFluidCauldronRecipe.Type.ID), CrystalFluidCauldronRecipe.Type.INSTANCE);

        Registry.register(
                Registries.RECIPE_SERIALIZER,
                new Identifier(Crystallurgy.MOD_ID, CoolingFluidCauldronRecipe.Serializer.ID),
                CoolingFluidCauldronRecipe.Serializer.INSTANCE
        );

        Registry.register(Registries.RECIPE_TYPE, new Identifier(Crystallurgy.MOD_ID, CoolingFluidCauldronRecipe.Type.ID), CoolingFluidCauldronRecipe.Type.INSTANCE);
    }
}
