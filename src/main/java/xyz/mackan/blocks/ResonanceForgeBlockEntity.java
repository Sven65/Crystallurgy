package xyz.mackan.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.gui.ResonanceForgeScreenHandler;
import xyz.mackan.registry.ModBlockEntities;
import xyz.mackan.registry.ModBlocks;

public class ResonanceForgeBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    private int energy;
    private int progress;
    private final Inventory inventory = new SimpleInventory(3);

    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_FORGE, pos, state);
    }

    public void tick() {
        // Add energy tick, check inputs, process synthesis
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(ModBlocks.RESONANCE_FORGE.getTranslationKey());
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ResonanceForgeScreenHandler(syncId, playerInventory, this.inventory);
    }
}
