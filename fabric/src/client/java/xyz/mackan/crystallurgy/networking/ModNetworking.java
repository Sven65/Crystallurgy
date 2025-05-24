package xyz.mackan.crystallurgy.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xyz.mackan.crystallurgy.networking.packet.EnergySyncS2CPacket;
import xyz.mackan.crystallurgy.networking.packet.FluidSyncS2CPacket;
import xyz.mackan.crystallurgy.networking.packet.ParticleSpawnS2CPacket;
import xyz.mackan.crystallurgy.registry.ModMessages;

public class ModNetworking {
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.ENERGY_SYNC, EnergySyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.SPAWN_PARTICLES, ParticleSpawnS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.FLUID_SYNC, FluidSyncS2CPacket::receive);
    }
}