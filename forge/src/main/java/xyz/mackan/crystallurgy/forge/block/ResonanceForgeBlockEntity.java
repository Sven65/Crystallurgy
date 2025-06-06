package xyz.mackan.crystallurgy.forge.block;

import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.blocks.AbstractResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.forge.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.forge.networking.ForgeEnergySyncS2CPacket;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlockEntities;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlocks;
import xyz.mackan.crystallurgy.forge.registry.ForgeModMessages;
import xyz.mackan.crystallurgy.forge.util.ModEnergyStorage;
import xyz.mackan.crystallurgy.forge.util.StorageUtil;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.util.BlockUtils;

import java.util.Optional;

public class ResonanceForgeBlockEntity extends AbstractResonanceForgeBlockEntity implements NamedScreenHandlerFactory, EnergySyncableBlockEntity {
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

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private LazyOptional<IItemHandler> lazyCatalystItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyRawMaterialItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyDyeItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyOutputItemHandler = LazyOptional.empty();

    private final ItemStackHandler catalystItemHandler = StorageUtil.createItemStorage(1, inventory, CATALYST_SLOT, this::markDirty);
    private final ItemStackHandler rawMaterialItemHandler = StorageUtil.createItemStorage(1, inventory, RAW_MATERIAL_SLOT, this::markDirty);
    private final ItemStackHandler dyeItemHandler = StorageUtil.createItemStorage(1, inventory, DYE_SLOT, this::markDirty);
    private final ItemStackHandler outputItemHandler = StorageUtil.createItemStorage(1, inventory, OUTPUT_SLOT, this::markDirty);

    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ForgeModBlockEntities.RESONANCE_FORGE.get(), pos, state);

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
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        sendEnergyPacket();

        return new ResonanceForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected void sendEnergyPacket() {
        ForgeModMessages.sendToClients(new ForgeEnergySyncS2CPacket(this.ENERGY_STORAGE.getEnergyStored(), getPos()));
    }

    @Override
    public void setEnergyLevel(long energyLevel) {
        this.ENERGY_STORAGE.setEnergy((int) energyLevel);
    }

    @Override
    protected void extractEnergy(AbstractResonanceForgeBlockEntity entity, long amount) {
        if (entity instanceof ResonanceForgeBlockEntity pEntity) {
            pEntity.ENERGY_STORAGE.extractEnergy((int) amount, false);
        }
    }

    @Override
    protected boolean hasEnoughEnergy(AbstractResonanceForgeBlockEntity entity) {
        if (!this.hasRecipe(entity)) return false;
        Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();

        if (entity instanceof ResonanceForgeBlockEntity resonanceForgeEntity) {
            return resonanceForgeEntity.ENERGY_STORAGE.getEnergyStored() >= recipe.get().getEnergyPerTick();
        }

        return false;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(ForgeModBlocks.RESONANCE_FORGE.get().getTranslationKey());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyEnergyHandler          = LazyOptional.of(() -> ENERGY_STORAGE);
        lazyCatalystItemHandler    = LazyOptional.of(() -> catalystItemHandler);
        lazyRawMaterialItemHandler = LazyOptional.of(() -> rawMaterialItemHandler);
        lazyDyeItemHandler         = LazyOptional.of(() -> dyeItemHandler);
        lazyOutputItemHandler      = LazyOptional.of(() -> outputItemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
        lazyCatalystItemHandler.invalidate();
        lazyRawMaterialItemHandler.invalidate();
        lazyDyeItemHandler.invalidate();
        lazyOutputItemHandler.invalidate();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt(String.format("%s.stored_energy", Constants.MOD_ID), ENERGY_STORAGE.getEnergyStored());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        ENERGY_STORAGE.setEnergy(nbt.getInt(String.format("%s.stored_energy", Constants.MOD_ID)));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            BlockUtils.Side side = BlockUtils.getSideFromDirection(this.getCachedState(), direction);
            if (side == BlockUtils.Side.LEFT) return lazyCatalystItemHandler.cast();
            if (side == BlockUtils.Side.BACK) return lazyRawMaterialItemHandler.cast();
            if (side == BlockUtils.Side.RIGHT) return lazyDyeItemHandler.cast();
            if (side == BlockUtils.Side.BOTTOM) return lazyOutputItemHandler.cast();
        }

        return super.getCapability(cap, direction);
    }
}
