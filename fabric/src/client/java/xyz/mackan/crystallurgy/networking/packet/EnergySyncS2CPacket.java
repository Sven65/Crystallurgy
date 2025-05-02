package xyz.mackan.crystallurgy.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import xyz.mackan.crystallurgy.block.EnergySyncableBlockEntity;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreenHandler;

public class EnergySyncS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        long energy = buf.readLong();
        BlockPos position = buf.readBlockPos();

        if(client.world.getBlockEntity(position) instanceof EnergySyncableBlockEntity blockEntity) {
            blockEntity.setEnergyLevel(energy);

            if(client.player.currentScreenHandler instanceof ResonanceForgeScreenHandler screenHandler &&
                    screenHandler.forgeBlockEntity.getPos().equals(position)) {
                blockEntity.setEnergyLevel(energy);
            }
//            else if(client.player.currentScreenHandler instanceof FluidSynthesizerScreenHandler screenHandler &&
//                    screenHandler.synthesizerBlockEntity.getPos().equals(position)) {
//                blockEntity.setEnergyLevel(energy);
//            }
        }
    }
}