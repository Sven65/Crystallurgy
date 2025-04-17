package xyz.mackan.crystallurgy.gui;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.util.GUIElement;

public class ResonanceForgeScreen extends HandledScreen<ResonanceForgeScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Crystallurgy.MOD_ID, "textures/gui/resonance_forge.png");
    private final ResonanceForgeScreenHandler handler;

    private static final GUIElement ENERGY_BAR = new GUIElement(162, 9, 5, 64);
    private static final GUIElement PROGRESS_ARROW = new GUIElement(75, 35, 22, 17);



    public ResonanceForgeScreen(ResonanceForgeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
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
        renderRFBar(context, x, y);
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

    private void renderRFBar(DrawContext context, int x, int y) {
        int barBottom = y + ENERGY_BAR.y() + ENERGY_BAR.height();  // Bottom of the energy bar
        int barTop = barBottom - handler.getScaledEnergyBar();      // Top of the energy bar (drawn upwards)

        context.fill(x + ENERGY_BAR.x(), barTop,
                x + ENERGY_BAR.x() + ENERGY_BAR.width(),
                barBottom,
                0x80FF0000);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}