package xyz.mackan.crystallurgy.block;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.blocks.AbstractResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.registry.FabricModBlockEntities;
import xyz.mackan.crystallurgy.registry.FabricModBlocks;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.util.StorageUtil;

import java.util.Optional;

public class ResonanceForgeBlockEntity extends AbstractResonanceForgeBlockEntity implements ExtendedScreenHandlerFactory, EnergySyncableBlockEntity {
    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(ENERGY_CAPACITY, MAX_ENERGY_INSERT, MAX_ENERGY_EXTRACT) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            if (!world.isClient()) {
                sendEnergyPacket();
            }
        }
    };

    public final SingleVariantStorage<ItemVariant> catalystStorage = StorageUtil.createItemStorage(inventory, CATALYST_SLOT, this::markDirty);
    public final SingleVariantStorage<ItemVariant> rawMaterialStorage = StorageUtil.createItemStorage(inventory, RAW_MATERIAL_SLOT, this::markDirty);
    public final SingleVariantStorage<ItemVariant> dyeStorage = StorageUtil.createItemStorage(inventory, DYE_SLOT, this::markDirty);
    public final SingleVariantStorage<ItemVariant> outputStorage = StorageUtil.createItemStorage(inventory, OUTPUT_SLOT, this::markDirty);

    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(FabricModBlockEntities.RESONANCE_FORGE, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ResonanceForgeBlockEntity.this.progress;
                    case 1 -> ResonanceForgeBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> ResonanceForgeBlockEntity.this.progress = value;
                    case 1 -> ResonanceForgeBlockEntity.this.maxProgress = value;
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

    @Override
    public void setEnergyLevel(long energyLevel) {
        this.energyStorage.amount = energyLevel;
    }

    @Override
    protected <T extends AbstractResonanceForgeBlockEntity> void extractEnergy(T entity, long amount) {
        if (entity instanceof ResonanceForgeBlockEntity resonanceForgeEntity) {
            try (Transaction transaction = Transaction.openOuter()) {
                resonanceForgeEntity.energyStorage.extract(amount, transaction);
                transaction.commit();
            }
        }
    }

    @Override
    protected boolean hasEnoughEnergy(AbstractResonanceForgeBlockEntity entity) {
        if (!this.hasRecipe(entity)) return false;

        Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();

        if (entity instanceof ResonanceForgeBlockEntity resonanceForgeEntity) {
            return resonanceForgeEntity.energyStorage.amount >= recipe.get().getEnergyPerTick();
        }

        return false;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        sendEnergyPacket();
        return new ResonanceForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(FabricModBlocks.RESONANCE_FORGE.getTranslationKey());
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong(String.format("%s.stored_energy", Constants.MOD_ID), energyStorage.amount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        energyStorage.amount = nbt.getLong(String.format("%s.stored_energy", Constants.MOD_ID));
    }
}
