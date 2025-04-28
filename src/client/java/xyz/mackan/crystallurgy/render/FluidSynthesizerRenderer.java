package xyz.mackan.crystallurgy.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.FluidSynthesizerBlockEntity;

public class FluidSynthesizerRenderer implements BlockEntityRenderer<FluidSynthesizerBlockEntity>  {

    public FluidSynthesizerRenderer(BlockEntityRendererFactory.Context context) {}

    @Override
    public void render(FluidSynthesizerBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction facing = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);

        if (!blockEntity.inputFluidStorage.variant.isBlank()) {
            // TODO: Render fluid bars, with the texture and height of the actual fluids in the machine
            //renderFluidBar(matrices, vertexConsumers, 15, overlay, facing, 4, 4, blockEntity.inputFluidStorage.variant);
        }
    }

    private static void renderFluidBar(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, float x, float y, FluidVariant fluid) {
        // Push the current matrix stack
        matrices.push();

        // Move to the center of the block
        matrices.translate(0.5, 0.5, 0.5);

        double zTranslate = -0.501;

        switch (facing) {
            case NORTH:
                zTranslate = -0.501f;
                break;
            case EAST:
                zTranslate = -0.501f;
                break;
            case SOUTH:
                zTranslate = -0.501f;
                break;
            case WEST:
                zTranslate = -0.501f;
                break;
            default:
                break;
        }

        // Rotate to face the front side based on block's facing

        // Move to the desired position on the front face

        switch (facing) {
            case NORTH:
                break;
            case EAST:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facing.asRotation()));
                break;
            case SOUTH:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                break;
            case WEST:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facing.asRotation()));
                break;
            default:
                break;
        }


        float width = 3;
        float height = 8;

        matrices.translate(x / 16f - (width / 16f) / 2f, y / 16f - (height / 16f) / 2f, zTranslate); // Slightly in front of the front face to avoid z-fighting
        matrices.scale(width / 16f, height / 16f, 1.0f);


        // Scale if needed

        Sprite sprite = FluidVariantRendering.getSprite(fluid);
        int color = FluidVariantRendering.getColor(fluid);
        Identifier atlas = sprite.getAtlasId();




        // Use this safe layer — suitable for block-style textured rendering

        Identifier texture = new Identifier("modid", "textures/gui/overlay.png");
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(atlas));
        //VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getText(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
        //VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getTranslucent());
        // Set up the texture (fluid texture)
        //RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        // Extract RGBA components
        float r = (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        float a = 0f;


        // Prepare the texture and render layer

        // Render a flat quad (2D texture on face)
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        // Define a square (quad) on the face
        // Use Vec3i for normalDirection
        Vector3f normalDirection = new Vector3f(0, 0, -1); // Default normal

        // Adjust normal for the block's facing direction
        switch (facing) {
            case NORTH:
                normalDirection = new Vector3f(0, 0, 1);
                break;
            case EAST:
                normalDirection = new Vector3f(-1, 0, 0);
                break;
            case SOUTH:
                normalDirection = new Vector3f(0, 0, -1);
                break;
            case WEST:
                normalDirection = new Vector3f(1, 0, 0);
                break;
            default:
                break;
        }

        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        // Convert Vec3i to Vector3f for normal
        Vector3f normalVector = new Vector3f(normalDirection.x(), normalDirection.y(), normalDirection.z());

        vc.vertex(positionMatrix, -0.5f, 0.5f, 0)
                .color(r, g, b, a)
                //.texture(1f, 0f)
                .texture(sprite.getMinU(), sprite.getMinV())
                .overlay(OverlayTexture.DEFAULT_UV).light(light)
                .normal(normalMatrix, normalVector.x, normalVector.y, normalVector.z)
                .next();
        vc.vertex(positionMatrix, 0.5f, 0.5f, 0)
                .color(r, g, b, a)
                //.texture(0f, 0f)
                .texture(sprite.getMaxU(), sprite.getMinV())
                .overlay(OverlayTexture.DEFAULT_UV).light(light)
                .normal(normalMatrix, normalVector.x, normalVector.y, normalVector.z)
                .next();
        vc.vertex(positionMatrix, 0.5f, -0.5f, 0)
                .color(r, g, b, a)
                //.texture(0f, 1f)
                .texture(sprite.getMaxU(), sprite.getMaxV())
                .overlay(OverlayTexture.DEFAULT_UV).light(light)
                .normal(normalMatrix, normalVector.x, normalVector.y, normalVector.z)
                .next();
        vc.vertex(positionMatrix, -0.5f, -0.5f, 0)
                .color(r, g, b, a)
                //.texture(1f, 1f)
                .texture(sprite.getMinU(), sprite.getMaxV())
                .overlay(OverlayTexture.DEFAULT_UV).light(light)
                .normal(normalMatrix, normalVector.x, normalVector.y, normalVector.z)
                .next();

        matrices.pop();
    }

    public int getFluidColor(Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        return handler.getFluidColor(null, null, fluid.getDefaultState());
    }


}
