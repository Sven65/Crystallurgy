package xyz.mackan.crystallurgy.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xyz.mackan.crystallurgy.networking.packet.EnergySyncS2CPacket;
import xyz.mackan.crystallurgy.registry.ModMessages;

public class ModNetworking {
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.ENERGY_SYNC, EnergySyncS2CPacket::receive);
    }
}
