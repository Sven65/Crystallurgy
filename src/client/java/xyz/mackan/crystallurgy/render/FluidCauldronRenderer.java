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
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.CrystalFluidCauldron;
import xyz.mackan.crystallurgy.blocks.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.registry.ModFluids;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.List;

public class FluidCauldronRenderer<T extends BlockEntity & ImplementedInventory> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;

    public FluidCauldronRenderer() {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    public int getFluidColor(Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        Sprite sprite = handler.getFluidSprites(null, null, fluid.getDefaultState())[0];

        return handler.getFluidColor(null, null, fluid.getDefaultState());
    }


    public void renderLiquid(CrystalFluidCauldronBlockEntity entity, float tickDelta, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vertexConsumers, int light, int overlay, Fluid fluid) {
        int liquidLevel = entity.getFluidProgress();
        if (liquidLevel == 0)
            return;

        BlockState state = entity.getWorld().getBlockState(entity.getPos());
        CrystalFluidCauldron block = (CrystalFluidCauldron) state.getBlock();

        int color = getFluidColor(fluid);
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int alpha = 190;

        // Assuming WATER_MATERIAL.sprite() is unavailable, use a custom texture
        // If you need to get a custom texture, replace this line with your texture fetching method
       //RenderMaterial waterMaterial = getWaterMaterial();  // You need to implement this method or use your own way to get the material

        Identifier waterTextureId = new Identifier("minecraft", "block/water_still");

//        Sprite waterSprite = MinecraftClient.getInstance()
//                .getBakedModelManager()
//                .getAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
//                .getSprite(FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidSprites(null, null, ModFluids.FLOWING_CRYSTAL_FLUID.getDefaultState())[0].getId());

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
        matrices.translate(0, block.getFluidHeight2(state) + 0.001, 0);

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
        if (!(blockEntity instanceof CrystalFluidCauldronBlockEntity)) return;
        CrystalFluidCauldron cauldron = (CrystalFluidCauldron)blockEntity.getCachedState().getBlock();

        // === FLUID RENDERING START ===
        //renderLiquid((CrystalFluidCauldronBlockEntity) blockEntity, ModFluids.STILL_CRYSTAL_FLUID);
        renderLiquid((CrystalFluidCauldronBlockEntity) blockEntity,tickDelta, matrices, vertexConsumers, light, overlay, ModFluids.STILL_CRYSTAL_FLUID);
        // === FLUID RENDERING END ===


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
