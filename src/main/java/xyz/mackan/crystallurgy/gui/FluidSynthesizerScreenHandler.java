package xyz.mackan.crystallurgy.gui;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.registry.ModScreens;
import xyz.mackan.crystallurgy.util.FluidStack;

public class FluidSynthesizerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final FluidSynthesizerBlockEntity synthesizerBlockEntity;

    public FluidStack inputFluidStack;
    public FluidStack outputFluidStack;

    public FluidSynthesizerScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(4));
    }

    public FluidSynthesizerScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreens.FLUID_SYNTHESIZER_SCREEN_HANDLER, syncId);
        checkSize((Inventory) blockEntity, 4);
        this.inventory = ((Inventory) blockEntity);
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.synthesizerBlockEntity = ((FluidSynthesizerBlockEntity) blockEntity);

        this.inputFluidStack = new FluidStack(synthesizerBlockEntity.inputFluidStorage.variant, synthesizerBlockEntity.inputFluidStorage.amount);
        this.outputFluidStack = new FluidStack(synthesizerBlockEntity.outputFluidStorage.variant, synthesizerBlockEntity.outputFluidStorage.amount);

        this.addSlot(new Slot(inventory, 0, 7, 10) {
            @Override
            public boolean canInsert(ItemStack stack) {
                // TODO: Only allow fluid buckets (water)
                return true;
            }
        });
        this.addSlot(new Slot(inventory, 1, 7, 54) {
            @Override
            public boolean canInsert(ItemStack stack) {
                // Output slot, don't insert.
                return false;
            }
        });
        this.addSlot(new Slot(inventory, 2, 70, 9));
        this.addSlot(new Slot(inventory, 3, 104, 9));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }

    public boolean isCrafting() {
        return propertyDelegate.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);

        int progressArrowSize = 57;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getScaledEnergyBar() {
        long storedEnergy = this.synthesizerBlockEntity.energyStorage.amount;
        long maxEnergy = this.synthesizerBlockEntity.energyStorage.capacity;

        int energyBarSize = 64;

        return (int) (maxEnergy != 0 && storedEnergy != 0 ? storedEnergy * energyBarSize / maxEnergy : 0);
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public void setInputFluid(FluidStack fluidStack) {
        this.inputFluidStack = fluidStack;
    }

    public void setOutputFluid(FluidStack fluidStack) {
        this.outputFluidStack = fluidStack;
    }

}
