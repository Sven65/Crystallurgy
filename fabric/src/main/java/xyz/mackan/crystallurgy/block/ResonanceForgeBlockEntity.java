package xyz.mackan.crystallurgy.block;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import xyz.mackan.crystallurgy.blocks.AbstractResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.registry.FabricModBlockEntities;
import xyz.mackan.crystallurgy.registry.FabricModBlocks;

public class ResonanceForgeBlockEntity extends AbstractResonanceForgeBlockEntity implements ExtendedScreenHandlerFactory {
    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(ENERGY_CAPACITY, 10000, 20000) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            if (!world.isClient()) {
                sendEnergyPacket();
            }
        }
    };

    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(FabricModBlockEntities.RESONANCE_FORGE, pos, state, new ArrayPropertyDelegate(2));
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

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        sendEnergyPacket();
        return new ResonanceForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(FabricModBlocks.RESONANCE_FORGE.getTranslationKey());
    }
}
