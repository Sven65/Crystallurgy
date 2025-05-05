package xyz.mackan.crystallurgy.forge.networking;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.forge.CrystallurgyForge;
import xyz.mackan.crystallurgy.forge.block.CoolingFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.forge.block.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.forge.block.EnergySyncableBlockEntity;
import xyz.mackan.crystallurgy.forge.gui.FluidSynthesizerScreenHandler;
import xyz.mackan.crystallurgy.forge.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.gui.TextureUtil;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class ForgeSpawnParticleS2CPacket {
    private final BlockPos pos;
    private final ItemStack itemStack;

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

    public static void spawnParticles(World world, BlockPos pos, int chance, Vector3f color) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        if(rand.nextInt(chance) == 0) {
            // ~10% chance per tick → average once every 0.5 seconds
            double x = pos.getX() + rand.nextDouble();
            double y = pos.getY() + 1.0 + rand.nextDouble() * 0.2;
            double z = pos.getZ() + rand.nextDouble();

            world.addParticle(
                    new DustParticleEffect(color, 1.0F),
                    x, y, z,
                    0.0, 0.02, 0.0
            );
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        if (!context.getDirection().getReceptionSide().isClient()) {
            CrystallurgyCommon.LOGGER.info("World is NOT client.");
            return true;
        }

        context.enqueueWork(() -> {
            assert MinecraftClient.getInstance().world != null;
            BlockEntity entity = MinecraftClient.getInstance().world.getBlockEntity(pos);
            if (entity instanceof CoolingFluidCauldronBlockEntity blockEntity) {
                Vector3f color = TextureUtil.getAverageItemColor(itemStack);
                boolean isCrafting = blockEntity.getIsCrafting();

                ForgeSpawnParticleS2CPacket.spawnParticles(MinecraftClient.getInstance().world, pos, isCrafting ? 30 : 10, color);
            } else if (entity instanceof CrystalFluidCauldronBlockEntity blockEntity) {
                Vector3f color = TextureUtil.getAverageItemColor(itemStack);
                boolean isCrafting = blockEntity.getIsCrafting();

                ForgeSpawnParticleS2CPacket.spawnParticles(MinecraftClient.getInstance().world, pos, isCrafting ? 30 : 10, color);
            }
        });
        return true;
    }
}
