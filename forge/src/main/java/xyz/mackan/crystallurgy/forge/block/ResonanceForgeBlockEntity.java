package xyz.mackan.crystallurgy.forge.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.blocks.AbstractResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.forge.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlockEntities;
import xyz.mackan.crystallurgy.forge.util.ModEnergyStorage;

public class ResonanceForgeBlockEntity extends AbstractResonanceForgeBlockEntity implements ScreenHandlerFactory {
    public final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(this.ENERGY_CAPACITY, MAX_ENERGY_INSERT, MAX_ENERGY_EXTRACT) {
        @Override
        public void onEnergyChanged() {
            markDirty();

            if (!world.isClient()) {
                // Sync with packet
            }
        }
    };

    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ForgeModBlockEntities.RESONANCE_FORGE.get(), pos, state, new ArrayPropertyDelegate(2));
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        sendEnergyPacket();

        return new ResonanceForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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

//    @Override
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
//        if (cap == ForgeCapabilities.ENERGY) {
//            // ?
//        }
//    }
}
