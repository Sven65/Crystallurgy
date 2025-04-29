package xyz.mackan.crystallurgy.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.fabric.ingredients.fluids.JeiFluidIngredient;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.ResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.compat.jei.ModJEIRecipeTypes;
import xyz.mackan.crystallurgy.gui.renderer.FluidStackRenderer;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.registry.ModBlocks;
import xyz.mackan.crystallurgy.util.FluidStack;
import xyz.mackan.crystallurgy.util.GUIElement;
import xyz.mackan.crystallurgy.util.MouseUtil;

import java.text.NumberFormat;
import java.util.List;

public class FluidSynthesizerCategory implements IRecipeCategory<FluidSynthesizerRecipe> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    public static final Identifier TEXTURE = new Identifier(Crystallurgy.MOD_ID, "textures/gui/fluid_synthesizer.png");

    private final IDrawable background;
    private final IDrawable icon;

    private static final GUIElement ENERGY_BAR = new GUIElement(162, 9, 5, 64);

    private FluidStackRenderer inputFluidRenderer;
    private FluidStackRenderer outputFluidRenderer;

    public FluidSynthesizerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FLUID_SYNTHESIZER));
    }

    @Override
    public RecipeType<FluidSynthesizerRecipe> getRecipeType() {
        return ModJEIRecipeTypes.FLUID_SYNTHESIZER;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.crystallurgy.fluid_synthesizer");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    private void assignFluidStackRenderers() {
        inputFluidRenderer = new FluidStackRenderer(FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 20, true, 15, 66);
        outputFluidRenderer = new FluidStackRenderer(FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 20, true, 15, 66);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidSynthesizerRecipe fluidSynthesizerRecipe, IFocusGroup iFocusGroup) {
        FluidStack outputFluid = fluidSynthesizerRecipe.getOutputFluid();

        builder.addSlot(RecipeIngredientRole.INPUT, 70, 9).addItemStack(fluidSynthesizerRecipe.getIngredientAtSlot(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 104, 9).addItemStack(fluidSynthesizerRecipe.getIngredientAtSlot(1));
        builder.addSlot(RecipeIngredientRole.OUTPUT).addFluidStack(outputFluid.fluidVariant.getFluid(), outputFluid.amount).setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 7, 54).addItemStack(outputFluid.fluidVariant.getFluid().getBucketItem().getDefaultStack());

        assignFluidStackRenderers();
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
    public void draw(FluidSynthesizerRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();

        background.draw(guiGraphics, 0, 0);
        drawEnergy(recipe, guiGraphics, mouseX, mouseY);

        inputFluidRenderer.drawFluid(guiGraphics, recipe.getInputFluid(), 51, 9, 9, 66,
                FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 20);

        outputFluidRenderer.drawFluid(guiGraphics, recipe.getOutputFluid(),  129, 9, 9, 66,
                FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 20);



        if (isMouseAboveArea((int) mouseX, (int) mouseY, 51, 9, inputFluidRenderer)) {
            List<Text> tooltip = inputFluidRenderer.getTooltip(recipe.getInputFluid(), TooltipContext.Default.BASIC);
            guiGraphics.drawTooltip(client.textRenderer, tooltip, (int) mouseX, (int) mouseY);
        }

        if (isMouseAboveArea((int) mouseX, (int) mouseY, 129, 9, outputFluidRenderer)) {
            List<Text> tooltip = outputFluidRenderer.getTooltip(recipe.getOutputFluid(), TooltipContext.Default.BASIC);
            guiGraphics.drawTooltip(client.textRenderer, tooltip, (int) mouseX, (int) mouseY);
        }
    }

    private int getScaledEnergyBar(int storedEnergy) {
        long maxEnergy = ResonanceForgeBlockEntity.ENERGY_CAPACITY;
        int energyBarSize = ENERGY_BAR.height();

        return Math.min(energyBarSize, (int) (maxEnergy != 0 && storedEnergy != 0 ? storedEnergy * energyBarSize / maxEnergy : 0));
    }

    private void drawEnergy(FluidSynthesizerRecipe recipe, DrawContext guiGraphics, double mouseX, double mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();

        int ticks = recipe.getTicks();
        int energyPerTick = recipe.getEnergyPerTick();
        int totalEnergy = ticks * energyPerTick;

        int barBottom = ENERGY_BAR.y() + ENERGY_BAR.height();  // Bottom of the energy bar
        int barTop = barBottom - getScaledEnergyBar(totalEnergy);

        guiGraphics.fill(ENERGY_BAR.x(), barTop,
                ENERGY_BAR.x() + ENERGY_BAR.width(),
                barBottom,
                0x80FF0000);

        if (mouseX >= ENERGY_BAR.x() && mouseX <= ENERGY_BAR.x() + ENERGY_BAR.width() && mouseY >= ENERGY_BAR.y() && mouseY <= ENERGY_BAR.y() + ENERGY_BAR.height()) {
            Text tooltipText = Text.translatable("text.crystallurgy.recipe.energy", nf.format(totalEnergy));
            guiGraphics.drawTooltip(client.textRenderer, tooltipText, (int) mouseX, (int) mouseY);
        }
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, FluidStackRenderer renderer) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x, y, renderer.getWidth(), renderer.getHeight());
    }
}
