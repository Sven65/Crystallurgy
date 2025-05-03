package xyz.mackan.crystallurgy.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.gui.renderer.FluidStackRenderer;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.util.FluidStack;
import xyz.mackan.crystallurgy.util.FluidUtils;

import java.util.List;

public class FluidSynthesizerScreen extends HandledScreen<FluidSynthesizerScreenHandler> {
    private static final Identifier TEXTURE = Constants.id("textures/gui/fluid_synthesizer.png");
    private final FluidSynthesizerScreenHandler handler;

    private static final GUIElement ENERGY_BAR = new GUIElement(162, 9, 5, 64);
    private static final GUIElement PROGRESS_ARROW = new GUIElement(66, 49, 57, 17);

    private FluidStackRenderer inputFluidRenderer;
    private FluidStackRenderer outputFluidRenderer;

    private final EnergyStorage energy;

    public FluidSynthesizerScreen(FluidSynthesizerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        this.energy = handler.synthesizerBlockEntity.energyStorage;
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;

        assignFluidStackRenderers();
    }

    private void assignFluidStackRenderers() {
        inputFluidRenderer = new FluidStackRenderer(FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 20, true, 15, 66);
        outputFluidRenderer = new FluidStackRenderer(FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 20, true, 15, 66);
    }

    public Text getTooltips() {
        return Text.literal(energy.getAmount()+"/"+energy.getCapacity()+" E");
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

        inputFluidRenderer.drawFluid(context, handler.inputFluidStack, x + 51, y + 9, 9, 66,
                FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 20);

        outputFluidRenderer.drawFluid(context, handler.outputFluidStack, x + 129, y + 9, 9, 66,
                FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 20);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderEnergyAreaTooltips(context, mouseX, mouseY, x, y);
        renderInputFluidTooltips(context, mouseX, mouseY, x, y, handler.inputFluidStack.toDecoded(), 51, 9, inputFluidRenderer);
        renderOutputFluidTooltips(context, mouseX, mouseY, x, y, handler.outputFluidStack.toDecoded(), 129, 9, outputFluidRenderer);
    }

    private void renderOutputFluidTooltips(DrawContext context, int mouseX, int mouseY, int x, int y, FluidUtils.DecodedFluid fluidStack, int offsetX, int offsetY, FluidStackRenderer renderer) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, offsetX, offsetY, renderer)) {
            List<OrderedText> tooltip = renderer.getOrderedTooltip(fluidStack, TooltipContext.Default.BASIC);
            this.setTooltip(tooltip);
            drawMouseoverTooltip(context, x, y);
        }
    }

    private void renderInputFluidTooltips(DrawContext context, int mouseX, int mouseY, int x, int y, FluidUtils.DecodedFluid fluidStack, int offsetX, int offsetY, FluidStackRenderer renderer) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, offsetX, offsetY, renderer)) {
            List<OrderedText> tooltip = renderer.getOrderedTooltip(fluidStack, TooltipContext.Default.BASIC);
            this.setTooltip(tooltip);
            drawMouseoverTooltip(context, x, y);
        }
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // For output fluid
        if (isMouseAboveArea((int) mouseX, (int) mouseY, x, y, 129, 9, outputFluidRenderer)) {
            ItemStack held = client.player.currentScreenHandler.getCursorStack();

            if ((held.getItem() == Items.BUCKET)) {
                FluidStack outputFluid = handler.outputFluidStack;

                if (!outputFluid.fluidVariant.isBlank() && outputFluid.amount >= 1000) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(handler.synthesizerBlockEntity.getPos());
                    buf.writeString("output");
                    ClientPlayNetworking.send(ModMessages.EXTRACT_FLUID_TO_CURSOR, buf);

                    Item newItem = outputFluid.fluidVariant.getFluid().getBucketItem();

                    client.player.currentScreenHandler.setCursorStack(new ItemStack(newItem));
                }

                // Send to server
                return true;
            }
        }

        // For input fluid (if needed)
        if (isMouseAboveArea((int) mouseX, (int) mouseY, x, y, 51, 9, inputFluidRenderer)) {
            ItemStack held = client.player.currentScreenHandler.getCursorStack();
            if ((held.getItem() == Items.BUCKET)) {
                FluidStack inputFluid = handler.inputFluidStack;

                if (!inputFluid.fluidVariant.isBlank() && inputFluid.amount >= 1000) {
                    Item newItem = inputFluid.fluidVariant.getFluid().getBucketItem();

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(handler.synthesizerBlockEntity.getPos());
                    buf.writeString("input");
                    ClientPlayNetworking.send(ModMessages.EXTRACT_FLUID_TO_CURSOR, buf);

                    client.player.currentScreenHandler.setCursorStack(new ItemStack(newItem));
                }

                // Send to server
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, FluidStackRenderer renderer) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
