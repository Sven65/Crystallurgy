package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.forge.networking.ForgeEnergySyncS2CPacket;

public class ForgeModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(Constants.id("messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ForgeEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
            .decoder(ForgeEnergySyncS2CPacket::new)
            .encoder(ForgeEnergySyncS2CPacket::toBytes)
            .consumerMainThread(ForgeEnergySyncS2CPacket::handle)
            .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayerEntity player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
