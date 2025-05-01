package xyz.mackan.crystallurgy.forge.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.blocks.AbstractResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlockEntities;

public class ResonanceForgeBlockEntity extends AbstractResonanceForgeBlockEntity implements ScreenHandlerFactory {
    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ForgeModBlockEntities.RESONANCE_FORGE.get(), pos, state, new ArrayPropertyDelegate(2));
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    @Override
    protected void sendEnergyPacket() {

    }

    @Override
    protected void setEnergyLevel(long energyLevel) {

    }

    @Override
    protected void extractEnergy(AbstractResonanceForgeBlockEntity entity, long amount) {

    }

    @Override
    protected boolean hasEnoughEnergy(AbstractResonanceForgeBlockEntity entity) {
        return false;
    }
}
