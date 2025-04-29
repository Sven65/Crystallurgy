package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.entity.BlockEntity;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public interface EnergySyncableBlockEntity {
    public final SimpleEnergyStorage energyStorage = null;
    public void setEnergyLevel(long energyLevel);
}
