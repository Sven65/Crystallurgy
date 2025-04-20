package xyz.mackan.crystallurgy.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;


import java.io.IOException;
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
        Sprite sprite = model.getParticleSprite();

        // Get the texture identifier for the sprite's location within the atlas
        Identifier spriteAtlasId = sprite.getAtlasId();
        // Get the identifier for the specific sprite
        Identifier spriteId = sprite.getAtlasId();

        // Construct the full texture path for the sprite within the resource pack
        // This assumes the sprite is located in the standard textures/ folder
        Identifier fullTextureId = new Identifier(spriteAtlasId.getNamespace(), "textures/" + spriteId.getPath() + ".png");

        // Fetch texture data (this will be part of the client-side code)
        NativeImage image = null;

        try {
            // Get the resource manager
            var resourceManager = client.getResourceManager();

            // Get the resource associated with the sprite texture
            Optional<Resource> resourceOptional = resourceManager.getResource(fullTextureId);

            if (resourceOptional.isEmpty()) {
                // If the resource is not found, return white
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

        // Iterate through all pixels of the image
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // Get the color of the pixel as an integer (typically ARGB format)
                int color = image.getColor(x, y);

                // Extract color components using bitwise operations
                // The format is typically ARGB (Alpha, Red, Green, Blue)
                int alpha = (color >> 24) & 0xFF; // Shift alpha bits to the right and mask
                int red = (color >> 16) & 0xFF;   // Shift red bits and mask
                int green = (color >> 8) & 0xFF;  // Shift green bits and mask
                int blue = color & 0xFF;          // Mask blue bits

                // Skip pixels that are mostly transparent (alpha less than 32 out of 255)
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
}
