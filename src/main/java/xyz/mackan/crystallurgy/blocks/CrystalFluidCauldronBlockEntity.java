package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.datagen.ModBlockTagProvider;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModCauldron;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.*;


// TODO: Make particles during processing
// TODO: Make items in inv render at bottom
public class CrystalFluidCauldronBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final List<ItemEntity> itemsBeingProcessed = new ArrayList<>();
    public boolean isHeating = false;

    private int maxProgress = 100;
    private int progress = 0;

    public CrystalFluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTAL_FLUID_CAULDRON, pos, state);
    }

    // Check if an item is already being processed
    public boolean isItemBeingProcessed(ItemEntity itemEntity) {
        return itemsBeingProcessed.contains(itemEntity);
    }

    // Add an item to the processing set
    public void addItemToProcessing(ItemEntity itemEntity) {
        itemsBeingProcessed.add(itemEntity);
        this.progress = 0;
    }

    // Remove an item from the processing set once it is finished
    public void removeItemFromProcessing(ItemEntity itemEntity) {
        itemsBeingProcessed.remove(itemEntity);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void addToInventory(ItemStack toAdd) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack existing = inventory.get(i);

            if (existing.isEmpty()) {
                inventory.set(i, toAdd.copy());
                return;
            } else if (ItemStack.canCombine(existing, toAdd)) {
                int space = existing.getMaxCount() - existing.getCount();
                if (space > 0) {
                    int toMove = Math.min(space, toAdd.getCount());
                    existing.increment(toMove);
                    toAdd.decrement(toMove);

                    if (toAdd.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }

    public void addItemToCauldron(ItemStack itemStack) {
       this.addToInventory(itemStack);

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

    public void tick(World world, BlockPos pos, BlockState state, CrystalFluidCauldronBlockEntity entity) {
        if (world.isClient()) {
            return;
        }

        BlockState blockBelow = world.getBlockState(pos.down());

        isHeating = blockBelow.isIn(ModBlockTagProvider.FLUID_CAULDRON_HEATERS);

        if (isHeating && !this.itemsBeingProcessed.isEmpty() && this.hasRecipe(entity) && this.hasFluid(world)) {
            Optional<CrystalFluidCauldronRecipe> recipe = getCurrentRecipe();

            int recipeTicks = recipe.get().getTicks();
            this.maxProgress = recipeTicks;

            progress++;

            Crystallurgy.LOGGER.info("Progress is "+progress+"/"+maxProgress);

            // TODO: Set fluid level with math progress
            // world.setBlockState(pos, state.with(LeveledCauldronBlock.LEVEL, 0));

            world.setBlockState(pos, state.with(ModCauldron.FLUID_LEVEL, getFluidProgress()));

            if (hasCraftingFinished()) {
                Crystallurgy.LOGGER.info("Crafting finished");
                if (!this.itemsBeingProcessed.isEmpty()) {
                    this.clearFluid(world, pos);
                    this.craftItem(world, pos);

                    for (ItemStack itemStack : inventory) {
                        itemStack.decrement(1);
                    }

                    this.itemsBeingProcessed.clear();

                    resetProgress();
                }
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
        if (this.inventory.isEmpty()) return;
        Optional<ItemStack> firstNonEmpty = inventory.stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst();


        firstNonEmpty.ifPresent(stack -> {
            int index = inventory.indexOf(stack);
            inventory.set(index, ItemStack.EMPTY); // Remove by replacing with EMPTY
            player.setStackInHand(hand, stack);
        });
    }

    public boolean canAcceptItem(ItemStack itemStack) {
        return inventory.stream().anyMatch(ItemStack::isEmpty);
    }
}
