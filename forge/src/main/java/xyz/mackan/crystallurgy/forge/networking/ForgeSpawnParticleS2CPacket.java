package xyz.mackan.crystallurgy.forge.networking;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ForgeSpawnParticleS2CPacket {
    public final BlockPos pos;
    public final ItemStack itemStack;

    public ForgeSpawnParticleS2CPacket(BlockPos pos, ItemStack itemStack) {
        this.pos = pos;
        this.itemStack = itemStack;
    }

    public ForgeSpawnParticleS2CPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.itemStack = buf.readItemStack();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeItemStack(itemStack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> ClientSpawnParticleHandler.handle(pos, itemStack))
        );
        context.setPacketHandled(true);
        return true;
    }
}