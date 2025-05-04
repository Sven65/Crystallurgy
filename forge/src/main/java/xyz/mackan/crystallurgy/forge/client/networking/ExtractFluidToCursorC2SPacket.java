package xyz.mackan.crystallurgy.forge.client.networking;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.network.NetworkEvent;
import xyz.mackan.crystallurgy.forge.block.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.forge.gui.FluidSynthesizerScreenHandler;

import java.util.Objects;
import java.util.function.Supplier;

public class ExtractFluidToCursorC2SPacket {
    private final BlockPos pos;
    private final String slot;


    public ExtractFluidToCursorC2SPacket(BlockPos pos, String slot) {
        this.pos = pos;
        this.slot = slot;
    }

    public ExtractFluidToCursorC2SPacket(PacketByteBuf buf) {
        this.slot = buf.readString();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeString(slot);
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // On the server!!!!
            ServerPlayerEntity player = context.getSender();
            World world = player.getWorld();
            if (!(player.currentScreenHandler instanceof FluidSynthesizerScreenHandler screenHandler)) return;

            BlockEntity be = player.getWorld().getBlockEntity(pos);
            if (!(be instanceof FluidSynthesizerBlockEntity blockEntity)) return;

            FluidTank fluidTank = Objects.equals(slot, "input") ? blockEntity.inputFluidStorage : blockEntity.outputFluidStorage;
            FluidStack fluidStack = fluidTank.getFluid();

            if (fluidStack == null || fluidStack.isEmpty() || fluidTank.getFluidAmount() < 1000) return;

            ItemStack cursor = screenHandler.getCursorStack();
            if (!cursor.isOf(Items.BUCKET) || cursor.getCount() != 1) return;

            // Do the fluid -> bucket conversion
            ItemStack filledBucket = new ItemStack(fluidStack.getFluid().getBucketItem());// make sure this method exists
            if (filledBucket.isEmpty()) return;

            // Set the player's cursor stack
            screenHandler.setCursorStack(filledBucket);

            // Decrease fluid in block entity

            if (Objects.equals(this.slot, "input")) {
                blockEntity.extractInputFluid((FluidSynthesizerBlockEntity) be, 1000);
            } else {
                blockEntity.extractOutputFluid((FluidSynthesizerBlockEntity)be, 1000);
            }

            blockEntity.markDirty();

        });
        return true;
    }
}
