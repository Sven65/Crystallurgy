package xyz.mackan.crystallurgy.forge.networking;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.network.NetworkEvent;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.forge.block.EnergySyncableBlockEntity;
import xyz.mackan.crystallurgy.forge.block.ResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.forge.gui.FluidSynthesizerScreenHandler;
import xyz.mackan.crystallurgy.forge.gui.ResonanceForgeScreenHandler;

import java.util.function.Supplier;

public class ForgeEnergySyncS2CPacket {
    private final int energy;
    private final BlockPos pos;

    public ForgeEnergySyncS2CPacket(int energy, BlockPos pos) {
        this.energy = energy;
        this.pos = pos;
    }

    public ForgeEnergySyncS2CPacket(PacketByteBuf buf) {
        this.energy = buf.readInt();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(energy);
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            BlockEntity entity = MinecraftClient.getInstance().world.getBlockEntity(pos);
            if(entity instanceof EnergySyncableBlockEntity blockEntity) {
                blockEntity.setEnergyLevel(energy);

                if(MinecraftClient.getInstance().player.currentScreenHandler instanceof ResonanceForgeScreenHandler screenHandler &&
                        screenHandler.forgeBlockEntity.getPos().equals(pos)) {
                    blockEntity.setEnergyLevel(energy);
                } else if(MinecraftClient.getInstance().player.currentScreenHandler instanceof FluidSynthesizerScreenHandler screenHandler &&
                        screenHandler.synthesizerBlockEntity.getPos().equals(pos)) {
                    blockEntity.setEnergyLevel(energy);
                }
            }
        });
        return true;
    }
}