package xyz.mackan.crystallurgy.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

public class FabricModRecipes {
    public static void register() {
        Registry.register(
                Registries.RECIPE_SERIALIZER,
                Constants.id(ResonanceForgeRecipe.Serializer.ID),
                ResonanceForgeRecipe.Serializer.INSTANCE
        );

        Registry.register(Registries.RECIPE_TYPE, Constants.id(ResonanceForgeRecipe.Type.ID), ResonanceForgeRecipe.Type.INSTANCE);

        Registry.register(
                Registries.RECIPE_SERIALIZER,
                Constants.id(CrystalFluidCauldronRecipe.Serializer.ID),
                CrystalFluidCauldronRecipe.Serializer.INSTANCE
        );

        Registry.register(Registries.RECIPE_TYPE, Constants.id(CrystalFluidCauldronRecipe.Type.ID), CrystalFluidCauldronRecipe.Type.INSTANCE);

        Registry.register(
                Registries.RECIPE_SERIALIZER,
                Constants.id(CoolingFluidCauldronRecipe.Serializer.ID),
                CoolingFluidCauldronRecipe.Serializer.INSTANCE
        );

        Registry.register(Registries.RECIPE_TYPE, Constants.id(CoolingFluidCauldronRecipe.Type.ID), CoolingFluidCauldronRecipe.Type.INSTANCE);

        Registry.register(
                Registries.RECIPE_SERIALIZER,
                Constants.id(FluidSynthesizerRecipe.Serializer.ID),
                FluidSynthesizerRecipe.Serializer.INSTANCE
        );

        Registry.register(Registries.RECIPE_TYPE, Constants.id(FluidSynthesizerRecipe.Type.ID), FluidSynthesizerRecipe.Type.INSTANCE);
    }
}
