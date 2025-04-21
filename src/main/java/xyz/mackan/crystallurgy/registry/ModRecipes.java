package xyz.mackan.crystallurgy.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

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
