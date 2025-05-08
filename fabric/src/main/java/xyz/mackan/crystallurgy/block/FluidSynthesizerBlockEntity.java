package xyz.mackan.crystallurgy.block;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.blocks.AbstractFluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.gui.FluidSynthesizerScreenHandler;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.registry.FabricModBlockEntities;
import xyz.mackan.crystallurgy.registry.FabricModBlocks;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.util.FluidStack;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class FluidSynthesizerBlockEntity extends AbstractFluidSynthesizerBlockEntity implements ExtendedScreenHandlerFactory, EnergySyncableBlockEntity {
    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(ENERGY_CAPACITY, MAX_ENERGY_INSERT, MAX_ENERGY_EXTRACT) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            if (!world.isClient()) {
                sendEnergyPacket();
            }
        }
    };

    public final SingleVariantStorage<FluidVariant> inputFluidStorage = new SingleVariantStorage<FluidVariant>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant transferVariant) {
            return FluidConstants.BUCKET * 20;
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
            if(!world.isClient()) {
                sendFluidPacket("input", this);
            }
        }
    };

    public final SingleVariantStorage<FluidVariant> outputFluidStorage = new SingleVariantStorage<FluidVariant>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant transferVariant) {
            return FluidConstants.BUCKET * 20;
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
            if(!world.isClient()) {
                sendFluidPacket("output", this);
            }
        }
    };

    public FluidSynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(FabricModBlockEntities.FLUID_SYNTHESIZER, pos, state);

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
        PacketByteBuf data = PacketByteBufs.create();
        data.writeLong(energyStorage.amount);
        data.writeBlockPos(getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
            ServerPlayNetworking.send(player, ModMessages.ENERGY_SYNC, data);
        }
    }

    private void sendFluidPacket(String slot, SingleVariantStorage<FluidVariant> storage) {
        this.sendFluidPacket(slot, storage.getResource().getFluid(), (int) storage.getAmount());
    }

    @Override
    protected void sendFluidPacket(String slot, Fluid fluid, int amount) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeString(slot);

        if (Objects.equals(slot, "input")) {
            inputFluidStorage.variant.toPacket(data);
            data.writeLong(inputFluidStorage.amount);
        } else if (Objects.equals(slot, "output")) {
            outputFluidStorage.variant.toPacket(data);
            data.writeLong(outputFluidStorage.amount);
        }

        data.writeBlockPos(getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
            ServerPlayNetworking.send(player, ModMessages.FLUID_SYNC, data);
        }
    }

    @Override
    public void setEnergyLevel(long energyLevel) {
        this.energyStorage.amount = energyLevel;
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> void extractEnergy(T entity, long amount) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            try (Transaction transaction = Transaction.openOuter()) {
                fluidSynthesizerBlockEntity.energyStorage.extract(amount, transaction);
                transaction.commit();
            }
        }
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> boolean hasEnoughEnergy(T entity) {
        if (!this.hasRecipe(entity)) return false;

        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            return fluidSynthesizerBlockEntity.energyStorage.amount >= recipe.get().getEnergyPerTick();
        }

        return false;
    }

    @Override
    public void setInputFluidLevel(Fluid fluid, long fluidLevel) {
        this.inputFluidStorage.variant = FluidVariant.of(fluid);
        this.inputFluidStorage.amount = fluidLevel;
    }

    @Override
    public <T extends AbstractFluidSynthesizerBlockEntity> void extractInputFluid(T entity, long amount) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            if (fluidSynthesizerBlockEntity.inputFluidStorage.variant == FluidVariant.blank()) return;

            try (Transaction transaction = Transaction.openOuter()) {
                fluidSynthesizerBlockEntity.inputFluidStorage.extract(fluidSynthesizerBlockEntity.inputFluidStorage.variant, amount, transaction);
                transaction.commit();
            }
        }
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> boolean hasEnoughInputFluid(T entity) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            if (!this.hasRecipe(entity)) return false;
            Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();
            return fluidSynthesizerBlockEntity.inputFluidStorage.amount >= recipe.get().getInputFluidAmount() * 81L;
        }
        return false;
    }

    @Override
    public void setOutputFluidLevel(Fluid fluid, long fluidLevel) {
        this.outputFluidStorage.variant = FluidVariant.of(fluid);
        this.outputFluidStorage.amount = fluidLevel;
    }

    @Override
    public <T extends AbstractFluidSynthesizerBlockEntity> long extractOutputFluid(T entity, long amount) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            if (fluidSynthesizerBlockEntity.outputFluidStorage.variant == FluidVariant.blank()) return 0;

            try (Transaction transaction = Transaction.openOuter()) {
                fluidSynthesizerBlockEntity.outputFluidStorage.extract(fluidSynthesizerBlockEntity.outputFluidStorage.variant, amount, transaction);
                transaction.commit();

                return (int) amount;
            }
        }
        return 0;
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> void transferFluidToInputStorage(T entity) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            try (Transaction transaction = Transaction.openOuter()) {
                ItemStack bucketItem = entity.getStack(FLUID_INPUT_SLOT);

                if (bucketItem.getItem() instanceof BucketItem bucket) {
                    if (bucket.fluid == Fluids.EMPTY) return;
                    fluidSynthesizerBlockEntity.inputFluidStorage.insert(
                            FluidVariant.of(bucket.fluid),
                            FluidConstants.BUCKET,
                            transaction
                    );

                    transaction.commit();
                    entity.setStack(FLUID_INPUT_SLOT, new ItemStack(Items.BUCKET));
                }
            }
        }
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> void transferFluidFromOutputStorage(T entity) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            Item fluidBucket = fluidSynthesizerBlockEntity.outputFluidStorage.variant.getFluid().getBucketItem();
            long extractedAmount = extractOutputFluid(entity, FluidConstants.BUCKET);

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
        int outputFluidAmount = recipe.get().getOutputFluidAmount() * 81;

        this.setOutputFluidLevel(outputFluid, outputFluidStorage.amount + outputFluidAmount);
    }

    @Override
    protected boolean canInsertFluidIntoOutputSlot(Fluid fluidOutput, int fluidOutputAmount) {
        boolean sameFluid = outputFluidStorage.getResource().getFluid().equals(fluidOutput)
                || outputFluidStorage.getResource().isBlank();

        // Check if there's enough capacity left to insert the new amount
        boolean hasSpace = outputFluidStorage.getCapacity() - outputFluidStorage.getAmount() >= fluidOutputAmount * 81L;

        return sameFluid && hasSpace;
    }

    @Override
    protected Optional<FluidSynthesizerRecipe> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());

        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }


        Optional<FluidSynthesizerRecipe> recipe = getWorld().getRecipeManager().getFirstMatch(FluidSynthesizerRecipe.Type.INSTANCE, inv, getWorld());
        if (recipe.isPresent() && recipe.get().matchFluid(getWorld(), this.inputFluidStorage.variant.getFluid(), this.inputFluidStorage.amount)) {
            return recipe;
        } else {
            return Optional.empty();
        }
    }

    @Override
    protected void onCrafingFinished(AbstractFluidSynthesizerBlockEntity entity, Optional<FluidSynthesizerRecipe> recipe) {
        this.extractInputFluid(entity, recipe.get().getInputFluidAmount() * 81L);

        sendFluidPacket("input", inputFluidStorage);
        sendFluidPacket("output", outputFluidStorage);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(FabricModBlocks.FLUID_SYNTHESIZER.getTranslationKey());
    }

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

        nbt.put("synthesizer_input_fluid.variant", inputFluidStorage.variant.toNbt());
        nbt.putLong("synthesizer_input_fluid.level", inputFluidStorage.amount);

        nbt.put("synthesizer_output_fluid.variant", outputFluidStorage.variant.toNbt());
        nbt.putLong("synthesizer_output_fluid.level", outputFluidStorage.amount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inputFluidStorage.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("synthesizer_input_fluid.variant"));
        inputFluidStorage.amount = nbt.getLong("synthesizer_input_fluid.level");

        outputFluidStorage.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("synthesizer_output_fluid.variant"));
        outputFluidStorage.amount = nbt.getLong("synthesizer_output_fluid.level");
    }
}
