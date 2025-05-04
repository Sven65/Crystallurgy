package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

public class ForgeModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ResonanceForgeRecipe>> RESONANCE_FORGE_SERIALIZER = SERIALIZERS.register("resonance_forging", () -> ResonanceForgeRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<FluidSynthesizerRecipe>> FLUID_SYNTH_SERIALIZER = SERIALIZERS.register("fluid_synthesizer", () -> FluidSynthesizerRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
