package xyz.mackan.crystallurgy.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModCauldron;
import xyz.mackan.crystallurgy.registry.ModItems;

import java.util.Map;

public class CoolingFluidCauldron extends LeveledCauldronBlock implements BlockEntityProvider {
    public CoolingFluidCauldron(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, precipitation -> false, behaviorMap);
        this.setDefaultState(this.stateManager.getDefaultState().with(ModCauldron.FLUID_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ModCauldron.FLUID_LEVEL);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        // TODO: Make this check for recipes to make sure we only get desireable items in the processing list
        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity) entity;
            ItemStack itemStack = itemEntity.getStack();

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CrystalFluidCauldronBlockEntity) {
                CrystalFluidCauldronBlockEntity cauldronEntity = (CrystalFluidCauldronBlockEntity) blockEntity;

                if (itemStack.getItem() == ModItems.CRYSTAL_SEED) {
                    Crystallurgy.LOGGER.info("Crystal seed was thrown in");

                    cauldronEntity.addItemEntityToCauldron(itemEntity);
                }
            }
        }
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        // Do nothing - don't let rain fill this cauldron
    }

    @Override
    protected boolean canBeFilledByDripstone(Fluid fluid) {
        return false;
    }

    @Override
    public Item asItem() {
        return Items.CAULDRON;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalFluidCauldronBlockEntity(pos, state);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.COOLING_FLUID_CAULDRON,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity));
    }
}
