package xyz.mackan.crystallurgy.forge.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.blocks.AbstractResonanceForge;

public class ResonanceForgeBlock extends AbstractResonanceForge {
    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ResonanceForgeBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity entity = world.getBlockEntity(pos);

            if (entity instanceof ResonanceForgeBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayerEntity) player), (ResonanceForgeBlockEntity)entity, pos);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return super.getTicker(world, state, type);
    }
}