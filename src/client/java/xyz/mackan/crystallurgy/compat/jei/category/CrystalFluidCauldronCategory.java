package xyz.mackan.crystallurgy.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.compat.jei.EmptyBackground;
import xyz.mackan.crystallurgy.compat.jei.ModJEIRecipeTypes;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.registry.ModCauldron;

public class CrystalFluidCauldronCategory implements IRecipeCategory<CrystalFluidCauldronRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public CrystalFluidCauldronCategory(IGuiHelper helper) {
        this.background = new EmptyBackground(176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModCauldron.COOLING_CAULDRON));
    }

    @Override
    public RecipeType<CrystalFluidCauldronRecipe> getRecipeType() {
        return ModJEIRecipeTypes.CRYSTAL_FLUID_CAULDRON;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.crystallurgy.fluid_cauldron");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, CrystalFluidCauldronRecipe fluidCauldronRecipe, IFocusGroup iFocusGroup) {

    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void draw(CrystalFluidCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();

        background.draw(guiGraphics, 0, 0);
    }
}
