package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.datagen.ModBlockTagProvider;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


// TODO: Make particles during processing
/* GOAL: Cauldron with custom cooling fluid, surrounded by cooling blocks, which give different cooling scores, which get combined.
 * Recipe includes a min cooling score (maybe max too)
 * Will work like CrystalFluidCauldron in that it uses recipes and takes fluid.
 *
 * Something like this for the blocks
 * int coolingScore = 0;
 * if (block == Blocks.ICE) coolingScore += 1;
 * if (block == Blocks.BLUE_ICE) coolingScore += 3;
 * if (block == Blocks.SNOW_BLOCK) coolingScore += 2;
 * if (block == Blocks.FIRE) coolingScore -= 5;
 *
 * But, try to give them custom props (maybe with tags) in order to define cooling score somewhere else in a list, sort of like block tags.
 */
public class CoolingFluidCauldronBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final DefaultedList<ItemEntity> itemEntitiesInCauldron = DefaultedList.ofSize(2);
    private List<ItemEntity> itemsBeingProcessed = new ArrayList<>();
    public boolean isHeating = false;

    private static final int SEED_SLOT = 0;
    private static final int MATERIAL_SLOT = 1;

    private int maxProgress = 100;
    private int progress = 0;

    public CoolingFluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTAL_FLUID_CAULDRON, pos, state);
    }

    // Check if an item is already being processed
    public boolean isItemBeingProcessed(ItemEntity itemEntity) {
        return itemsBeingProcessed.contains(itemEntity);
    }

    // Add an item to the processing set
    public void addItemToProcessing(ItemEntity itemEntity) {
        itemsBeingProcessed.add(itemEntity);
    }

    // Remove an item from the processing set once it is finished
    public void removeItemFromProcessing(ItemEntity itemEntity) {
        itemsBeingProcessed.remove(itemEntity);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void addItemToCauldron(ItemEntity entity) {
        this.itemEntitiesInCauldron.add(entity);
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

    public void tick(World world, BlockPos pos, BlockState state, CoolingFluidCauldronBlockEntity entity) {
        if (world.isClient()) {
            return;
        }

        BlockState blockBelow = world.getBlockState(pos.down());

        isHeating = blockBelow.isIn(ModBlockTagProvider.FLUID_CAULDRON_HEATERS);

        if (isHeating && !this.itemsBeingProcessed.isEmpty() && this.hasRecipe(entity)) {
            Optional<CrystalFluidCauldronRecipe> recipe = getCurrentRecipe();

            int recipeTicks = recipe.get().getTicks();
            this.maxProgress = recipeTicks;

            progress++;

            Crystallurgy.LOGGER.info("Progress is "+progress+"/"+maxProgress);

            // TODO: Set fluid level with math progress
            // world.setBlockState(pos, state.with(LeveledCauldronBlock.LEVEL, 0));

            if (hasCraftingFinished()) {
                Crystallurgy.LOGGER.info("Crafting finished");
                if (!this.itemsBeingProcessed.isEmpty()) {
                    this.clearFluid(world, pos);
                    this.craftItem(world, pos);

                    this.itemsBeingProcessed.get(0).getStack().decrement(1);
                    this.itemsBeingProcessed.remove(0);
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
        if (state.getBlock() instanceof CrystalFluidCauldron && state.get(LeveledCauldronBlock.LEVEL) > 0) {
            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
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
        SimpleInventory inv = new SimpleInventory(this.itemEntitiesInCauldron.size());

        for (int i = 0; i < this.itemEntitiesInCauldron.size(); i++) {
            inv.setStack(i, this.itemEntitiesInCauldron.get(i).getStack());
        }

        Crystallurgy.LOGGER.info("Inv is" + inv);

        return getWorld().getRecipeManager().getFirstMatch(CrystalFluidCauldronRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean hasRecipe(CoolingFluidCauldronBlockEntity entity) {
        Optional<CrystalFluidCauldronRecipe> recipe = getCurrentRecipe();

        return recipe.isPresent();
    }
}
