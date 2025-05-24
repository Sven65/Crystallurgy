package xyz.mackan.crystallurgy.gui;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
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
import xyz.mackan.crystallurgy.block.FluidSynthesizerBlockEntity;
import xyz.mackan.crystallurgy.registry.FabricModScreens;
import xyz.mackan.crystallurgy.util.FluidStack;

public class FluidSynthesizerScreenHandler extends AbstractFluidSynthesizerScreenHandler {
    public final FluidSynthesizerBlockEntity synthesizerBlockEntity;

    public FluidStack inputFluidStack;
    public FluidStack outputFluidStack;

    public FluidSynthesizerScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(4));
    }

    public FluidSynthesizerScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(FabricModScreens.FLUID_SYNTHESIZER_SCREEN_HANDLER, syncId);
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
        long storedEnergy = this.synthesizerBlockEntity.energyStorage.amount;
        long maxEnergy = this.synthesizerBlockEntity.energyStorage.capacity;

        int energyBarSize = 64;

        return Math.min(energyBarSize, (int) (maxEnergy != 0 && storedEnergy != 0 ? storedEnergy * energyBarSize / maxEnergy : 0));
    }

    @Override
    public void setInputFluid(Fluid fluid, int amount) {
        this.inputFluidStack = new FluidStack(FluidVariant.of(fluid), amount);
    }

    @Override
    public void setOutputFluid(Fluid fluid, int amount) {
        this.outputFluidStack = new FluidStack(FluidVariant.of(fluid), amount);;
    }
}
