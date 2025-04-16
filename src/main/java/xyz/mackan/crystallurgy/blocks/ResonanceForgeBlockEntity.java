package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.gui.ResonanceForgeScreenHandler;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModBlocks;

public class ResonanceForgeBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    private static final int CATALYST_SLOT = 0;
    private static final int RAW_MATERIAL_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    private static final int INVENTORY_SIZE = 3;

    private final PropertyDelegate propertyDelegate = new ArrayPropertyDelegate(2);

    private int energyStored;
    private int progress;

    private boolean isDirty = false;


    private final Inventory inventory = new SimpleInventory(INVENTORY_SIZE);

    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_FORGE, pos, state);
    }

    public void tick() {
        // Add energy tick, check inputs, process synthesis
        // propertyDelegate.set(0, energyStored);
        // propertyDelegate.set(1, progress);

        this.markDirty();
    }

    public void setCatalyst(ItemStack stack) {
        this.inventory.setStack(CATALYST_SLOT, stack);
        this.isDirty = true;
    }
    @Nullable
    public ItemStack getCatalyst() {
        return this.inventory.getStack(CATALYST_SLOT);
    }

    public void setRawMaterial(ItemStack stack) {
        this.inventory.setStack(RAW_MATERIAL_SLOT, stack);
        this.isDirty = true;
    }

    @Nullable
    public ItemStack getRawMaterial() {
        return this.inventory.getStack(RAW_MATERIAL_SLOT);
    }

    public void setOutput(ItemStack stack) {
        this.inventory.setStack(OUTPUT_SLOT, stack);
        this.isDirty = true;
    }

    @Nullable
    public ItemStack getOutput() {
        return this.inventory.getStack(OUTPUT_SLOT);
    }

    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(ModBlocks.RESONANCE_FORGE.getTranslationKey());
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (world == null) {
            Crystallurgy.LOGGER.warn("Attempted to create menu for block entity at " + pos + " before world was set.");
            return null; // Return null or handle the case appropriately
        }

        return new ResonanceForgeScreenHandler(syncId, playerInventory, this.inventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putInt("energy", energyStored);
        nbt.putInt("progress", progress);
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            nbt.put("slot_" + i, inventory.getStack(i).writeNbt(new NbtCompound()));
        }

        Crystallurgy.LOGGER.info("Saving inventory?" + nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt); // Always call super first

        Crystallurgy.LOGGER.info("Read nbt" + nbt);


        // Load custom data
        this.energyStored = nbt.getInt("energy");
        this.progress = nbt.getInt("progress");

        // Clear the inventory before loading - prevents item duplication if readNbt is called multiple times
        this.inventory.clear();
        // Load inventory contents
        // Write inventory data to NBT
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            this.inventory.setStack(i, ItemStack.fromNbt(nbt.getCompound("slot_" + i)));
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        if (world == null || world.isClient()) {
            return null;  // Avoid sending update packet on the client side
        }

        // Send the block entity update packet if on the server side
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // This reads the NBT data sent from the server on the client side.
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        Crystallurgy.LOGGER.info("initial nbt");

        return createNbt(); // createNbt calls writeNbt internally
    }

    // Mark the block entity dirty whenever its state changes that needs saving or syncing
    @Override
    public void markDirty() {
        if (!isDirty) return;

        Crystallurgy.LOGGER.info("Called mark dirty");


        if (world == null) {
            Crystallurgy.LOGGER.warn("World is null for block entity at " + pos);
            return; // Skip if world is null
        }
        if (!world.isClient()) {
            Crystallurgy.LOGGER.info("Marking ResonanceForgeBlockEntity as dirty at " + pos);
            world.updateNeighbors(pos, getCachedState().getBlock()); // Notify neighbors, often sufficient
            super.markDirty(); // Marks the chunk dirty for saving
        } else {
            Crystallurgy.LOGGER.warn("Attempted to mark dirty on the client side at " + pos);
        }

        isDirty = false;

    }
}
