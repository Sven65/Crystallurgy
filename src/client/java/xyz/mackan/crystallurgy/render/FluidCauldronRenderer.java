package xyz.mackan.crystallurgy.render;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import xyz.mackan.crystallurgy.blocks.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.List;

public class FluidCauldronRenderer<T extends BlockEntity & ImplementedInventory> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;

    public FluidCauldronRenderer() {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
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
