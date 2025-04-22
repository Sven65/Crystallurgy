package xyz.mackan.crystallurgy.blocks;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;
import xyz.mackan.crystallurgy.registry.ModCauldron;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.util.CauldronUtil;
import xyz.mackan.crystallurgy.util.ImplementedInventory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


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
    // TODO: Try making this block tags
    private Map<Block, Integer> COOLING_SCORES = new HashMap<>() {
        {
            put(Blocks.ICE, 1);
            put(Blocks.PACKED_ICE, 1);
            put(Blocks.BLUE_ICE, 2);
            put(Blocks.POWDER_SNOW, 1);
            put(Blocks.SNOW_BLOCK, 1);
        }
    };

    private boolean isCrafting = false;

    private int maxProgress = 100;
    private int progress = 0;

    public CoolingFluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COOLING_FLUID_CAULDRON, pos, state);
    }


    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void markDirty() {
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        super.markDirty();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt(String.format("%s.progress", Crystallurgy.MOD_ID), progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        inventory.clear();
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt(String.format("%s.progress", Crystallurgy.MOD_ID));
    }

    public void addToInventory(ItemStack toAdd) {
        Crystallurgy.LOGGER.info("add item {}", toAdd);
        if (toAdd.isEmpty()) {
            return;
        }

        // Pass 1: Try to merge with existing stacks
        for (int i = 0; i < this.size(); i++) {
            ItemStack existing = inventory.get(i);
            // Check if stacks are compatible and there's space
            if (ItemStack.canCombine(toAdd, existing) && existing.getCount() < existing.getMaxCount()) {
                int space = existing.getMaxCount() - existing.getCount();
                int toMove = Math.min(toAdd.getCount(), space);

                if (toMove > 0) {
                    existing.increment(toMove); // Modify existing stack in place
                    toAdd.decrement(toMove);
                    markDirty(); // Use this.markDirty() after modification

                    if (toAdd.isEmpty()) {
                        Crystallurgy.LOGGER.info("Inventory is now {}", inventory);

                        return; // All items added
                    }
                }
            }
        }

        // Pass 2: Place into empty slots
        if (!toAdd.isEmpty()) {
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i).isEmpty()) {
                    int toMove = Math.min(toAdd.getCount(), getMaxCountPerStack()); // Use this.getMaxCountPerStack()
                    ItemStack stackToPlace = toAdd.split(toMove); // split() reduces toAdd and creates the new stack
                    if (!stackToPlace.isEmpty()) {
                        setStack(i, stackToPlace); // Use this.setStack(). setStack ha ndles placing and calls markDirty.
                    }
                    if (toAdd.isEmpty()) {
                        markDirty();

                        Crystallurgy.LOGGER.info("Inventory is now {}", inventory);

                        return; // All items added
                    }
                }
            }
        }
    }

    public boolean getIsCrafting() {
        return this.isCrafting;
    }

    public void addItemEntityToCauldron(ItemEntity itemEntity) {
        boolean canAccept = this.canAcceptItem(itemEntity.getStack());

        if (canAccept) {
            this.addToInventory(itemEntity.getStack());
            //itemEntity.setDespawnImmediately();
        }
    }

    public int getCoolingScore(World world, BlockPos startPos) {
        List<BlockPos> cardinals = List.of(
                startPos.up(), startPos.down(), startPos.east(), startPos.west(), startPos.north(), startPos.south()
        );

        int coolingScore = 0;

        for(BlockPos nextBlockPos : cardinals) {
            BlockState blockState = world.getBlockState(nextBlockPos);
            Block block = blockState.getBlock();

            if (!COOLING_SCORES.containsKey(block)) {
                continue;
            }

            coolingScore += COOLING_SCORES.get(block);
        }

        return coolingScore;
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

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void tick(World world, BlockPos pos, BlockState state, CoolingFluidCauldronBlockEntity entity) {
        if (world.isClient() && this.hasFluid(world)) {
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

        int coolingScore = getCoolingScore(world, pos);

        if (this.hasRecipe(entity) && this.hasFluid(world)) {
            Optional<CoolingFluidCauldronRecipe> recipe = getCurrentRecipe();

            Crystallurgy.LOGGER.info("Cooling level is {}", coolingScore);

            if (coolingScore < recipe.get().getCoolingScore()) {
                return;
            }

            int recipeTicks = recipe.get().getTicks();
            this.maxProgress = recipeTicks;

            progress++;

            Crystallurgy.LOGGER.info("Progress is {}/{}", progress, maxProgress);

            world.setBlockState(pos, state.with(ModCauldron.FLUID_LEVEL, getFluidProgress()));

            if (hasCraftingFinished()) {
                this.clearFluid(world, pos);
                this.craftItem(world, pos);

                for (ItemStack itemStack : inventory) {
                    itemStack.decrement(1);
                }

                markDirty();
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
        if (state.getBlock() instanceof CoolingFluidCauldron && state.get(ModCauldron.FLUID_LEVEL) > 0) {
            world.setBlockState(pos, ModCauldron.COOLING_CAULDRON.getDefaultState());
        }
    }

    private void craftItem(World world, BlockPos pos) {
        Optional<CoolingFluidCauldronRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) return;

        ItemStack itemStack = recipe.get().getOutput(null);

        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY() + 3, pos.getZ(), itemStack);

        world.spawnEntity(itemEntity);
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 100;
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private Optional<CoolingFluidCauldronRecipe> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.inventory.size());

        for (int i = 0; i < this.inventory.size(); i++) {
            inv.setStack(i, this.inventory.get(i));
        }

        return getWorld().getRecipeManager().getFirstMatch(CoolingFluidCauldronRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean hasRecipe(CoolingFluidCauldronBlockEntity entity) {
        Optional<CoolingFluidCauldronRecipe> recipe = getCurrentRecipe();

        return recipe.isPresent();
    }

    public void handleEmptyHandInteraction(Hand hand, PlayerEntity player) {
        if (inventory.isEmpty()) {
            return;
        }

        Crystallurgy.LOGGER.info("Inventory is {}", inventory);

        Optional<ItemStack> firstNonEmpty = inventory.stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst();

        Crystallurgy.LOGGER.info("firstNonEmpty is {}", firstNonEmpty);

        if (firstNonEmpty.isPresent()) {
            ItemStack stack = firstNonEmpty.get();
            int index = inventory.indexOf(stack);
            player.getInventory().insertStack(stack);

            inventory.set(index, ItemStack.EMPTY);

            this.markDirty();
        }

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
