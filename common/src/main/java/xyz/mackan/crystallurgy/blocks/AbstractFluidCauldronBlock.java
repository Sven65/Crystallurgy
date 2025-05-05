package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import xyz.mackan.crystallurgy.registry.ModProperties;

import java.util.Map;

public class AbstractFluidCauldronBlock extends AbstractCauldronBlock {
    public AbstractFluidCauldronBlock(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
        this.setDefaultState(this.stateManager.getDefaultState().with(ModProperties.FLUID_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ModProperties.FLUID_LEVEL);
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        // Do nothing - don't let rain fill this cauldron
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        double fluidLevel = (double)state.get(ModProperties.FLUID_LEVEL);

        return fluidLevel == 0 ? 0 : (6.0 + fluidLevel * 3.0) / 16.0;
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
    public boolean isFull(BlockState state) {
        return state.get(ModProperties.FLUID_LEVEL) == 3;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractFluidCauldronBlockEntity cauldronEntity) {
                cauldronEntity.addItemEntityToCauldron(itemEntity);
            }
        }
    }
}
