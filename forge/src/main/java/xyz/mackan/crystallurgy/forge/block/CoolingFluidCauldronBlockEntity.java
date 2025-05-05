package xyz.mackan.crystallurgy.forge.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.blocks.AbstractFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlockEntities;
import xyz.mackan.crystallurgy.forge.registry.ForgeModFluids;
import xyz.mackan.crystallurgy.recipe.CoolingFluidCauldronRecipe;
import xyz.mackan.crystallurgy.registry.ModProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CoolingFluidCauldronBlockEntity extends AbstractFluidCauldronBlockEntity {
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

    public CoolingFluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ForgeModBlockEntities.COOLING_FLUID_CAULDRON.get(), pos, state);
    }

    public boolean getIsCrafting() {
        return this.isCrafting;
    }

    @Override
    public Fluid getFluid() {
        return ForgeModFluids.STILL_COOLING_FLUID.get();
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
            // TODO: Fix this packet
            //ForgeModMessages.sendToClients(new ForgeSpawnParticleS2CPacket(getPos(), CauldronUtil.getItemStack(entity)));
        }

        int coolingScore = getCoolingScore(world, pos);

        if (this.hasRecipe(entity) && this.hasFluid(world)) {
            Optional<CoolingFluidCauldronRecipe> recipe = getCurrentRecipe();
            if (coolingScore < recipe.get().getCoolingScore()) {
                return;
            }

            int recipeTicks = recipe.get().getTicks();
            this.maxProgress = recipeTicks;

            progress++;

            world.setBlockState(pos, state.with(ModProperties.FLUID_LEVEL, getFluidProgress()));

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
}
