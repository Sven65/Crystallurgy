package xyz.mackan.crystallurgy.forge.gui;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraftforge.fluids.FluidStack;
import xyz.mackan.crystallurgy.forge.block.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.forge.registry.ForgeModScreens;
import xyz.mackan.crystallurgy.gui.AbstractFluidSynthesizerScreenHandler;

public class FluidSynthesizerScreenHandler extends AbstractFluidSynthesizerScreenHandler {
    public final FluidSynthesizerBlockEntity synthesizerBlockEntity;

    public FluidStack inputFluidStack;
    public FluidStack outputFluidStack;

    public FluidSynthesizerScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(4));
    }

    public FluidSynthesizerScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ForgeModScreens.FLUID_SYNTHESIZER_SCREEN_HANDLER.get(), syncId);
        checkSize((Inventory) blockEntity, 4);
        this.inventory = ((Inventory) blockEntity);
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.synthesizerBlockEntity = ((FluidSynthesizerBlockEntity) blockEntity);

        this.inputFluidStack = synthesizerBlockEntity.inputFluidStorage.getFluid();
        this.outputFluidStack = synthesizerBlockEntity.outputFluidStorage.getFluid();

        this.addSlot(new Slot(inventory, 0, 7, 10) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof BucketItem;
            }
        });
        this.addSlot(new Slot(inventory, 1, 7, 54) {
            @Override
            public boolean canInsert(ItemStack stack) {
                // Output slot, don't insert.
                return stack.getItem() == Items.BUCKET;
            }
        });
        this.addSlot(new Slot(inventory, 2, 70, 9));
        this.addSlot(new Slot(inventory, 3, 104, 9));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }

    @Override
    public int getScaledEnergyBar() {
        long storedEnergy = this.synthesizerBlockEntity.ENERGY_STORAGE.getEnergyStored();
        long maxEnergy = this.synthesizerBlockEntity.ENERGY_STORAGE.getMaxEnergyStored();

        int energyBarSize = 64;

        return Math.min(energyBarSize, (int) (maxEnergy != 0 && storedEnergy != 0 ? storedEnergy * energyBarSize / maxEnergy : 0));
    }

    @Override
    public void setInputFluid(Fluid fluid, int amount) {
        this.inputFluidStack = new FluidStack(fluid, amount);
    }

    @Override
    public void setOutputFluid(Fluid fluid, int amount) {
        this.outputFluidStack = new FluidStack(fluid, amount);
    }
}
