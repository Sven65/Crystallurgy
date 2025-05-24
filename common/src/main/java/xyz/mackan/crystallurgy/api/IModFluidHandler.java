package xyz.mackan.crystallurgy.api;

public interface IModFluidHandler {
    Object getFluid();
    int getFluidAmount();
    int getMaxFluidAmount();
    boolean canFill();
    boolean canDrain();
    Object fill(Object fluid, boolean simulate);
    Object drain(int maxDrain, boolean simulate);
}