package xyz.mackan.crystallurgy.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.fabric.constants.FabricTypes;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
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
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.compat.jei.EmptyBackground;
import xyz.mackan.crystallurgy.compat.jei.ModJEIRecipeTypes;
import xyz.mackan.crystallurgy.datagen.ModBlockTagProvider;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.registry.ModCauldron;
import xyz.mackan.crystallurgy.registry.ModFluids;
import xyz.mackan.crystallurgy.util.FluidStack;

import java.text.NumberFormat;
import java.util.List;

public class CoolingFluidCauldronCategory implements IRecipeCategory<CoolingFluidCauldronRecipe> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

    private final IDrawable background;
    private final IDrawable icon;

    private static final int ICON_SIZE = 16;

    private int currentHeaterIndex = 0;
    private long lastSwitchTime = 0;
    private static final int SWITCH_INTERVAL_TICKS = 20; // every second

    private final List<Block> coolingBlocks;


    public CoolingFluidCauldronCategory(IGuiHelper helper) {
        this.background = new EmptyBackground(176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModCauldron.COOLING_CAULDRON));

        this.coolingBlocks = MinecraftClient.getInstance().world.getRegistryManager()
                .get(RegistryKeys.BLOCK)
                .getEntryList(ModBlockTagProvider.COOLING_BLOCKS)
                .map(entryList -> entryList.stream()
                        .map(RegistryEntry::value)
                        .toList())
                .orElse(List.of());
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
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 16).addFluidStack(ModFluids.STILL_COOLING_FLUID.getStill(), FluidStack.convertMbToDroplets(1000)).addRichTooltipCallback(this::getFluidTooltip);
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 16).addIngredients(fluidCauldronRecipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 48).addItemStack(fluidCauldronRecipe.getOutput(null));
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


    // TODO: Render cooling blocks around central cauldron in cardinal directions except up
    @Override
    public void draw(CoolingFluidCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);

        // 2) draw central cauldron icon
        int cx = (this.getWidth()  - ICON_SIZE) / 2;
        int cy = (this.getHeight() - ICON_SIZE) / 2;

        renderBlockInGui(guiGraphics, Blocks.CAULDRON.getDefaultState(), cx, cy, 0, ICON_SIZE);

        // 3) fetch your ghost‐block state list

        // 4) define the six cardinal offsets (dx, dy)
        int[][] offsets = {
                {  0, -ICON_SIZE - 4},   // north
                { ICON_SIZE + 4,  0},    // east
                {  0,  ICON_SIZE + 4},   // south
                {-ICON_SIZE - 4,  0},    // west
                {  0, -2*(ICON_SIZE + 4)}, // up
                {  0,  2*(ICON_SIZE + 4)}  // down
        };

        // 5) for each direction, draw the slot background then render the block
        for (int i = 0; i < offsets.length; i++) {
            int x = cx + offsets[i][0];
            int y = cy + offsets[i][1];

            // slot‐style backdrop
            //guiGraphics.drawDrawable(guiHelper.getSlotDrawable(), x, y);

            // render *each* possible cooling‐block state
            for (Block block : this.coolingBlocks) {
                BlockState state = block.getDefaultState();
                // extraZ to control layering, scale to fit in 16×16
                renderBlockInGui(guiGraphics, state, x + 1, y + 1, 50, 1.0f);
            }
        }
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
            // TODO: Render fluids in gui
        }


        matrices.pop();
        RenderSystem.disableDepthTest();
    }

}
