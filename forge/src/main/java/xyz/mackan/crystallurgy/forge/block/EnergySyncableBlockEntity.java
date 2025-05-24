package xyz.mackan.crystallurgy.forge.block;

import xyz.mackan.crystallurgy.api.IModEnergyStorage;

public interface EnergySyncableBlockEntity {
    public final IModEnergyStorage energyStorage = null;
    public void setEnergyLevel(long energyLevel);
}
