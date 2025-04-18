package xyz.mackan.crystallurgy.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.registry.ModItems;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        ResonanceForgeRecipeJsonBuilder
                .create(
                        List.of(Ingredient.ofItems(ModItems.DIAMOND_RESONATOR_CRYSTAL), Ingredient.ofItems(Items.COAL_BLOCK)),
                        Items.DIAMOND,
                        1
                )
                .ticks(60 * 20)
                .energyPerTick(100)
                .offerTo(consumer, new Identifier(Crystallurgy.MOD_ID, "resonance_forge_diamond"));

        CrystalFluidCauldronRecipeJsonBuilder
                .create(
                        List.of(Ingredient.ofItems(ModItems.CRYSTAL_SEED)),
                        ModItems.DIAMOND_RESONATOR_CRYSTAL,
                        1
                )
                .ticks(100)
                .offerTo(consumer, new Identifier(Crystallurgy.MOD_ID, "cauldron_diamond_crystal"));

    }
}
