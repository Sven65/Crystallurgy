package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.forge.client.networking.ExtractFluidToCursorC2SPacket;
import xyz.mackan.crystallurgy.forge.networking.ForgeEnergySyncS2CPacket;
import xyz.mackan.crystallurgy.forge.networking.ForgeFluidSyncS2CPacket;
import xyz.mackan.crystallurgy.forge.networking.ForgeSpawnParticleS2CPacket;

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

        net.messageBuilder(ForgeFluidSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ForgeFluidSyncS2CPacket::new)
                .encoder(ForgeFluidSyncS2CPacket::toBytes)
                .consumerMainThread(ForgeFluidSyncS2CPacket::handle)
                .add();

        net.messageBuilder(ExtractFluidToCursorC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ExtractFluidToCursorC2SPacket::new)
                .encoder(ExtractFluidToCursorC2SPacket::toBytes)
                .consumerMainThread(ExtractFluidToCursorC2SPacket::handle)
                .add();

        net.messageBuilder(ForgeSpawnParticleS2CPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ForgeSpawnParticleS2CPacket::new)
                .encoder(ForgeSpawnParticleS2CPacket::toBytes)
                .consumerMainThread(ForgeSpawnParticleS2CPacket::handle)
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
