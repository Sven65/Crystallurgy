package xyz.mackan.crystallurgy.forge.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.blocks.AbstractFluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.forge.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.forge.networking.ForgeEnergySyncS2CPacket;
import xyz.mackan.crystallurgy.forge.networking.ForgeFluidSyncS2CPacket;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlockEntities;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlocks;
import xyz.mackan.crystallurgy.forge.registry.ForgeModFluids;
import xyz.mackan.crystallurgy.forge.registry.ForgeModMessages;
import xyz.mackan.crystallurgy.forge.util.ModEnergyStorage;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;

import java.util.Optional;

public class FluidSynthesizerBlockEntity extends AbstractFluidSynthesizerBlockEntity implements NamedScreenHandlerFactory {
    public final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(ENERGY_CAPACITY, MAX_ENERGY_INSERT, MAX_ENERGY_EXTRACT) {
        @Override
        public void onEnergyChanged() {
            markDirty();

            if (!world.isClient()) {
                // Sync with packet
                sendEnergyPacket();
            }
        }
    };

    public final FluidTank inputFluidStorage = new FluidTank(20000, 256) {
        @Override
        protected void onContentsChanged() {
            markDirty();
            if(!world.isClient()) {
                sendFluidPacket("input", this);
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            // TODO: Check this against recipe
            return stack.getFluid() == Fluids.WATER;
        }
    };

    public final FluidTank outputFluidStorage = new FluidTank(20000, 256) {
        @Override
        protected void onContentsChanged() {
            markDirty();
            if(!world.isClient()) {
                sendFluidPacket("output", this);
            }
        }
    };

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyInputFluidHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyOutputFluidHandler = LazyOptional.empty();

    public FluidSynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(ForgeModBlockEntities.RESONANCE_FORGE.get(), pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FluidSynthesizerBlockEntity.this.progress;
                    case 1 -> FluidSynthesizerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> FluidSynthesizerBlockEntity.this.progress = value;
                    case 1 -> FluidSynthesizerBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    protected void sendEnergyPacket() {
        ForgeModMessages.sendToClients(new ForgeEnergySyncS2CPacket(this.ENERGY_STORAGE.getEnergyStored(), getPos()));
    }

    @Override
    protected void sendFluidPacket(String slot, Fluid fluid, int amount) {
        ForgeModMessages.sendToClients(new ForgeFluidSyncS2CPacket(new FluidStack(fluid, amount), this.getPos(), slot));
    }

    private void sendFluidPacket(String slot, FluidTank storage) {
        this.sendFluidPacket(slot, storage.getFluid().getFluid(), storage.getFluidAmount());
    }

    @Override
    public void setEnergyLevel(long energyLevel) {
        this.ENERGY_STORAGE.setEnergy((int) energyLevel);
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> void extractEnergy(T entity, long amount) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            fluidSynthesizerBlockEntity.ENERGY_STORAGE.extractEnergy((int) amount, false);
        }
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> boolean hasEnoughEnergy(T entity) {
        if (!this.hasRecipe(entity)) return false;

        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            return fluidSynthesizerBlockEntity.ENERGY_STORAGE.getEnergyStored() >= recipe.get().getEnergyPerTick();
        }

        return false;
    }

    @Override
    public void setInputFluidLevel(Fluid fluid, long fluidLevel) {
        this.inputFluidStorage.setFluid(new FluidStack(fluid, (int) fluidLevel));
    }

    @Override
    public <T extends AbstractFluidSynthesizerBlockEntity> void extractInputFluid(T entity, long amount) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            if (fluidSynthesizerBlockEntity.inputFluidStorage.getFluid().getFluid() == Fluids.EMPTY) return;

            FluidStack drainStack = this.inputFluidStorage.getFluid();
            drainStack.setAmount((int) amount);

            this.inputFluidStorage.drain(drainStack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> boolean hasEnoughInputFluid(T entity) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            return fluidSynthesizerBlockEntity.inputFluidStorage.getFluidAmount() >= 500; // TODO: Recipe check, this is in millibuckets
        }
        return false;
    }

    @Override
    public void setOutputFluidLevel(Fluid fluid, long fluidLevel) {
        this.outputFluidStorage.setFluid(new FluidStack(fluid, (int) fluidLevel));
    }

    @Override
    public <T extends AbstractFluidSynthesizerBlockEntity> long extractOutputFluid(T entity, long amount) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            if (fluidSynthesizerBlockEntity.outputFluidStorage.isEmpty()) return 0;

            FluidStack drainStack = this.outputFluidStorage.getFluid();
            drainStack.setAmount((int) amount);

            this.outputFluidStorage.drain(drainStack, IFluidHandler.FluidAction.EXECUTE);
            return amount;
        }
        return 0;
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> void transferFluidToInputStorage(T entity) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {

            int drainAmount = Math.min(fluidSynthesizerBlockEntity.inputFluidStorage.getSpace(), 1000);

            ItemStack bucketItem = entity.getStack(FLUID_INPUT_SLOT);

            if (bucketItem.getItem() instanceof BucketItem bucket) {
                Fluid fluid = bucket.getFluid();
                FluidStack fluidStack = new FluidStack(fluid, drainAmount);

                fluidSynthesizerBlockEntity.inputFluidStorage.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

                entity.setStack(FLUID_INPUT_SLOT, new ItemStack(Items.BUCKET));
            }
        }
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> void transferFluidFromOutputStorage(T entity) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            Item fluidBucket = fluidSynthesizerBlockEntity.outputFluidStorage.getFluid().getFluid().getBucketItem();
            long extractedAmount = extractOutputFluid(entity, 1000);

            if (extractedAmount > 0) {
                entity.setStack(FLUID_OUTPUT_SLOT, new ItemStack(fluidBucket));
            }
        }
    }

    @Override
    protected void craftFluid() {
        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        // Numbers are different because the getCount is the slot in the recipe, not inventory
        this.removeStack(MATERIAL_0_SLOT, recipe.get().getCount(0));
        this.removeStack(MATERIAL_1_SLOT, recipe.get().getCount(1));

        Fluid outputFluid = recipe.get().getOutputFluid();
        int outputFluidAmount = recipe.get().getOutputFluidAmount();

        this.setOutputFluidLevel(outputFluid, outputFluidStorage.getFluidAmount() + outputFluidAmount);
    }

    @Override
    protected boolean canInsertFluidIntoOutputSlot(Fluid fluidOutput, int fluidOutputAmount) {
        boolean sameFluid = outputFluidStorage.getFluid().equals(fluidOutput)
                || outputFluidStorage.getFluid().isEmpty();

        // Check if there's enough capacity left to insert the new amount
        boolean hasSpace = outputFluidStorage.getCapacity() - outputFluidStorage.getFluidAmount() >= fluidOutputAmount;

        return sameFluid && hasSpace;
    }

    @Override
    protected Optional<FluidSynthesizerRecipe> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());

        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }


        Optional<FluidSynthesizerRecipe> recipe = getWorld().getRecipeManager().getFirstMatch(FluidSynthesizerRecipe.Type.INSTANCE, inv, getWorld());
        if (recipe.isPresent() && recipe.get().matchFluid(getWorld(), this.inputFluidStorage.getFluid().getFluid(), (int) this.inputFluidStorage.getFluidAmount())) {
            return recipe;
        } else {
            return Optional.empty();
        }
    }

    @Override
    protected void onCrafingFinished(AbstractFluidSynthesizerBlockEntity entity, Optional<FluidSynthesizerRecipe> recipe) {
        this.extractInputFluid(entity, recipe.get().getInputFluidAmount());

        sendFluidPacket("input", inputFluidStorage);
        sendFluidPacket("output", outputFluidStorage);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(ForgeModBlocks.FLUID_SYNTHESIZER.get().getTranslationKey());
    }

    // TODO: Return correct screen
    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        sendEnergyPacket();
        sendFluidPacket("input", inputFluidStorage);
        sendFluidPacket("output", outputFluidStorage);
        return new FluidSynthesizerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        inputFluidStorage.writeToNBT(nbt);
        outputFluidStorage.writeToNBT(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inputFluidStorage.readFromNBT(nbt);
        outputFluidStorage.readFromNBT(nbt);
    }
}
