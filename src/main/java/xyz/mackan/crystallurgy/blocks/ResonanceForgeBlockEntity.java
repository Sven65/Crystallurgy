package xyz.mackan.crystallurgy.blocks;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.recipe.ResonanceForgeRecipe;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModBlocks;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.Optional;

public class ResonanceForgeBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(100000, 1000, 200) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            if (!world.isClient()) {
                PacketByteBuf data = PacketByteBufs.create();
                data.writeLong(amount);
                data.writeBlockPos(getPos());

                for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
                    ServerPlayNetworking.send(player, ModMessages.ENERGY_SYNC, data);
                }

            }
        }
    };

    private static final int CATALYST_SLOT = 0;
    private static final int RAW_MATERIAL_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    protected final PropertyDelegate propertyDelegate;

    private int maxProgress = 100;
    private int progress = 0;

    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_FORGE, pos, state);
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
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt(String.format("%s.progress", Crystallurgy.MOD_ID));
        energyStorage.amount = nbt.getLong(String.format("%s.stored_energy", Crystallurgy.MOD_ID));
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ResonanceForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state, ResonanceForgeBlockEntity entity) {
        if (world.isClient()) {
            return;
        }


//        if (hasEnergyItem(entity)) {
//            try (Transaction transaction = Transaction.openOuter()) {
//                entity.energyStorage.insert(16, transaction);
//                transaction.commit();;
//            }
//        }

        if (isOutputSlotEmptyOrReceivable()) {
            if (this.hasRecipe(entity) & hasEnoughEnergy(entity)) {
                Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();

                int recipeTicks = recipe.get().getTicks();
                
                this.propertyDelegate.set(1, recipeTicks);

                progress++;
                extractEnergy(entity, recipe.get().getEnergyPerTick());
                markDirty(world, pos, state);

                if (hasCraftingFinished()) {
                    this.craftItem();
                    this.resetProgress();
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            markDirty(world, pos, state);
        }
    }

    private static void extractEnergy(ResonanceForgeBlockEntity entity, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            entity.energyStorage.extract(amount, transaction);
            transaction.commit();
        }
    }

    private boolean hasEnoughEnergy(ResonanceForgeBlockEntity entity) {
        if (!this.hasRecipe(entity)) return false;

        Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();

        return entity.energyStorage.amount >= recipe.get().getEnergyPerTick();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        Direction localDir = this.getWorld().getBlockState(this.pos).get(ResonanceForge.FACING);

        if (side == Direction.UP || side == Direction.DOWN) {
            return false;
        }

        // Insert top 1
        // Right insert 1
        // Left insert 0

        // TODO: Check item tag on slot 0 to only allow crystals

        return switch (localDir) {
            case EAST ->
                    side.rotateYClockwise() == Direction.NORTH && slot == 1 ||
                            side.rotateYClockwise() == Direction.EAST && slot == 1 ||
                            side.rotateYClockwise() == Direction.WEST && slot == 0;
            case SOUTH ->
                    side == Direction.NORTH && slot == 1 ||
                            side == Direction.EAST && slot == 1 ||
                            side == Direction.WEST && slot == 0;
            case WEST ->
                    side.rotateYCounterclockwise() == Direction.NORTH && slot == 1 ||
                            side.rotateYCounterclockwise() == Direction.EAST && slot == 1 ||
                            side.rotateYCounterclockwise() == Direction.WEST && slot == 0;
            default ->
                    side.getOpposite() == Direction.NORTH && slot == 1 ||
                            side.getOpposite() == Direction.EAST && slot == 1 ||
                            side.getOpposite() == Direction.WEST && slot == 0;
        };
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        Direction localDir = this.getWorld().getBlockState(this.pos).get(ResonanceForge.FACING);

        if(side == Direction.UP) {
            return false;
        }

        // Down extract 2
        if(side == Direction.DOWN) {
            return slot == 2;
        }

        // bottom extract 2
        // right extract 2
        return switch (localDir) {
            case EAST -> side.rotateYClockwise() == Direction.SOUTH && slot == 2 ||
                    side.rotateYClockwise() == Direction.EAST && slot == 2;
            case SOUTH -> side == Direction.SOUTH && slot == 2 ||
                    side == Direction.EAST && slot == 2;
            case WEST -> side.rotateYCounterclockwise() == Direction.SOUTH && slot == 2 ||
                    side.rotateYCounterclockwise() == Direction.EAST && slot == 2;
            default -> side.getOpposite() == Direction.SOUTH && slot == 2 ||
                    side.getOpposite() == Direction.EAST && slot == 2;
        };
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 100;
    }

    private void craftItem() {
        Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();
        ItemStack catalystStack = this.getStack(CATALYST_SLOT);

        if(catalystStack.getDamage() <= catalystStack.getMaxDamage()) {
            catalystStack.setDamage(catalystStack.getDamage() + 1);
        }

        if (catalystStack.getMaxDamage() == catalystStack.getDamage()) {
            this.removeStack(CATALYST_SLOT, 1);
        }

        this.removeStack(RAW_MATERIAL_SLOT, 1);

        this.setStack(OUTPUT_SLOT,
                new ItemStack(
                        recipe.get().getOutput(null).getItem(),
                        getStack(OUTPUT_SLOT).getCount() + recipe.get().getOutput(null).getCount()
                )
        );
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }


    private boolean hasRecipe(ResonanceForgeBlockEntity entity) {
        Optional<ResonanceForgeRecipe> recipe = getCurrentRecipe();

        return recipe.isPresent()
                && canInsertAmountIntoOutputSlot(recipe.get().getOutput(null))
                && canInsertItemIntoOutputSlot(recipe.get().getOutput(null).getItem());
    }

    private Optional<ResonanceForgeRecipe> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());

        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }

        return getWorld().getRecipeManager().getFirstMatch(ResonanceForgeRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }
}
