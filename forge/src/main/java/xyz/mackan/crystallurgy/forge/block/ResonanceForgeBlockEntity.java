package xyz.mackan.crystallurgy.forge.block;

import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.blocks.AbstractResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.forge.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlockEntities;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlocks;
import xyz.mackan.crystallurgy.forge.util.ModEnergyStorage;
import xyz.mackan.crystallurgy.registry.ModMessages;

public class ResonanceForgeBlockEntity extends AbstractResonanceForgeBlockEntity implements NamedScreenHandlerFactory {
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
        super(ForgeModBlockEntities.RESONANCE_FORGE.get(), pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ResonanceForgeBlockEntity.this.progress;
                    case 1 -> ResonanceForgeBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> ResonanceForgeBlockEntity.this.progress = value;
                    case 1 -> ResonanceForgeBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        sendEnergyPacket();

        return new ResonanceForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected void sendEnergyPacket() {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeLong(ENERGY_STORAGE.getEnergyStored());
        data.writeBlockPos(getPos());

//        ((ServerWorld) world).getEntityLookup().
//
//        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
//            ServerPlayNetworking.send(player, ModMessages.ENERGY_SYNC, data);
//        }
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
    public Text getDisplayName() {
        return Text.translatable(ForgeModBlocks.RESONANCE_FORGE.get().getTranslationKey());
    }




//    @Override
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
//        if (cap == ForgeCapabilities.ENERGY) {
//            // ?
//        }
//    }
}
