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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import xyz.mackan.crystallurgy.gui.FluidSynthesizerScreenHandler;
import xyz.mackan.crystallurgy.recipe.FluidSynthesizerRecipe;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModBlocks;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.util.FluidStack;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.Optional;

public class FluidSynthesizerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, EnergySyncableBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

    private static final int FLUID_INPUT_SLOT = 0;
    private static final int FLUID_OUTPUT_SLOT = 1;
    private static final int MATERIAL_0_SLOT = 2;
    private static final int MATERIAL_1_SLOT = 3;

    protected final PropertyDelegate propertyDelegate;

    private int maxProgress = 100;
    private int progress = 0;

    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(100000, 10000, 200000) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            if (!world.isClient()) {
                sendEnergyPacket();
            }
        }
    };

    private void sendEnergyPacket() {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeLong(energyStorage.amount);
        data.writeBlockPos(getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
            ServerPlayNetworking.send(player, ModMessages.ENERGY_SYNC, data);
        }
    }

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
            return FluidStack.convertDropletsToMb(FluidConstants.BUCKET * 20);
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
            if(!world.isClient()) {
                sendFluidPacket("output", this);
            }
        }
    };

    private void sendFluidPacket(String slot, SingleVariantStorage<FluidVariant> storage) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeString(slot);
        storage.variant.toPacket(data);
        data.writeLong(storage.amount);
        data.writeBlockPos(getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
            ServerPlayNetworking.send(player, ModMessages.FLUID_SYNC, data);
        }
    }

    public FluidSynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_SYNTHESIZER, pos, state);

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
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(ModBlocks.RESONANCE_FORGE.getTranslationKey());
    }

    public void setEnergyLevel(long energyLevel) {
        this.energyStorage.amount = energyLevel;
    }

    public void setInputFluidLevel(FluidVariant fluidVariant, long fluidLevel) {
        this.inputFluidStorage.variant = fluidVariant;
        this.inputFluidStorage.amount = fluidLevel;
    }

    public void setOutputFluidLevel(FluidVariant fluidVariant, long fluidLevel) {
        this.outputFluidStorage.variant = fluidVariant;
        this.outputFluidStorage.amount = fluidLevel;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt(String.format("%s.progress", Crystallurgy.MOD_ID), progress);
        nbt.putLong(String.format("%s.stored_energy", Crystallurgy.MOD_ID), energyStorage.amount);

        nbt.put("synthesizer_input_fluid.variant", inputFluidStorage.variant.toNbt());
        nbt.putLong("synthesizer_input_fluid.level", inputFluidStorage.amount);

        nbt.put("synthesizer_output_fluid.variant", outputFluidStorage.variant.toNbt());
        nbt.putLong("synthesizer_output_fluid.level", outputFluidStorage.amount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt(String.format("%s.progress", Crystallurgy.MOD_ID));
        energyStorage.amount = nbt.getLong(String.format("%s.stored_energy", Crystallurgy.MOD_ID));

        inputFluidStorage.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("synthesizer_input_fluid.variant"));
        inputFluidStorage.amount = nbt.getLong("synthesizer_input_fluid.level");

        outputFluidStorage.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("synthesizer_output_fluid.variant"));
        outputFluidStorage.amount = nbt.getLong("synthesizer_output_fluid.level");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        sendEnergyPacket();
        sendFluidPacket("input", inputFluidStorage);
        sendFluidPacket("output", outputFluidStorage);
        return new FluidSynthesizerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    private static void extractEnergy(FluidSynthesizerBlockEntity entity, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            entity.energyStorage.extract(amount, transaction);
            transaction.commit();
        }
    }

    public static void extractInputFluid(FluidSynthesizerBlockEntity entity, long amount) {
        if (entity.inputFluidStorage.variant == FluidVariant.blank()) return;

        try (Transaction transaction = Transaction.openOuter()) {
            entity.inputFluidStorage.extract(entity.inputFluidStorage.variant, amount, transaction);
            transaction.commit();
        }
    }

    public static void extractOutputFluid(FluidSynthesizerBlockEntity entity, long amount) {
        if (entity.outputFluidStorage.variant == FluidVariant.blank()) return;

        try (Transaction transaction = Transaction.openOuter()) {
            entity.outputFluidStorage.extract(entity.outputFluidStorage.variant, amount, transaction);
            transaction.commit();
        }
    }

    private boolean hasEnoughItems(int slot) {
        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) return false;
        int count = recipe.get().getCount(slot);
        ItemStack stackInSlot = this.getStack(slot);
        int slotCount = stackInSlot.isEmpty() ? 64 : stackInSlot.getCount();

        return slotCount >= count;
    }


    private boolean hasRecipe(FluidSynthesizerBlockEntity entity) {
        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        return recipe.isPresent()
                && hasEnoughItems(MATERIAL_0_SLOT)
                && hasEnoughItems(MATERIAL_1_SLOT)
                && canInsertFluidIntoOutputSlot(recipe.get().getOutputFluid());
    }

    private boolean canInsertFluidIntoOutputSlot(FluidStack fluidOutput) {
        boolean sameFluid = outputFluidStorage.getResource().equals(fluidOutput.getFluidVariant())
                || outputFluidStorage.getResource().isBlank();

        // Check if there's enough capacity left to insert the new amount
        boolean hasSpace = outputFluidStorage.getCapacity() - outputFluidStorage.getAmount() >= fluidOutput.getAmount();

        return sameFluid && hasSpace;
    }

    private Optional<FluidSynthesizerRecipe> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());

        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }


        Optional<FluidSynthesizerRecipe> recipe = getWorld().getRecipeManager().getFirstMatch(FluidSynthesizerRecipe.Type.INSTANCE, inv, getWorld());
        if (recipe.isPresent() && recipe.get().matchFluid(getWorld(), new FluidStack(this.inputFluidStorage.variant, this.inputFluidStorage.amount))) {
            return recipe;
        } else {
            return Optional.empty();
        }
    }

    private boolean hasEnoughEnergy(FluidSynthesizerBlockEntity entity) {
        if (!this.hasRecipe(entity)) return false;

        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        return entity.energyStorage.amount >= recipe.get().getEnergyPerTick();
    }


    public void tick(World world, BlockPos pos, BlockState state, FluidSynthesizerBlockEntity entity) {
        if (world.isClient()) {
            return;
        }

        if (hasFluidSourceInSlot(entity)) {
            transferFluidToInputStorage(entity);
        }

        if (hasBucketInOutputSlot(entity)) {
            transferFluidFromOutputStorage(entity);
        }

        if (hasRecipe(entity) && hasEnoughEnergy(entity) && hasEnoughFluid(entity)) {
            Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

            int recipeTicks = recipe.get().getTicks();
            this.propertyDelegate.set(1, recipeTicks);

            entity.progress++;

            extractEnergy(entity, recipe.get().getEnergyPerTick());
            markDirty();

            if (hasCraftingFinished()) {
                this.craftFluid();
                this.extractInputFluid(entity, recipe.get().getInputFluid().amount);

                sendFluidPacket("input", inputFluidStorage);
                sendFluidPacket("output", outputFluidStorage);

                this.resetProgress();
                markDirty();
            }
        } else {
            this.resetProgress();
        }
    }


    private void craftFluid() {
        Optional<FluidSynthesizerRecipe> recipe = getCurrentRecipe();

        // Numbers are different because the getCount is the slot in the recipe, not inventory
        this.removeStack(MATERIAL_0_SLOT, recipe.get().getCount(0));
        this.removeStack(MATERIAL_1_SLOT, recipe.get().getCount(1));

        FluidStack outputFluid = recipe.get().getOutputFluid();

        this.setOutputFluidLevel(outputFluid.fluidVariant, outputFluidStorage.amount + outputFluid.amount);
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 0;
    }


    private void transferFluidToInputStorage(FluidSynthesizerBlockEntity entity) {
        try (Transaction transaction = Transaction.openOuter()) {
            // TODO: Insert what's actually in the bucket.
            ItemStack bucketItem = entity.getStack(FLUID_INPUT_SLOT);

            if (bucketItem.getItem() instanceof BucketItem bucket) {
                entity.inputFluidStorage.insert(
                        FluidVariant.of(bucket.fluid),
                        FluidStack.convertDropletsToMb(FluidConstants.BUCKET),
                        transaction
                );

                transaction.commit();
                entity.setStack(FLUID_INPUT_SLOT, new ItemStack(Items.BUCKET));
            }
        }
    }

    private boolean hasFluidSourceInSlot(FluidSynthesizerBlockEntity entity) {
        return entity.getStack(FLUID_INPUT_SLOT).getItem() == Items.WATER_BUCKET; // TODO: Check if is fluid bucket.
    }

    private static boolean hasEnoughFluid(FluidSynthesizerBlockEntity entity) {
        return entity.inputFluidStorage.amount >= 500; // TODO: Recipe check, this is in millibuckets.
    }

    private void transferFluidFromOutputStorage(FluidSynthesizerBlockEntity entity) {
        extractOutputFluid(entity, FluidConstants.BUCKET);

        entity.setStack(FLUID_OUTPUT_SLOT, new ItemStack(entity.outputFluidStorage.variant.getFluid().getBucketItem()));
    }

    private boolean hasBucketInOutputSlot(FluidSynthesizerBlockEntity entity) {
        return entity.getStack(FLUID_OUTPUT_SLOT).getItem() == Items.BUCKET;
    }
}