package xyz.mackan.crystallurgy.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import xyz.mackan.crystallurgy.blocks.FluidSynthesizerBlockEntity;

public class FluidSynthesizerRenderer implements BlockEntityRenderer<FluidSynthesizerBlockEntity> {

    @Override
    public void render(FluidSynthesizerBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Get the appropriate render layer, e.g., translucent for fluid
        RenderLayer renderLayer = RenderLayer.getCutout();

        // Create the VertexConsumer for the block, using the translucent render layer
        VertexConsumer consumer = vertexConsumers.getBuffer(renderLayer);

        // Set size for the fluid texture overlay, for example, rendering a 2x7 water texture
        float size = 0.5f; // Adjust the size for the overlay
        float textureX = 4.0f; // X coordinate in the texture atlas (water texture starts at (4, 4))
        float textureY = 4.0f; // Y coordinate in the texture atlas
        float maxWidth = 2.0f;  // Maximum width of the texture (2 tiles)
        float maxHeight = 7.0f; // Maximum height of the texture (7 tiles)

        // Define the normal vector (for flat surface, we use (0, 1, 0))
        float normalX = 0.0f;
        float normalY = 1.0f;
        float normalZ = 0.0f;

        BlockState state = blockEntity.getWorld().getBlockState(blockEntity.getPos());

        FluidVariant inputFluid = blockEntity.inputFluidStorage.variant;

        if (!inputFluid.isBlank()) {
            Fluid fluid = inputFluid.getFluid();

            int color = getFluidColor(fluid);
            int red = color >> 16 & 255;
            int green = color >> 8 & 255;
            int blue = color & 255;
            int alpha = 255;

            renderColorBar(matrices, consumer, 4, 4, 2, 4, red, green, blue, alpha, light, overlay, normalX, normalY, normalZ);
        }


        // Call the renderWaterTextureOverlay method to draw the water texture
    }

    public int getFluidColor(Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        return handler.getFluidColor(null, null, fluid.getDefaultState());
    }

    private void renderColorBar(MatrixStack matrices, VertexConsumer consumer, float xStart, float yStart, float width, float height,
                                float red, float green, float blue, float alpha, int light, int overlay,
                                float normalX, float normalY, float normalZ) {
        // Get the position matrix
        var positionMatrix = matrices.peek().getPositionMatrix();

        // Bottom-left corner (with texture coords)
        consumer.vertex(positionMatrix, xStart, yStart, 0.0f)
                .texture(u0, v0) // Texture coordinates using .texture()
                .color(red, green, blue, alpha)
                .light(light)
                .overlay(overlay)
                .normal(normalX, normalY, normalZ).next();

        // Bottom-right corner (with texture coords)
        consumer.vertex(positionMatrix, xStart + width, yStart, 0.0f)
                .texture(u1, v0) // Texture coordinates using .texture()
                .color(red, green, blue, alpha)
                .light(light)
                .overlay(overlay)
                .normal(normalX, normalY, normalZ).next();

        // Top-right corner (with texture coords)
        consumer.vertex(positionMatrix, xStart + width, yStart + height, 0.0f)
                .texture(u1, v1) // Texture coordinates using .texture()
                .color(red, green, blue, alpha)
                .light(light)
                .overlay(overlay)
                .normal(normalX, normalY, normalZ).next();

        // Top-left corner (with texture coords)
        consumer.vertex(positionMatrix, xStart, yStart + height, 0.0f)
                .texture(u0, v1) // Texture coordinates using .texture()
                .color(red, green, blue, alpha)
                .light(light)
                .overlay(overlay)
                .normal(normalX, normalY, normalZ).next();


        matrices.pop();
    }
}
