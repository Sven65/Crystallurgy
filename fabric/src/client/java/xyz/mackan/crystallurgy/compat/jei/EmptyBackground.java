package xyz.mackan.crystallurgy.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.DrawContext;

// Taken from Create Mod, modified for Fabric
// https://github.com/Creators-of-Create/Create/blob/mc1.20.1/dev/src/main/java/com/simibubi/create/compat/jei/EmptyBackground.java
public class EmptyBackground implements IDrawable {

    private int width;
    private int height;

    public EmptyBackground(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void draw(DrawContext graphics, int xOffset, int yOffset) {}
}