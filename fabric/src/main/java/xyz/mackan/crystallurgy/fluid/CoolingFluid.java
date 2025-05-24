package xyz.mackan.crystallurgy.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import xyz.mackan.crystallurgy.registry.FabricModFluids;

public abstract class CoolingFluid extends AbstractCoolingFluid {
    @Override
    public Fluid getStill() {
        return FabricModFluids.STILL_COOLING_FLUID;
    }

    @Override
    public Fluid getFlowing() {
        return FabricModFluids.FLOWING_COOLING_FLUID;
    }

    @Override
    public Item getBucketItem() {
        return FabricModFluids.COOLING_FLUID_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return FabricModFluids.COOLING_FLUID_BLOCK.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    public static class Flowing extends CoolingFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Still extends CoolingFluid {
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }
}
