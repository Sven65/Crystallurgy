package xyz.mackan.crystallurgy.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import xyz.mackan.crystallurgy.registry.ModMessages;

public class ModServerNetworking {
    public static void registerC2SPackets() {
       // ServerPlayNetworking.registerGlobalReceiver(ModMessages.EXTRACT_FLUID_TO_CURSOR, ExtractFluidToCursorC2SPacket::receive);
    }
}