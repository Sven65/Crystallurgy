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
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.forge.client.compat.jei.EmptyBackground;
import xyz.mackan.crystallurgy.forge.client.compat.jei.ModJEIRecipeTypes;
import xyz.mackan.crystallurgy.forge.registry.ForgeModCauldron;
import xyz.mackan.crystallurgy.forge.registry.ForgeModFluids;
import xyz.mackan.crystallurgy.forge.registry.ForgeModTags;
import xyz.mackan.crystallurgy.gui.GUIElement;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.registry.ModProperties;

import java.text.NumberFormat;
import java.util.List;

public class CrystalFluidCauldronCategory implements IRecipeCategory<CrystalFluidCauldronRecipe> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

    public static final Identifier ARROWS_TEXTURE = Constants.id("textures/gui/arrows.png");
    public static final Identifier INFO_TEXTURE = Identifier.of(ModIds.JEI_ID, "textures/jei/atlas/gui/icons/info.png");


    private final IDrawable background;
    private final IDrawable icon;
    private final List<Block> heaterBlocks;

    private int currentHeaterIndex = 0;
    private long lastSwitchTime = 0;
    private static final int SWITCH_INTERVAL_TICKS = 20; // every second

    private final GUIElement infoIcon;


    public CrystalFluidCauldronCategory(IGuiHelper helper) {
        this.background = new EmptyBackground(176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ForgeModCauldron.CRYSTAL_CAULDRON.get()));
        this.heaterBlocks = MinecraftClient.getInstance().world.getRegistryManager()
                .get(RegistryKeys.BLOCK)
                .getEntryList(ForgeModTags.FLUID_CAULDRON_HEATERS)
                .map(entryList -> entryList.stream()
                        .map(RegistryEntry::value)
                        .toList())
                .orElse(List.of());

        this.infoIcon = new GUIElement(this.getWidth() - 16, 8, 8, 8);
    }

    @Override
    public RecipeType<CrystalFluidCauldronRecipe> getRecipeType() {
        return ModJEIRecipeTypes.CRYSTAL_FLUID_CAULDRON;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.crystallurgy.crystal_fluid_cauldron");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrystalFluidCauldronRecipe fluidCauldronRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 16).addFluidStack(ForgeModFluids.STILL_CRYSTAL_FLUID.get().getStill(), 1000).addRichTooltipCallback(this::getFluidTooltip);
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 16).addIngredients(fluidCauldronRecipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 51, 16).addIngredients(fluidCauldronRecipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 48).addItemStack(fluidCauldronRecipe.getOutput(null));
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

    @Override
    public void draw(CrystalFluidCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);

        guiGraphics.drawTexture(INFO_TEXTURE, this.infoIcon.x(), this.infoIcon.y(), 0, 0, this.infoIcon.width(), this.infoIcon.height(), 8, 8);


        guiGraphics.drawTexture(ARROWS_TEXTURE, 73 - 4, 20, 0, 0, 16, 16);
        guiGraphics.drawTexture(ARROWS_TEXTURE, 93, 48, 16, 0, 16, 16);


        BlockState cauldron = ForgeModCauldron.COOLING_CAULDRON.get().getDefaultState().with(ModProperties.FLUID_LEVEL, 3);

        renderBlockInGui(guiGraphics, cauldron, 75 + 8, 35 + 16, 1, 16);
        renderHeatingBlock(guiGraphics, 75 + 8, 35 + 32, 0, 16);
    }

    public void renderHeatingBlock(DrawContext guiGraphics, int x, int y, int extraZ, float scale) {
        long time = MinecraftClient.getInstance().world.getTime();
        if (time - lastSwitchTime >= SWITCH_INTERVAL_TICKS) {
            lastSwitchTime = time;
            currentHeaterIndex = (currentHeaterIndex + 1) % heaterBlocks.size();
        }

        Block currentBlock = heaterBlocks.get(currentHeaterIndex);
        BlockState blockState = currentBlock.getDefaultState();

        if (currentBlock instanceof FluidBlock) {
            blockState = blockState.with(FluidBlock.LEVEL, 8);
        }

        renderBlockInGui(guiGraphics, blockState, x, y, extraZ, scale);
    }

    public void renderBlockInGui(DrawContext context, BlockState state, int x, int y, int extraZ, float scale) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockRenderManager blockRenderManager = client.getBlockRenderManager();
        MatrixStack matrices = context.getMatrices();

        matrices.push();

        // Move to position in GUI
        matrices.translate(x, y, 100.0 + extraZ);
        matrices.scale(scale, scale, scale);

        // Center and rotate
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180)); // flip to face forward
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30));  // slight tilt
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45));  // slight tilt


        // Enable lighting & depth
        DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.enableDepthTest();
        VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();

        if (state.getFluidState() != null) {
            // Get model & render
            BakedModel model = blockRenderManager.getModel(state);


            blockRenderManager.getModelRenderer().render(
                    matrices.peek(),
                    vertexConsumers.getBuffer(RenderLayers.getBlockLayer(state)),
                    state,
                    model,
                    1f, 1f, 1f,
                    0xF000F0,
                    OverlayTexture.DEFAULT_UV
            );

            vertexConsumers.draw(); // flush

        } else {
            // TODO: Render fluid blocks in gui
        }


        matrices.pop();
        RenderSystem.disableDepthTest();
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CrystalFluidCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= this.infoIcon.x() && mouseX <= this.infoIcon.x() + this.infoIcon.width() && mouseY >= this.infoIcon.y() && mouseY <= this.infoIcon.y() + this.infoIcon.height()) {
            List<Text> tooltips = List.of(
                    Text.translatable("text.crystallurgy.recipe.time", nf.format(recipe.getTicks() / 20))
            );

            tooltip.addAll(tooltips);
        }
    }
}
