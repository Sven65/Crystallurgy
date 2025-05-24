package xyz.mackan.crystallurgy.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import xyz.mackan.crystallurgy.block.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.gui.FluidSynthesizerScreenHandler;
import xyz.mackan.crystallurgy.util.FluidStack;

import java.util.Objects;

public class ExtractFluidToCursorC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        BlockPos pos = buf.readBlockPos();
        String tankName = buf.readString();

        server.execute(() -> {
            if (!(player.currentScreenHandler instanceof FluidSynthesizerScreenHandler screenHandler)) return;

            BlockEntity be = player.getWorld().getBlockEntity(pos);
            if (!(be instanceof FluidSynthesizerBlockEntity blockEntity)) return;

            SingleVariantStorage<FluidVariant> fluidTank = Objects.equals(tankName, "input") ? blockEntity.inputFluidStorage : blockEntity.outputFluidStorage;
            FluidVariant fluid = fluidTank.variant;

            if (fluid == null || fluid.isBlank() || fluidTank.amount < FluidConstants.BUCKET) return;

            ItemStack cursor = screenHandler.getCursorStack();
            if (!cursor.isOf(Items.BUCKET) || cursor.getCount() != 1) return;

            // Do the fluid -> bucket conversion
            ItemStack filledBucket = new ItemStack(fluid.getFluid().getBucketItem());// make sure this method exists
            if (filledBucket.isEmpty()) return;

            // Set the player's cursor stack
            screenHandler.setCursorStack(filledBucket);

            // Decrease fluid in block entity

            if (Objects.equals(tankName, "input")) {
                blockEntity.extractInputFluid((FluidSynthesizerBlockEntity) be, FluidConstants.BUCKET);
            } else {
                blockEntity.extractOutputFluid((FluidSynthesizerBlockEntity)be, FluidConstants.BUCKET);
            }

            blockEntity.markDirty();
        });
    }
}