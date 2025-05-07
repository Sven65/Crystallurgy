package xyz.mackan.crystallurgy.forge.client.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.forge.client.compat.jei.EmptyBackground;
import xyz.mackan.crystallurgy.forge.client.compat.jei.ModJEIRecipeTypes;
import xyz.mackan.crystallurgy.forge.registry.ForgeModCauldron;
import xyz.mackan.crystallurgy.forge.registry.ForgeModFluids;
import xyz.mackan.crystallurgy.forge.registry.ForgeModTags;
import xyz.mackan.crystallurgy.gui.GUIElement;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;

import java.text.NumberFormat;
import java.util.List;

public class CoolingFluidCauldronCategory implements IRecipeCategory<CoolingFluidCauldronRecipe> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

    private final IDrawable background;
    private final IDrawable icon;
    public static final Identifier ARROWS_TEXTURE = Constants.id("textures/gui/arrows.png");
    public static final Identifier INFO_TEXTURE = Identifier.of(ModIds.JEI_ID, "textures/jei/atlas/gui/icons/info.png");

    private int currentCoolerIndex = 0;
    private long lastSwitchTime = 0;
    private static final int SWITCH_INTERVAL_TICKS = 20; // every second

    private final List<Block> coolingBlocks;

    private final GUIElement infoIcon;

    private final int INPUT_SLOT_Y = 16;


    public CoolingFluidCauldronCategory(IGuiHelper helper) {
        this.background = new EmptyBackground(176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ForgeModCauldron.COOLING_CAULDRON.get()));

        this.coolingBlocks = MinecraftClient.getInstance().world.getRegistryManager()
                .get(RegistryKeys.BLOCK)
                .getEntryList(ForgeModTags.COOLING_BLOCKS)
                .map(entryList -> entryList.stream()
                        .map(RegistryEntry::value)
                        .toList())
                .orElse(List.of());

        this.infoIcon = new GUIElement(this.getWidth() - 16, 8, 8, 8);
    }

    @Override
    public RecipeType<CoolingFluidCauldronRecipe> getRecipeType() {
        return ModJEIRecipeTypes.COOLING_FLUID_CAULDRON;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.crystallurgy.cooling_fluid_cauldron");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CoolingFluidCauldronRecipe fluidCauldronRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 16).addFluidStack(ForgeModFluids.STILL_COOLING_FLUID.get().getStill(), 1000).addRichTooltipCallback(this::getFluidTooltip);
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 16).addIngredients(fluidCauldronRecipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112 + 16, 48).addItemStack(fluidCauldronRecipe.getOutput(null));
    }

    private void getFluidTooltip(IRecipeSlotView slotView, ITooltipBuilder tooltipBuilder) {
        slotView.getDisplayedIngredient(ForgeTypes.FLUID_STACK).ifPresent(fluidStack -> {
            // Add the amount in millibuckets (mB)
            MutableText amountString = Text.translatable("text.crystallurgy.tooltip.liquid.amount", nf.format(fluidStack.getAmount()));
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


    // TODO: Make the render of this better... (may god help the one who attempts this one.)
    // We want it to look like the lava mill in JEI from Extra Utils 2
    @Override
    public void draw(CoolingFluidCauldronRecipe recipe,
                     IRecipeSlotsView recipeSlotsView,
                     DrawContext guiGraphics,
                     double mouseX, double mouseY) {
        // — your existing JEI/GUIs —
        background.draw(guiGraphics, 0, 0);

        int ICON_SCALE = 16;
        int cx = 75 + 8;
        int cy = 35 + 16 + 16;

        guiGraphics.drawTexture(
                INFO_TEXTURE,
                this.infoIcon.x(), this.infoIcon.y(),
                0, 0,
                this.infoIcon.width(), this.infoIcon.height(),
                8, 8
        );
        guiGraphics.drawTexture(ARROWS_TEXTURE, 73 - 4, INPUT_SLOT_Y, 0, 0, 16, 16);
        guiGraphics.drawTexture(ARROWS_TEXTURE, 93 + 16, 48,        16, 0, 16, 16);

        // — begin block rendering —
        MinecraftClient client = MinecraftClient.getInstance();
        BlockRenderManager blockRenderer = client.getBlockRenderManager();
        MatrixStack matrices = guiGraphics.getMatrices();
        VertexConsumerProvider.Immediate buffers = guiGraphics.getVertexConsumers();

        RenderSystem.enableDepthTest();

        // Central lava cube
        renderGuiBlock(matrices, buffers, blockRenderer,
                Blocks.CAULDRON.getDefaultState(),
                cx, cy, 100, ICON_SCALE);

        long time = MinecraftClient.getInstance().world.getTime();
        if (time - lastSwitchTime >= SWITCH_INTERVAL_TICKS) {
            lastSwitchTime = time;
            currentCoolerIndex = (currentCoolerIndex + 1) % coolingBlocks.size();
        }

        Block currentBlock = coolingBlocks.get(currentCoolerIndex);
        BlockState blockState = currentBlock.getDefaultState();

        if (currentBlock instanceof FluidBlock) {
            blockState = blockState.with(FluidBlock.LEVEL, 8);
        }


        renderGuiBlock(matrices, buffers, blockRenderer,
                blockState,
                cx - ICON_SCALE, cy, 0, ICON_SCALE);

        renderGuiBlock(matrices, buffers, blockRenderer,
                blockState,
                cx + ICON_SCALE, cy, 0, ICON_SCALE);

        renderGuiBlock(matrices, buffers, blockRenderer,
                blockState,
                cx, cy + ICON_SCALE, 0, ICON_SCALE);

        // Flush and disable depth so the rest of JEI draws normally
        buffers.draw();
        RenderSystem.disableDepthTest();
        // — end block rendering —
    }

    // Helper to push, scale, and render a 16×16 block as a 'size'×'size' GUI cube
    private void renderGuiBlock(MatrixStack matrices,
                                VertexConsumerProvider.Immediate vertexConsumers,
                                BlockRenderManager blockRenderManager,
                                BlockState state,
                                int x, int y, int z,
                                int scale) {
        matrices.push();

        // 1) Translate into GUI-space and bring forward in Z
        matrices.translate(x, y, 100);

        // 2) Scale block down to desired size (100 = full block)
        matrices.translate(-8, -8, z);
        matrices.scale(scale, scale, scale);


        // 3) Tilt: 22.5° X and Y for pseudo-isometric look
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));

        // 4) Centre the 16×16 block around origin

        // 5) Get model and render with fullbright light
        BakedModel model = blockRenderManager.getModel(state);

        blockRenderManager.getModelRenderer().render(
                matrices.peek(),
                vertexConsumers.getBuffer(RenderLayers.getBlockLayer(state)),
                state,
                model,
                1f, 1f, 1f, // RGB
                LightmapTextureManager.MAX_LIGHT_COORDINATE, // full brightness
                OverlayTexture.DEFAULT_UV
        );

        matrices.pop();
    }


    @Override
    public void getTooltip(ITooltipBuilder tooltip, CoolingFluidCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= this.infoIcon.x() && mouseX <= this.infoIcon.x() + this.infoIcon.width() && mouseY >= this.infoIcon.y() && mouseY <= this.infoIcon.y() + this.infoIcon.height()) {
            List<Text> tooltips = List.of(
                    Text.translatable("text.crystallurgy.tooltip.required_cooling_score", recipe.getCoolingScore()),
                    Text.translatable("text.crystallurgy.recipe.time", nf.format(recipe.getTicks() / 20))
            );

            tooltip.addAll(tooltips);
        }
    }
}
