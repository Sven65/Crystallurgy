package xyz.mackan.crystallurgy.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.ResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.compat.jei.ModJEIRecipeTypes;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.registry.ModBlocks;
import xyz.mackan.crystallurgy.util.GUIElement;

import java.text.NumberFormat;

public class ResonanceForgeCategory implements IRecipeCategory<ResonanceForgeRecipe> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    public static final Identifier TEXTURE = new Identifier(Crystallurgy.MOD_ID, "textures/gui/resonance_forge.png");

    private final IDrawable background;
    private final IDrawable icon;

    private static final GUIElement ENERGY_BAR = new GUIElement(162, 9, 5, 64);


    public ResonanceForgeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.RESONANCE_FORGE));
    }

    @Override
    public RecipeType<ResonanceForgeRecipe> getRecipeType() {
        return ModJEIRecipeTypes.RESONANCE_FORGE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.crystallurgy.resonance_forge");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ResonanceForgeRecipe resonanceForgeRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 35).addItemStack(resonanceForgeRecipe.getIngredientAtSlot(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 35).addItemStack(resonanceForgeRecipe.getIngredientAtSlot(1));

        builder.addSlot(RecipeIngredientRole.INPUT, 51, 35).addItemStack(resonanceForgeRecipe.getIngredientAtSlot(2));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 35).addItemStack(resonanceForgeRecipe.getOutput(null));
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
    public void draw(ResonanceForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
        drawTimeText(recipe, guiGraphics);
        drawEnergy(recipe, guiGraphics, mouseX, mouseY);
    }

    private int getScaledEnergyBar(int storedEnergy) {
        long maxEnergy = ResonanceForgeBlockEntity.ENERGY_CAPACITY;
        int energyBarSize = ENERGY_BAR.height();

        return Math.min(energyBarSize, (int) (maxEnergy != 0 && storedEnergy != 0 ? storedEnergy * energyBarSize / maxEnergy : 0));
    }

    private void drawEnergy(ResonanceForgeRecipe recipe, DrawContext guiGraphics, double mouseX, double mouseY) {
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

    private void drawTimeText(ResonanceForgeRecipe recipe, DrawContext guiGraphics) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer font = client.textRenderer;

        // Retrieve your time and energy values from the recipe
        int time = recipe.getTicks(); // Time in ticks (20 ticks = 1 second)

        // Convert time to seconds (assuming it's in ticks)
        int timeInSeconds = time / 20;

        // Position where you want to display the text
        int x = 80; // X position of the text
        int y = 50; // Y position of the text

        // Draw the time
        Text timeText = Text.translatable("text.crystallurgy.recipe.time", nf.format(timeInSeconds));
        guiGraphics.drawText(font, timeText, x, y + 12, 0xFF7E7E7E, false);
    }

}
