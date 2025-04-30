package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.CrystallurgyCommon;

public class FluidSynthesizer extends BlockWithEntity implements BlockEntityProvider {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public FluidSynthesizer() {
        super(AbstractBlock.Settings.create().strength(4.0f).requiresTool());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FluidSynthesizerBlockEntity(pos, state);
    }

//    @Override
//    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        return null;
//    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof FluidSynthesizerBlockEntity) {
                ItemScatterer.spawn(world, pos, (FluidSynthesizerBlockEntity)blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((FluidSynthesizerBlockEntity) world.getBlockEntity(pos));

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        Direction face = hit.getSide();
        Vec3d hitPos = hit.getPos();
        BlockPos blockPos = hit.getBlockPos();

        double dx = hitPos.x - blockPos.getX();
        double dy = hitPos.y - blockPos.getY();
        double dz = hitPos.z - blockPos.getZ();

        double u = 0, v = 0;

        switch (face) {
            case UP:
                u = dx; v = dz;
                break;
            case DOWN:
                u = dx; v = dz;
                break;
            case NORTH:
                u = 1 - dx; v = 1 - dy;
                break;
            case SOUTH:
                u = dx; v = 1 - dy;
                break;
            case WEST:
                u = dz; v = 1 - dy;
                break;
            case EAST:
                u = 1 - dz; v = 1 - dy;
                break;
        }

        CrystallurgyCommon.LOGGER.info("UV: {}, {}", u, v);

        return ActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.FLUID_SYNTHESIZER,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity));
    }
}
