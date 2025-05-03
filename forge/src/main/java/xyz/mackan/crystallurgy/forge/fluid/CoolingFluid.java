package xyz.mackan.crystallurgy.forge.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import xyz.mackan.crystallurgy.fluid.AbstractCoolingFluid;
import xyz.mackan.crystallurgy.forge.registry.ForgeModFluids;

public abstract class CoolingFluid extends AbstractCoolingFluid {
    @Override
    public Fluid getStill() {
        return ForgeModFluids.STILL_COOLING_FLUID.get();
    }

    @Override
    public Fluid getFlowing() {
        return ForgeModFluids.FLOWING_COOLING_FLUID.get();
    }

    @Override
    public Item getBucketItem() {
        return ForgeModFluids.COOLING_FLUID_BUCKET.get();
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return ForgeModFluids.COOLING_FLUID_BLOCK.get().getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
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