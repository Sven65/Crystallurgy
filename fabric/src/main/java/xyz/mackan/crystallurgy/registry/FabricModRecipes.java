package xyz.mackan.crystallurgy.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

public class FabricModRecipes {
    public static void register() {
        Registry.register(
                Registries.RECIPE_SERIALIZER,
                Constants.id(ResonanceForgeRecipe.Serializer.ID),
                ResonanceForgeRecipe.Serializer.INSTANCE
        );

        Registry.register(Registries.RECIPE_TYPE, Constants.id(ResonanceForgeRecipe.Type.ID), ResonanceForgeRecipe.Type.INSTANCE);

    }
}
