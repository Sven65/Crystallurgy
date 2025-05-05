package xyz.mackan.crystallurgy.forge.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.blocks.AbstractFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.forge.networking.ForgeSpawnParticleS2CPacket;
import xyz.mackan.crystallurgy.forge.registry.*;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.registry.ModProperties;
import xyz.mackan.crystallurgy.util.CauldronUtil;

import java.util.Optional;

public class CrystalFluidCauldronBlockEntity extends AbstractFluidCauldronBlockEntity {
    public boolean isHeating = false;

    public CrystalFluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ForgeModBlockEntities.CRYSTAL_FLUID_CAULDRON.get(), pos, state);
    }

    public boolean getIsCrafting() {
        return this.isCrafting;
    }

    @Override
    public Fluid getFluid() {
        return ForgeModFluids.STILL_CRYSTAL_FLUID.get();
    }

    public void tick(World world, BlockPos pos, BlockState state, CrystalFluidCauldronBlockEntity entity) {
        if (world.isClient()) {
            return;
        }

        if (this.hasFluid(world)) {
            // TODO: Fix this packet
            //ForgeModMessages.sendToClients(new ForgeSpawnParticleS2CPacket(getPos(), CauldronUtil.getItemStack(entity)));
        }

        BlockState blockBelow = world.getBlockState(pos.down());

        isHeating = blockBelow.isIn(ForgeModTags.FLUID_CAULDRON_HEATERS);

        if (isHeating && this.hasRecipe(entity) && this.hasFluid(world)) {
            isCrafting = true;

            Optional<CrystalFluidCauldronRecipe> recipe = getCurrentRecipe();

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
        Optional<CrystalFluidCauldronRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) return;

        ItemStack itemStack = recipe.get().getOutput(null);

        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY() + 3, pos.getZ(), itemStack);

        world.spawnEntity(itemEntity);
    }

    @Override
    protected void resetProgress() {
        super.resetProgress();
        this.isCrafting = false;
    }

    private Optional<CrystalFluidCauldronRecipe> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.inventory.size());

        for (int i = 0; i < this.inventory.size(); i++) {
            inv.setStack(i, this.inventory.get(i));
        }

        return getWorld().getRecipeManager().getFirstMatch(CrystalFluidCauldronRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean hasRecipe(CrystalFluidCauldronBlockEntity entity) {
        Optional<CrystalFluidCauldronRecipe> recipe = getCurrentRecipe();

        return recipe.isPresent();
    }
}