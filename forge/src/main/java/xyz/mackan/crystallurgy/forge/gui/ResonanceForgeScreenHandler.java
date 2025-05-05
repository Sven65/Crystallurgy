package xyz.mackan.crystallurgy.forge.gui;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import xyz.mackan.crystallurgy.forge.block.ResonanceForgeBlockEntity;
import xyz.mackan.crystallurgy.forge.registry.ForgeModTags;
import xyz.mackan.crystallurgy.forge.registry.ForgeModScreens;
import xyz.mackan.crystallurgy.gui.AbstractResonanceForgeScreenHandler;

public class ResonanceForgeScreenHandler extends AbstractResonanceForgeScreenHandler {
    public ResonanceForgeBlockEntity forgeBlockEntity;

    public ResonanceForgeScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(4));
    }

    public ResonanceForgeScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ForgeModScreens.RESONANCE_FORGE_SCREEN.get(), syncId);
        checkSize((Inventory) blockEntity, 4);
        this.inventory = ((Inventory) blockEntity);
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.forgeBlockEntity = ((ResonanceForgeBlockEntity) blockEntity);

        this.addSlot(new Slot(inventory, 0, 7, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(ForgeModTags.RESONATOR_CRYSTALS);
            }
        });
        this.addSlot(new Slot(inventory, 1, 29, 35));
        this.addSlot(new Slot(inventory, 2, 51, 35));

        this.addSlot(new Slot(inventory, 3, 112, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }

    @Override
    public int getScaledEnergyBar() {
        long storedEnergy = this.forgeBlockEntity.ENERGY_STORAGE.getEnergyStored();
        long maxEnergy = this.forgeBlockEntity.ENERGY_STORAGE.getMaxEnergyStored();

        int energyBarSize = 64;

        return Math.min(energyBarSize, (int) (maxEnergy != 0 && storedEnergy != 0 ? storedEnergy * energyBarSize / maxEnergy : 0));
    }
}
