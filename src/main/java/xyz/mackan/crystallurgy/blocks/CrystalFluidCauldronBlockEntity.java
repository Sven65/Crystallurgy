package xyz.mackan.crystallurgy.blocks;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.datagen.ModBlockTagProvider;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModCauldron;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.util.CauldronUtil;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


// TODO: Make items in inv render at bottom
public class CrystalFluidCauldronBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    public boolean isHeating = false;
    private boolean isCrafting;

    private int maxProgress = 100;
    private int progress = 0;

    public CrystalFluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTAL_FLUID_CAULDRON, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        //Crystallurgy.LOGGER.info("returning items to caller {}", inventory);
        return inventory;
    }

    public void addToInventory(ItemStack toAdd) {
        // 'this' refers to the instance implementing ImplementedInventory
        // Assuming this method is defined within or accessible to such an instance.

        Crystallurgy.LOGGER.info("Adding itemStack {} to inv", toAdd);

        if (toAdd.isEmpty()) {
            Crystallurgy.LOGGER.warn("Can't add to inv, stack is empty");

            return; // Nothing to add
        }

        // Pass 1: Try to merge with existing stacks
        for (int i = 0; i < this.size(); i++) {
            ItemStack existing = inventory.get(i);

            Crystallurgy.LOGGER.info("Found existing {}", existing);


            // Check if stacks are compatible and there's space
            if (ItemStack.canCombine(toAdd, existing) && existing.getCount() < existing.getMaxCount()) {
                Crystallurgy.LOGGER.info("We can combine");

                int space = existing.getMaxCount() - existing.getCount();
                int toMove = Math.min(toAdd.getCount(), space);

                if (toMove > 0) {
                    existing.increment(toMove); // Modify existing stack in place
                    toAdd.decrement(toMove);
                    markDirty(); // Use this.markDirty() after modification

                    if (toAdd.isEmpty()) {
                        Crystallurgy.LOGGER.info("Added everything...");

                        return; // All items added
                    }
                }
            }
        }

        // Pass 2: Place into empty slots
        if (!toAdd.isEmpty()) {
            Crystallurgy.LOGGER.info("To add is not empty, do pass 2");

            for (int i = 0; i < inventory.size(); i++) { // Use this.size()
                if (inventory.get(i).isEmpty()) { // Use this.getStack()
                    Crystallurgy.LOGGER.info("Checked slot {} is empty", i);

                    int toMove = Math.min(toAdd.getCount(), getMaxCountPerStack()); // Use this.getMaxCountPerStack()
                    ItemStack stackToPlace = toAdd.split(toMove); // split() reduces toAdd and creates the new stack
                    if (!stackToPlace.isEmpty()) {
                        Crystallurgy.LOGGER.info("Setting stack at {} to {}", i, stackToPlace);

                        setStack(i, stackToPlace); // Use this.setStack(). setStack handles placing and calls markDirty.
                    }
                    if (toAdd.isEmpty()) {
                        markDirty();
                        return; // All items added
                    }
                }
            }
        }

        Crystallurgy.LOGGER.warn("FAILED TO ADD ITEM TO INVENTORY???");
        // If the method reaches here, toAdd still contains items that couldn't fit.
        // These items are implicitly dropped/lost as per the void return type.
    }

    public void addItemToCauldron(ItemStack itemStack) {
       if (this.canAcceptItem(itemStack)) this.addToInventory(itemStack);
    }

    public void addItemEntityToCauldron(ItemEntity itemEntity) {
        boolean canAccept = this.canAcceptItem(itemEntity.getStack());

        Crystallurgy.LOGGER.info("Can accept? {}", canAccept);

        if (canAccept) {
            Crystallurgy.LOGGER.info("We can accept, add item.");
            this.addToInventory(itemEntity.getStack());
            itemEntity.setDespawnImmediately();
        }
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


    private int getFluidProgress() {
        float normalizedProgress = (float) this.progress / this.maxProgress;

        // Only return 0 when progress is exactly at max
        if (normalizedProgress >= 0.99999f) {
            return 0;
        }

        // Otherwise, map the fluid level to 1-3 based on progress
        float fluidLevel = 1 - normalizedProgress;
        return Math.max(1, Math.min(3, (int)Math.ceil(fluidLevel * 3)));
    }

    private boolean hasFluid(World world) {
        return world.getBlockState(this.pos).get(ModCauldron.FLUID_LEVEL) > 0;
    }

    public boolean getIsCrafting() {
        return this.isCrafting;
    }



    public void tick(World world, BlockPos pos, BlockState state, CrystalFluidCauldronBlockEntity entity) {
        if (world.isClient()) {
            return;
        }

        if (this.hasFluid(world)) {
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeBlockPos(getPos());
            buf.writeItemStack(CauldronUtil.getItemStack(entity));

            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
                ServerPlayNetworking.send(player, ModMessages.SPAWN_PARTICLES, buf);
            }
        }


        BlockState blockBelow = world.getBlockState(pos.down());

        isHeating = blockBelow.isIn(ModBlockTagProvider.FLUID_CAULDRON_HEATERS);

        if (isHeating && this.hasRecipe(entity) && this.hasFluid(world)) {
            isCrafting = true;

            Optional<CrystalFluidCauldronRecipe> recipe = getCurrentRecipe();

            int recipeTicks = recipe.get().getTicks();
            this.maxProgress = recipeTicks;

            progress++;

            Crystallurgy.LOGGER.info("Progress is "+progress+"/"+maxProgress);

            world.setBlockState(pos, state.with(ModCauldron.FLUID_LEVEL, getFluidProgress()));

            if (hasCraftingFinished()) {
                Crystallurgy.LOGGER.info("Crafting finished");
                this.clearFluid(world, pos);
                this.craftItem(world, pos);

                for (ItemStack itemStack : inventory) {
                    itemStack.decrement(1);
                }

                resetProgress();
            }
        }
    }

    public void clearFluid(World world, BlockPos pos) {
        if (world.isClient()) {
            return;
        }
        BlockState state = world.getBlockState(pos);

        // Check if the block is a cauldron and it contains a fluid
        if (state.getBlock() instanceof CrystalFluidCauldron && state.get(ModCauldron.FLUID_LEVEL) > 0) {
            world.setBlockState(pos, ModCauldron.CRYSTAL_CAULDRON.getDefaultState());
        }
    }

    private void craftItem(World world, BlockPos pos) {
        Optional<CrystalFluidCauldronRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) return;

        ItemStack itemStack = recipe.get().getOutput(null);

        Crystallurgy.LOGGER.info("Spawning item{}", itemStack);

        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY() + 3, pos.getZ(), itemStack);

        Crystallurgy.LOGGER.info("Spawning entity{}", itemEntity);

        world.spawnEntity(itemEntity);
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 100;
        this.isCrafting = false;
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private Optional<CrystalFluidCauldronRecipe> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.inventory.size());

        for (int i = 0; i < this.inventory.size(); i++) {
            inv.setStack(i, this.inventory.get(i));
        }

        Crystallurgy.LOGGER.info("Inv is" + inv);



        return getWorld().getRecipeManager().getFirstMatch(CrystalFluidCauldronRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean hasRecipe(CrystalFluidCauldronBlockEntity entity) {
        Optional<CrystalFluidCauldronRecipe> recipe = getCurrentRecipe();

        return recipe.isPresent();
    }

    public void handleEmptyHandInteraction(Hand hand, PlayerEntity player) {
        if (inventory.isEmpty()) {
            Crystallurgy.LOGGER.warn("[extract] inv empty???");
            return;
        }

        Crystallurgy.LOGGER.info("[extract] inv is {}", inventory);

        Optional<ItemStack> firstNonEmpty = inventory.stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst();

        Crystallurgy.LOGGER.info("[Extract] Found first non empty item {}", firstNonEmpty);


        AtomicInteger index = new AtomicInteger(-1);

        Crystallurgy.LOGGER.info("[extract] is present? {}", firstNonEmpty.isPresent());
        Crystallurgy.LOGGER.info("[extract] item is now? {}", firstNonEmpty);

        if(firstNonEmpty.isPresent()) {
            ItemStack stack = firstNonEmpty.get();
            Crystallurgy.LOGGER.info("[Extract] Removing non empty item {}", stack);
            //player.setStackInHand(hand, stack);
            player.getInventory().insertStack(stack);

            index.set(inventory.indexOf(stack));
            this.removeStack(index.get(), stack.getCount());
            markDirty();
        };

        Crystallurgy.LOGGER.info("[extract] inv is now {}", inventory);


        this.isCrafting = false;
    }

    public boolean canAcceptItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false; // Cannot accept an empty stack
        }

        // Get the underlying list of items using the ImplementedInventory method
        DefaultedList<ItemStack> items = this.getItems(); // Use this.getItems()

        // Check 1: Can the item stack merge with any existing stack that has space?
        boolean canCombineWithExisting = items.stream().anyMatch(existingStack ->
                // Use ItemStack.canCombine to check for matching items and existingStack.getCount()
                // to check if there's space by comparing to getMaxStackSize() of the existing stack.
                ItemStack.canCombine(itemStack, existingStack) && existingStack.getCount() < existingStack.getMaxCount()
        );

        // Check 2: Is there any empty slot in the inventory?
        boolean hasEmptySlot = items.stream().anyMatch(ItemStack::isEmpty);

        // The inventory can accept the item if it can combine with an existing stack OR there is an empty slot.
        return canCombineWithExisting || hasEmptySlot;
    }

}
