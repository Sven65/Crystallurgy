package xyz.mackan.crystallurgy.forge.networking;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;
import xyz.mackan.crystallurgy.forge.block.CoolingFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.forge.block.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.gui.TextureUtil;

import java.util.concurrent.ThreadLocalRandom;

public class ClientSpawnParticleHandler {
    public static void handle(BlockPos pos, ItemStack itemStack) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        BlockEntity entity = client.world.getBlockEntity(pos);
        if (entity instanceof CoolingFluidCauldronBlockEntity blockEntity) {
            Vector3f color = TextureUtil.getAverageItemColor(itemStack);
            spawnParticles(client.world, pos, blockEntity.getIsCrafting() ? 30 : 10, color);
        } else if (entity instanceof CrystalFluidCauldronBlockEntity blockEntity) {
            Vector3f color = TextureUtil.getAverageItemColor(itemStack);
            spawnParticles(client.world, pos, blockEntity.getIsCrafting() ? 30 : 10, color);
        }
    }

    private static void spawnParticles(World world, BlockPos pos, int chance, Vector3f color) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        if (rand.nextInt(chance) == 0) {
            double x = pos.getX() + rand.nextDouble();
            double y = pos.getY() + 1.0 + rand.nextDouble() * 0.2;
            double z = pos.getZ() + rand.nextDouble();
            world.addParticle(new DustParticleEffect(color, 1.0F), x, y, z, 0.0, 0.02, 0.0);
        }
    }
}
