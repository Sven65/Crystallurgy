package xyz.mackan.crystallurgy.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResonanceForgeDisplay extends BasicDisplay {
    public ResonanceForgeDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public ResonanceForgeDisplay(@Nullable ResonanceForgeRecipe recipe) {
        super(getInputList(recipe), recipe == null ? Collections.emptyList() : Collections.singletonList(EntryIngredients.of(recipe.getOutput(null))));
    }

    private static List<EntryIngredient> getInputList(@Nullable  ResonanceForgeRecipe recipe) {
        if (recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(0)));

        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ResonanceForgeCategory.RESONANCE_FORGE;
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}
