package xyz.mackan.crystallurgy.compat;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.registry.ModBlocks;

import java.util.LinkedList;
import java.util.List;


public class ResonanceForgeCategory implements DisplayCategory<BasicDisplay> {
    public static final Identifier TEXTURE = new Identifier(Crystallurgy.MOD_ID, "textures/gui/resonance_forge.png");
    public static final CategoryIdentifier<ResonanceForgeDisplay> RESONANCE_FORGE = CategoryIdentifier.of(Crystallurgy.MOD_ID, "resonance_forge");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return RESONANCE_FORGE;
    }

    @Override
    public Text getTitle() {
        // TODO: Make this translatable with .translatable
        return Text.literal("Resonance Forge");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.RESONANCE_FORGE.asItem().getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 87, bounds.getCenterY() - 35);
        List<Widget> widgets = new LinkedList<>();

        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint. y, 175, 82)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 7, startPoint.y + 35))
                .entries(display.getInputEntries().get(0)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 29, startPoint.y + 35))
                .entries(display.getInputEntries().get(1)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 51, startPoint.y + 35))
                .entries(display.getInputEntries().get(2)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 112, startPoint.y + 35))
                .markOutput().entries(display.getOutputEntries().get(0)));

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }
}
