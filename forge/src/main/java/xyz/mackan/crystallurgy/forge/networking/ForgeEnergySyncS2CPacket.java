package xyz.mackan.crystallurgy.forge.networking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.network.NetworkEvent;
import xyz.mackan.crystallurgy.forge.block.ResonanceForgeBlockEntity;
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
            if(MinecraftClient.getInstance().world.getBlockEntity(pos) instanceof ResonanceForgeBlockEntity blockEntity) {
                blockEntity.setEnergyLevel(energy);

                if(MinecraftClient.getInstance().player.currentScreenHandler instanceof ResonanceForgeScreenHandler menu &&
                        menu.forgeBlockEntity.getPos().equals(pos)) {
                    blockEntity.setEnergyLevel(energy);
                }
            }
        });
        return true;
    }
}