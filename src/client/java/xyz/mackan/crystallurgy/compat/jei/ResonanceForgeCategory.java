package xyz.mackan.crystallurgy.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.registry.ModBlocks;

public class ResonanceForgeCategory implements IRecipeCategory<ResonanceForgeRecipe> {
    public static final Identifier UID = new Identifier(Crystallurgy.MOD_ID, "resonance_forge");
    public static final Identifier TEXTURE = new Identifier(Crystallurgy.MOD_ID, "textures/gui/resonance_forge.png");

    public static final RecipeType<ResonanceForgeRecipe> RESONANCE_FORGE_TYPE = new RecipeType<>(UID, ResonanceForgeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ResonanceForgeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.RESONANCE_FORGE));
    }

    @Override
    public RecipeType<ResonanceForgeRecipe> getRecipeType() {
        return RESONANCE_FORGE_TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.crystallurgy.resonance_forge");
    }
    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ResonanceForgeRecipe resonanceForgeRecipe, IFocusGroup iFocusGroup) {

    }

    @Override
    public void draw(ResonanceForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }
}
