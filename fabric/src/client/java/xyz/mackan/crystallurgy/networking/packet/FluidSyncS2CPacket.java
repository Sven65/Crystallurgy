package xyz.mackan.crystallurgy.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.block.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.gui.FluidSynthesizerScreenHandler;
import xyz.mackan.crystallurgy.util.FluidUtils;

import java.util.Objects;

public class FluidSyncS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String fluidSlot = buf.readString();

        FluidVariant variant = FluidVariant.fromPacket(buf);
        long amount = buf.readLong();

        BlockPos position = buf.readBlockPos();
        //BlockPos position = new BlockPos(0, 0, 0);

        if(client.world.getBlockEntity(position) instanceof FluidSynthesizerBlockEntity blockEntity) {
            if (Objects.equals(fluidSlot, "input")) {
                blockEntity.setInputFluidLevel(variant.getFluid(), amount);
            } else {
                blockEntity.setOutputFluidLevel(variant.getFluid(), amount);
            }

            if(client.player.currentScreenHandler instanceof FluidSynthesizerScreenHandler screenHandler &&
                    screenHandler.synthesizerBlockEntity.getPos().equals(position)) {
                if (Objects.equals(fluidSlot, "input")) {
                    blockEntity.setInputFluidLevel(variant.getFluid(), amount);
                    screenHandler.setInputFluid(variant.getFluid(), (int) amount);
                } else {
                    blockEntity.setOutputFluidLevel(variant.getFluid(), amount);
                    screenHandler.setOutputFluid(variant.getFluid(), (int) amount);
                }
            }
        }
    }
}