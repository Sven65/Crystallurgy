package xyz.mackan.crystallurgy.forge.networking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;
import xyz.mackan.crystallurgy.forge.block.FluidSynthesizerBlockEntity;

import java.util.function.Supplier;

public class ForgeFluidSyncS2CPacket {
    private final FluidStack fluidStack;
    private final BlockPos pos;
    private String slot;

    public ForgeFluidSyncS2CPacket(FluidStack fluidStack, BlockPos pos, String slot) {
        this.slot = slot;
        this.fluidStack = fluidStack;
        this.pos = pos;
    }

    public ForgeFluidSyncS2CPacket(PacketByteBuf buf) {
        this.slot = buf.readString();
        this.fluidStack = buf.readFluidStack();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeString(slot);
        buf.writeFluidStack(fluidStack);
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if(MinecraftClient.getInstance().world.getBlockEntity(pos) instanceof FluidSynthesizerBlockEntity blockEntity) {
                //blockEntity.setFluid(this.fluidStack);

//                if(MinecraftClient.getInstance().player.currentScreenHandler instanceof GemInfusingStationMenu menu &&
//                        menu.getBlockEntity().getBlockPos().equals(pos)) {
//                    menu.setFluid(this.fluidStack);
//                }
            }
        });
        return true;
    }
}