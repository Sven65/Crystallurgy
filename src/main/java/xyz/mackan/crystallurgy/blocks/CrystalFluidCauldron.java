package xyz.mackan.crystallurgy.blocks;


import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModCauldron;
import xyz.mackan.crystallurgy.registry.ModItems;

import java.util.Map;

public class CrystalFluidCauldron extends AbstractCauldronBlock implements BlockEntityProvider {
    public CrystalFluidCauldron(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
        this.setDefaultState(this.stateManager.getDefaultState().with(ModCauldron.FLUID_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ModCauldron.FLUID_LEVEL);
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        return (6.0 + (double)state.get(ModCauldron.FLUID_LEVEL) * 3.0) / 16.0;
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.get(ModCauldron.FLUID_LEVEL) == 3;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {

        if (entity instanceof ItemEntity itemEntity) {
            Crystallurgy.LOGGER.info("ent collision");

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CrystalFluidCauldronBlockEntity cauldronEntity) {
                Crystallurgy.LOGGER.info("Adding item");
                cauldronEntity.addItemEntityToCauldron(itemEntity);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;

        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            if (!world.isClient()) {
                // Handle your empty-hand logic here!
                //player.sendMessage(Text.literal("Touched the goo with your bare hands 💀"), false);
                // maybe trigger a recipe or mutate state

                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof CrystalFluidCauldronBlockEntity cauldronEntity) {
                    // Call your logic here!
                    cauldronEntity.handleEmptyHandInteraction(hand, player);
                }

            }
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
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
        return checkType(type, ModBlockEntities.CRYSTAL_FLUID_CAULDRON,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity));
    }
}
