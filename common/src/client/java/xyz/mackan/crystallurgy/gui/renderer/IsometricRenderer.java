package xyz.mackan.crystallurgy.gui.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.util.Collection;
import java.util.function.Function;

public class IsometricRenderer {
    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final BlockRenderManager BLOCK_RENDERER = MC.getBlockRenderManager();

    /**
     * Render an isometric scene from a set of block positions.
     *
     * @param matrices    The PoseStack
     * @param originX     GUI X offset
     * @param originY     GUI Y offset
     * @param scale       Scale factor (e.g., 10–20)
     * @param positions   Collection of BlockPos to render (relative)
     * @param blockGetter Function that returns the BlockState at a given BlockPos
     */
    public static void renderBlocks(
            MatrixStack matrices,
            int originX,
            int originY,
            float scale,
            Collection<BlockPos> positions,
            Function<BlockPos, BlockState> blockGetter
    ) {
        RenderSystem.enableDepthTest();
        BlockRenderManager brm = MC.getBlockRenderManager();
        var buffers = MC.getBufferBuilders().getEntityVertexConsumers();

        // Pre‑compute the X‐tilt for a true isometric (arctan(1/√2) ≈ 35.264°)
        float isoX = (float) Math.toDegrees(Math.atan(1.0 / Math.sqrt(2)));

        for (BlockPos pos : positions) {
            BlockState state = blockGetter.apply(pos);
            if (state == null) continue;
            double BASE_Z = 100.0;
            boolean isCentre = pos.equals(BlockPos.ORIGIN);
            double z = BASE_Z + (isCentre ? 5.0 : 0.0);

            // 1) Project your block‑space (x,y,z) → screen pixels
            double sx = (pos.getX() - pos.getZ()) * scale;
            double sy = (pos.getX() + pos.getZ()) * (scale / 2.0) - pos.getY() * scale;

            matrices.push();
            // 2) Move to exactly that pixel + your GUI origin
            matrices.translate(originX + sx, originY + sy, z);

            // 3) Scale model‐units → pixels, then tilt into isometric
            //    (now one model unit = scale/16 pixels, so 16 units = `scale` px)
            float blockScale = scale;
            matrices.scale(blockScale, blockScale, blockScale);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(isoX));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45f));

            // 4) Render the block at (0,0,0) of this mini‐scene
            var model = brm.getModel(state);
            brm.getModelRenderer().render(
                    matrices.peek(),
                    buffers.getBuffer(RenderLayers.getBlockLayer(state)),
                    state,
                    model,
                    1f, 1f, 1f,
                    0xF000F0,
                    OverlayTexture.DEFAULT_UV
            );

            matrices.pop();
        }

        buffers.draw();
        RenderSystem.disableDepthTest();
    }
}
