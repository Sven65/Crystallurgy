package xyz.mackan.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import xyz.mackan.blocks.ResonanceForgeBlockEntity;

public class ResonanceForgeScreenHandler extends ScreenHandler {
    private final ResonanceForgeBlockEntity forge;
    private final Inventory inventory;
    private final Slot rawMaterialSlot;
    private final Slot catalystSlot;
    private final Slot outputSlot;

    public ResonanceForgeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(3));  // 3 slots: raw material, catalyst, output
    }

    public ResonanceForgeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_3X3, syncId);
        this.inventory = inventory;
        this.forge = new ResonanceForgeBlockEntity(playerInventory.player.getBlockPos(), null);  // Dummy block entity for now

        // Add slots to the container
        this.rawMaterialSlot = this.addSlot(new Slot(inventory, 0, 44, 35));  // Raw material slot
        this.catalystSlot = this.addSlot(new Slot(inventory, 1, 116, 35));  // Catalyst crystal slot
        this.outputSlot = this.addSlot(new Slot(inventory, 2, 180, 35) {  // Output slot
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;  // The player can’t insert into the output
            }
        });

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
        //return this.forge.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        // This is where we can remove or save item stacks when the screen is closed.
        //Inventories.clearAll(this.inventory);
    }

    public ResonanceForgeBlockEntity getForge() {
        return this.forge;
    }
}
