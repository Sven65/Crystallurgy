package xyz.mackan.crystallurgy.util;

import org.joml.Vector3f;

public class ColorUtil {
    public static Vector3f argbToVector3f(int argb) {
        // Extract the red, green, and blue channels (shifting and masking)
        float red = ((argb >> 16) & 0xFF) / 255.0f;
        float green = ((argb >> 8) & 0xFF) / 255.0f;
        float blue = (argb & 0xFF) / 255.0f;

        // Return the Vector3f with normalized RGB values
        return new Vector3f(red, green, blue);
    }
}