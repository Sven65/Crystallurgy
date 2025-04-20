package xyz.mackan.crystallurgy.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.CrystallurgyClient;
import xyz.mackan.crystallurgy.blocks.CrystalFluidCauldronBlockEntity;

// TODO: Make this get the right items from the cauldron entity.
public class CrystalFluidCauldronRenderer implements BlockEntityRenderer<CrystalFluidCauldronBlockEntity> {
    private final ItemRenderer itemRenderer;

    public CrystalFluidCauldronRenderer() {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }



    @Override
    public void render(CrystalFluidCauldronBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        DefaultedList<ItemStack> items = blockEntity.getItems();

        //Crystallurgy.LOGGER.info("Render items {}", items);


        // Loop through both items in the cauldron's inventory
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty()) {
                matrices.push(); // Corrected push() for matrix transformations

                // Adjust the position for each item
                matrices.translate(0.5F, 0.5F, 0.5F); // Center the items
                matrices.translate(0.0F, 0.0F, (i - 0.5F) * 0.25F); // Offset each item vertically
                matrices.scale(0.75F, 0.75F, 0.75F);  // Scale the items to fit inside the cauldron

                // Render the item inside the cauldron
                // Render the item
                itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, MinecraftClient.getInstance().world, 0);

                matrices.pop(); // Corrected pop() to revert the transformation
            }
        }
    }
}
