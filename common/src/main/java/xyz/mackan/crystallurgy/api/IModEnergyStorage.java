package xyz.mackan.crystallurgy.api;

import net.minecraft.nbt.NbtCompound;

public abstract class IModEnergyStorage {
    protected int maxStorage;
    protected int maxInput;
    protected int maxOutput;

    // Constructor to initialize energy storage
    public IModEnergyStorage(int maxStorage, int maxInput, int maxOutput) {
        this.maxStorage = maxStorage;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
    }

    // Abstract methods to read and write NBT data
    public void readNbt(NbtCompound nbt) {};
    public void writeNbt(NbtCompound nbt) {};

    // Abstract method to commit energy changes
    protected abstract void onFinalCommit();

    // Create method to allow platform-specific energy storage creation
    public static IModEnergyStorage create(int maxStorage, int maxInput, int maxOutput) {
        throw new UnsupportedOperationException("create() should be implemented in platform-specific classes.");
    }
}