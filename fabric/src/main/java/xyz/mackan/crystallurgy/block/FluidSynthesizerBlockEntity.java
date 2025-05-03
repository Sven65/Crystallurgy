package xyz.mackan.crystallurgy.block;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import xyz.mackan.crystallurgy.blocks.AbstractFluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.registry.FabricModBlocks;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.util.FluidStack;
import xyz.mackan.crystallurgy.util.FluidUtils;

import java.util.Optional;

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
            return FluidStack.convertDropletsToMb(FluidConstants.BUCKET * 20);
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
            if(!world.isClient()) {
                sendFluidPacket("input", this.getBlankVariant().getFluid(), (int) this.amount);
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
            return FluidStack.convertDropletsToMb(FluidConstants.BUCKET * 20);
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
            if(!world.isClient()) {
                sendFluidPacket("output", this.variant.getFluid(), (int) this.amount);
            }
        }
    };

    public FluidSynthesizerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

        new FluidUtils.DecodedFluid(fluid, amount).writePacket(data);

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
    protected void setInputFluidLevel(Fluid fluid, long fluidLevel) {
        this.inputFluidStorage.variant = FluidVariant.of(fluid);
        this.inputFluidStorage.amount = fluidLevel;
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> void extractInputFluid(T entity, long amount) {
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
            return fluidSynthesizerBlockEntity.inputFluidStorage.amount >= 500; // TODO: Recipe check, this is in millibuckets
        }
    }

    @Override
    protected void setOutputFluidLevel(Fluid fluid, long fluidLevel) {
        this.outputFluidStorage.variant = FluidVariant.of(fluid);
        this.outputFluidStorage.amount = fluidLevel;
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> void extractOutputFluid(T entity, long amount) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            if (fluidSynthesizerBlockEntity.outputFluidStorage.variant == FluidVariant.blank()) return;

            try (Transaction transaction = Transaction.openOuter()) {
                fluidSynthesizerBlockEntity.outputFluidStorage.extract(fluidSynthesizerBlockEntity.outputFluidStorage.variant, amount, transaction);
                transaction.commit();
            }
        }
    }

    @Override
    protected <T extends AbstractFluidSynthesizerBlockEntity> void transferFluidToInputStorage(T entity) {
        if (entity instanceof FluidSynthesizerBlockEntity fluidSynthesizerBlockEntity) {
            try (Transaction transaction = Transaction.openOuter()) {
                // TODO: Insert what's actually in the bucket.
                ItemStack bucketItem = entity.getStack(FLUID_INPUT_SLOT);

                if (bucketItem.getItem() instanceof BucketItem bucket) {
                    fluidSynthesizerBlockEntity.inputFluidStorage.insert(
                            FluidVariant.of(bucket.fluid),
                            FluidStack.convertDropletsToMb(FluidConstants.BUCKET),
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
            extractOutputFluid(entity, FluidConstants.BUCKET);

            entity.setStack(FLUID_OUTPUT_SLOT, new ItemStack(fluidSynthesizerBlockEntity.outputFluidStorage.variant.getFluid().getBucketItem()));
        }
    }

    @Override
    protected void craftFluid() {
        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        // Numbers are different because the getCount is the slot in the recipe, not inventory
        this.removeStack(MATERIAL_0_SLOT, recipe.get().getCount(0));
        this.removeStack(MATERIAL_1_SLOT, recipe.get().getCount(1));

        FluidStack outputFluid = recipe.get().getOutputFluid();

        this.setOutputFluidLevel(outputFluid.fluidVariant.getFluid(), outputFluidStorage.amount + outputFluid.amount);
    }

    @Override
    protected boolean canInsertFluidIntoOutputSlot(Fluid fluidOutput, int fluidOutputAmount) {
        boolean sameFluid = outputFluidStorage.getResource().equals(fluidOutput)
                || outputFluidStorage.getResource().isBlank();

        // Check if there's enough capacity left to insert the new amount
        boolean hasSpace = outputFluidStorage.getCapacity() - outputFluidStorage.getAmount() >= fluidOutputAmount;

        return sameFluid && hasSpace;
    }

    @Override
    protected Optional<FluidSynthesizerRecipe> getCurrentRecipe() {
        return Optional.empty();
    }

    @Override
    protected void onCrafingFinished(AbstractFluidSynthesizerBlockEntity entity, Optional<FluidSynthesizerRecipe> recipe) {
        this.extractInputFluid(entity, recipe.get().getInputFluid().amount);

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

    // TODO: Return correct screen
    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        sendEnergyPacket();
        return null;
        //return new ResonanceForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
