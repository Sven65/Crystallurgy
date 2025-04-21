package xyz.mackan.crystallurgy.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import xyz.mackan.crystallurgy.Crystallurgy;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TextureUtil {
    public static Vector3f getAverageItemColor(ItemStack stack) {
        // Fallback if the stack is empty
        if (stack.isEmpty()) {
            return new Vector3f(1.0F, 1.0F, 1.0F);  // Default to white color
        }

        // Accessing the Minecraft client instance
        MinecraftClient client = MinecraftClient.getInstance();

        // Get the model for the item (this is client-side)
        BakedModel model = client.getItemRenderer().getModel(stack, null, null, 0);

        // Get the particle sprite (this is often the texture used in particle effects)
        Identifier fullTextureId = getIdentifier(model);

        // Fetch texture data (this will be part of the client-side code)
        NativeImage image = null;

        try {
            // Get the resource manager
            var resourceManager = client.getResourceManager();

            // Get the resource associated with the sprite texture
            Optional<Resource> resourceOptional = resourceManager.getResource(fullTextureId);

            if (resourceOptional.isEmpty()) {
                return new Vector3f(1.0F, 1.0F, 1.0F);
            }

            Resource resource = resourceOptional.get();

            // Read the image data from the resource's input stream
            image = NativeImage.read(resource.getInputStream());

        } catch (IOException e) {
            // Print the stack trace for debugging and return white on failure
            e.printStackTrace();
            return new Vector3f(1.0F, 1.0F, 1.0F);
        }

        // Calculate the average color of the image (taking RGB values)
        long r = 0, g = 0, b = 0;
        int pixelCount = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getColor(x, y);

                int alpha = (color >> 24) & 0xFF;
                int red = color & 0xFF;
                int blue = (color >> 16) & 0xFF;
                int green = (color >> 8) & 0xFF;  // Shift green bits and mask

                if (alpha < 32) {
                    continue;
                }

                // Add the color components to the running total
                r += red;
                g += green;
                b += blue;
                // Increment the count of valid pixels
                pixelCount++;
            }
        }

        // Close the NativeImage to free up native memory resources
        image.close();

        // Prevent division by zero if no valid pixels were found
        if (pixelCount == 0) {
            return new Vector3f(1.0F, 1.0F, 1.0F);  // Default white if no valid pixels
        }

        // Calculate the average color components
        // Divide the total color values by the number of valid pixels,
        // then normalize to a 0.0 to 1.0 range by dividing by 255.0F

        float rf = (r / (float) pixelCount) / 255.0F;
        float gf = (g / (float) pixelCount) / 255.0F;
        float bf = (b / (float) pixelCount) / 255.0F;

        // Return the average color as a Vector3f


        return new Vector3f(rf, gf, bf);
    }

    private static @NotNull Identifier getIdentifier(BakedModel model) {
        Sprite sprite = model.getParticleSprite();

        // Get the texture identifier for the sprite's location within the atlas
        Identifier spriteAtlasId = sprite.getAtlasId();

        String textureName = sprite.getContents().getId().getPath();

        // Construct the full texture path for the sprite within the resource pack
        // This assumes the sprite is located in the standard textures/ folder
        return new Identifier(
                spriteAtlasId.getNamespace(),
                "textures/" + textureName + ".png"
        );
    }
}
