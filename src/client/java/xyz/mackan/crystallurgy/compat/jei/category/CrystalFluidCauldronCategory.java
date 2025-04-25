package xyz.mackan.crystallurgy.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import mezz.jei.api.fabric.ingredients.fluids.JeiFluidIngredient;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.compat.jei.EmptyBackground;
import xyz.mackan.crystallurgy.compat.jei.ModJEIRecipeTypes;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.registry.ModCauldron;
import xyz.mackan.crystallurgy.registry.ModFluids;
import xyz.mackan.crystallurgy.util.FluidStack;

import java.text.NumberFormat;
import java.util.Optional;

public class CrystalFluidCauldronCategory implements IRecipeCategory<CrystalFluidCauldronRecipe> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

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
    public void setRecipe(IRecipeLayoutBuilder builder, CrystalFluidCauldronRecipe fluidCauldronRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 35).addFluidStack(ModFluids.STILL_CRYSTAL_FLUID.getStill(), FluidStack.convertMbToDroplets(1000)).addRichTooltipCallback(this::getFluidTooltip);
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 35).addIngredients(fluidCauldronRecipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 51, 35).addIngredients(fluidCauldronRecipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 35).addItemStack(fluidCauldronRecipe.getOutput(null));
    }

    private void getFluidTooltip(IRecipeSlotView slotView, ITooltipBuilder tooltipBuilder) {
        slotView.getDisplayedIngredient(FabricTypes.FLUID_STACK).ifPresent(fluidStack -> {
            // Add the amount in millibuckets (mB)
            MutableText amountString = Text.translatable("text.crystallurgy.tooltip.liquid.amount", nf.format(FluidStack.convertDropletsToMb(fluidStack.getAmount())));
            tooltipBuilder.add(amountString.fillStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
        });
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
