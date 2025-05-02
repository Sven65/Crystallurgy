package xyz.mackan.crystallurgy.forge.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.forge.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.forge.util.ModEnergyStorage;
import xyz.mackan.crystallurgy.gui.GUIElement;
import xyz.mackan.crystallurgy.gui.MouseUtil;

public class ResonanceForgeScreen extends HandledScreen<ResonanceForgeScreenHandler> {
    private static final Identifier TEXTURE = Constants.id("textures/gui/resonance_forge.png");
    private final ResonanceForgeScreenHandler handler;

    private final ModEnergyStorage energy;

    private static final GUIElement ENERGY_BAR = new GUIElement(162, 9, 5, 64);
    private static final GUIElement PROGRESS_ARROW = new GUIElement(75, 35, 22, 17);

    public ResonanceForgeScreen(ResonanceForgeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        this.energy = handler.forgeBlockEntity.ENERGY_STORAGE;
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    public Text getTooltips() {
        return Text.literal(energy.getEnergyStored()+"/"+energy.getMaxEnergyStored()+" E");
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        renderProgressArrow(context, x, y);
        renderEnergyBar(context, x, y);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderEnergyAreaTooltips(context, mouseX, mouseY, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if (handler.isCrafting()) {
            context.drawTexture(TEXTURE,
                    x + PROGRESS_ARROW.x(), y + PROGRESS_ARROW.y(),
                    176, 0,
                    handler.getScaledProgress(), PROGRESS_ARROW.height()
            );
        }
    }

    private void renderEnergyBar(DrawContext context, int x, int y) {
        int barBottom = y + ENERGY_BAR.y() + ENERGY_BAR.height();  // Bottom of the energy bar
        int barTop = barBottom - handler.getScaledEnergyBar();      // Top of the energy bar (drawn upwards)

        context.fill(x + ENERGY_BAR.x(), barTop,
                x + ENERGY_BAR.x() + ENERGY_BAR.width(),
                barBottom,
                0x80FF0000);
    }

    private void renderEnergyAreaTooltips(DrawContext context, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, ENERGY_BAR.x(), ENERGY_BAR.y(), ENERGY_BAR.width(), ENERGY_BAR.height())) {
            this.setTooltip(getTooltips());
            drawMouseoverTooltip(context, x, y);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
