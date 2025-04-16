package xyz.mackan.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import xyz.mackan.Crystallurgy;
import xyz.mackan.blocks.ResonanceForgeBlockEntity;
import xyz.mackan.registry.ModScreens;

public class ResonanceForgeScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ResonanceForgeBlockEntity forge;
    private final Slot catalystSlot;
    private final Slot outputSlot;
    private final PropertyDelegate propertyDelegate;

    public ResonanceForgeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(3));  // 3 slots: raw material, catalyst, output
    }

    public ResonanceForgeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreens.RESONANCE_FORGE_SCREEN_HANDLER, syncId);
        this.forge = new ResonanceForgeBlockEntity(playerInventory.player.getBlockPos(), null);
        this.inventory = inventory;

        this.propertyDelegate = forge.getPropertyDelegate();

        this.addProperties(propertyDelegate);


        // Add slots to the container
        this.catalystSlot = this.addSlot(new Slot(inventory, 0, 26, 35) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return super.canInsert(stack);
            }
        });  // Raw material slot
        Slot rawMaterialSlot = this.addSlot(new Slot(inventory, 1, 51, 35));  // Catalyst crystal slot
        this.outputSlot = this.addSlot(new Slot(inventory, 2, 112, 35) {  // Output slot
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

    public int getEnergy() {
        return propertyDelegate.get(0);
    }

    public int getProgress() {
        return propertyDelegate.get(1);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return;
        }

        Slot slot = this.slots.get(slotIndex);

        switch (slotIndex) {
            case 0 -> {
                // Catalyst
                ItemStack stack = slot.getStack();
                this.forge.setCatalyst(stack);
            }
            case 1 -> {
                // Raw Material
                ItemStack stack = slot.getStack();
                this.forge.setRawMaterial(stack);
            }
        }


        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
        //return this.forge.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // TODO: Fix this cuz it'll crash otherwise.
        // See: https://wiki.fabricmc.net/tutorial:screenhandler
        return null;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.forge.markDirty();
    }

    public ResonanceForgeBlockEntity getForge() {
        return this.forge;
    }
}
