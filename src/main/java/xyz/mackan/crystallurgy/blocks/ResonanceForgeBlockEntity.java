package xyz.mackan.crystallurgy.blocks;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModBlocks;
import xyz.mackan.crystallurgy.registry.ModItems;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

public class ResonanceForgeBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    private static final int CATALYST_SLOT = 0;
    private static final int RAW_MATERIAL_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    protected final PropertyDelegate propertyDelegate;

    private int energyStored = 0;
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
                    case 2 -> ResonanceForgeBlockEntity.this.energyStored;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> ResonanceForgeBlockEntity.this.progress = value;
                    case 1 -> ResonanceForgeBlockEntity.this.maxProgress = value;
                    case 2 -> ResonanceForgeBlockEntity.this.energyStored = value;
                };
            }

            @Override
            public int size() {
                return 3;
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

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt(String.format("%s.progress", Crystallurgy.MOD_ID), progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt(String.format("%s.progress", Crystallurgy.MOD_ID));
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ResonanceForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }

        // TODO: Check for RF
        if (isOutputSlotEmptyOrReceivable()) {
            if (this.hasRecipe()) {
               this.increaseCraftProgress();
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

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        // TODO: REmove durability of catalyst
        this.removeStack(RAW_MATERIAL_SLOT, 1);
        ItemStack result = new ItemStack(Items.DIAMOND);

        this.setStack(OUTPUT_SLOT, new ItemStack(result.getItem(), getStack(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        ItemStack result = new ItemStack(Items.DIAMOND);
        boolean hasCatalyst = getStack(CATALYST_SLOT).getItem() == ModItems.DIAMOND_RESONATOR_CRYSTAL;
        boolean hasRawMaterials = getStack(RAW_MATERIAL_SLOT).getItem() == Items.COAL_BLOCK;

        return hasCatalyst && hasRawMaterials && canInsertAmountIntoOutputSlot(result) && canInsertItemIntoOutputSlot(result.getItem());
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
