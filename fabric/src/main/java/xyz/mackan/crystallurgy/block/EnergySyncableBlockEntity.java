package xyz.mackan.crystallurgy.block;

import team.reborn.energy.api.base.SimpleEnergyStorage;

public interface EnergySyncableBlockEntity {
    public final SimpleEnergyStorage energyStorage = null;
    public void setEnergyLevel(long energyLevel);
}