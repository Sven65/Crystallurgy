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
import net.minecraft.fluid.Fluid;
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
import xyz.mackan.crystallurgy.registry.ModFluids;
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
public class CoolingFluidCauldronBlockEntity extends FluidCauldronBlockEntity {
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

    public CoolingFluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COOLING_FLUID_CAULDRON, pos, state);
    }

    public boolean getIsCrafting() {
        return this.isCrafting;
    }

    @Override
    public Fluid getFluid() {
        return ModFluids.STILL_COOLING_FLUID;
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

        Optional<ItemStack> firstNonEmpty = inventory.stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst();

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
