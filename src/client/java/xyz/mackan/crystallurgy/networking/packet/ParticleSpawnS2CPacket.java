package xyz.mackan.crystallurgy.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.CoolingFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.blocks.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.blocks.ResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.util.ImplementedInventory;
import xyz.mackan.crystallurgy.util.TextureUtil;

public class ParticleSpawnS2CPacket {
    public static void spawnParticles(World world, BlockPos pos, int chance, Vector3f color) {
        //int chance = 10;
        //Vector3f color = new Vector3f(0.8F, 0.3F, 1.0F);

        // TODO: Get Colors on client side
        if(world.random.nextInt(chance) == 0) {
            // ~10% chance per tick → average once every 0.5 seconds
            double x = pos.getX() + world.random.nextDouble();
            double y = pos.getY() + 1.0 + world.random.nextDouble() * 0.2;
            double z = pos.getZ() + world.random.nextDouble();

            world.addParticle(
                    new DustParticleEffect(color, 1.0F),
                    x, y, z,
                    0.0, 0.02, 0.0
            );
        }
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world == null) return;
        BlockPos position = buf.readBlockPos();
        ItemStack itemStack = buf.readItemStack();
        BlockEntity entity = client.world.getBlockEntity(position);

        if (entity instanceof CoolingFluidCauldronBlockEntity blockEntity) {
            Vector3f color = TextureUtil.getAverageItemColor(itemStack);
            boolean isCrafting = blockEntity.getIsCrafting();

            ParticleSpawnS2CPacket.spawnParticles(client.world, position, isCrafting ? 30 : 10, color);
        } else if (entity instanceof CrystalFluidCauldronBlockEntity blockEntity) {
            Vector3f color = TextureUtil.getAverageItemColor(itemStack);
            boolean isCrafting = blockEntity.getIsCrafting();

            ParticleSpawnS2CPacket.spawnParticles(client.world, position, isCrafting ? 30 : 10, color);
        }
    }
}
