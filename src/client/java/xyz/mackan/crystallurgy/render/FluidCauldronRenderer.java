package xyz.mackan.crystallurgy.render;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.*;
import xyz.mackan.crystallurgy.registry.ModCauldron;
import xyz.mackan.crystallurgy.registry.ModFluids;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.List;

public class FluidCauldronRenderer<T extends BlockEntity & ImplementedInventory> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;

    public FluidCauldronRenderer() {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    public FluidCauldronRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    public int getFluidColor(Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        return handler.getFluidColor(null, null, fluid.getDefaultState());
    }

    protected double getFluidHeight(BlockState state) {
        return (6.0 + (double)state.get(ModCauldron.FLUID_LEVEL) * 3.0) / 16.0;
    }

    public <T extends FluidCauldronBlockEntity> void renderLiquid(T entity, float tickDelta, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int liquidLevel = entity.getFluidProgress();
        if (liquidLevel == 0)
            return;

        BlockState state = entity.getWorld().getBlockState(entity.getPos());

        Fluid fluid = entity.getFluid();

        int color = getFluidColor(fluid);
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int alpha = 255;

        Identifier waterTextureId = new Identifier("minecraft", "block/water_still");

        Sprite[] sprites = FluidRenderHandlerRegistry.INSTANCE.get(Fluids.WATER.getStill()).getFluidSprites(null, null, fluid.getDefaultState());

        Sprite waterSprite = sprites[0];

        // Bind the texture manually (use MinecraftClient's texture manager)
        MinecraftClient.getInstance().getTextureManager().bindTexture(waterTextureId);

        float size = 0.125f;
        float u0 = waterSprite.getMinU();
        float v0 = waterSprite.getMinV();
        float u1 = waterSprite.getMaxU();
        float v1 = waterSprite.getMaxV();

        matrices.push();
        matrices.translate(0, getFluidHeight(state) + 0.001, 0);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();

        float sizeFactor = 0.125f;
        float maxV = sizeFactor; // Adjust texture size along V-axis
        float minV = 1.0f - sizeFactor; // Adjust texture size along V-axis

        // Render the texture manually with appropriate UVs
        consumer.vertex(positionMatrix, size, 0, 1 - size).color(red, green, blue, alpha).texture(u0, v1).light(light).overlay(overlay).normal(normalMatrix, 0, 1, 0).next();
        consumer.vertex(positionMatrix, 1 - size, 0, 1 - size).color(red, green, blue, alpha).texture(u1, v1).light(light).overlay(overlay).normal(normalMatrix, 0, 1, 0).next();
        consumer.vertex(positionMatrix, 1 - size, 0, size).color(red, green, blue, alpha).texture(u1, v0).light(light).overlay(overlay).normal(normalMatrix, 0, 1, 0).next();
        consumer.vertex(positionMatrix, size, 0, size).color(red, green, blue, alpha).texture(u0, v0).light(light).overlay(overlay).normal(normalMatrix, 0, 1, 0).next();

        matrices.pop();
    }


    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity instanceof FluidCauldronBlockEntity) {
            renderLiquid((FluidCauldronBlockEntity) blockEntity, tickDelta, matrices, vertexConsumers, light, overlay);
        }

        List<ItemStack> items = blockEntity.getItems();

        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty()) {
                matrices.push();

                matrices.translate(0.5F, 0.5F, 0.5F); // Center the items
                matrices.translate(0.0F, 0.0F, (i - 0.5F) * 0.25F); // Offset each item vertically
                matrices.scale(0.75F, 0.75F, 0.75F);  // Scale the items to fit inside the cauldron

                // Render the item
                itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, MinecraftClient.getInstance().world, 0);

                matrices.pop();
            }
        }
    }
}
