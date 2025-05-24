package xyz.mackan.crystallurgy.forge.networking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.forge.block.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.forge.gui.FluidSynthesizerScreenHandler;

import java.util.Objects;
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
                if (Objects.equals(this.slot, "input")) {
                    blockEntity.setInputFluidLevel(this.fluidStack.getFluid(), this.fluidStack.getAmount());
                } else if (Objects.equals(this.slot, "output")) {
                    blockEntity.setOutputFluidLevel(this.fluidStack.getFluid(), this.fluidStack.getAmount());
                }

                if(MinecraftClient.getInstance().player.currentScreenHandler instanceof FluidSynthesizerScreenHandler menu && menu.synthesizerBlockEntity.getPos().equals(pos)) {
                    if (Objects.equals(this.slot, "input")) {
                        menu.setInputFluid(this.fluidStack.getFluid(), this.fluidStack.getAmount());
                    } else if (Objects.equals(this.slot, "output")) {
                        menu.setOutputFluid(this.fluidStack.getFluid(), this.fluidStack.getAmount());
                    }
                }
            }
        });
        return true;
    }
}