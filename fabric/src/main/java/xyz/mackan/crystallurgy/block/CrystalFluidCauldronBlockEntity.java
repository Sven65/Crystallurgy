package xyz.mackan.crystallurgy.block;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.blocks.AbstractFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.datagen.ModBlockTagProvider;
import xyz.mackan.crystallurgy.recipe.CrystalFluidCauldronRecipe;
import xyz.mackan.crystallurgy.registry.FabricModBlockEntities;
import xyz.mackan.crystallurgy.registry.FabricModFluids;
import xyz.mackan.crystallurgy.registry.ModMessages;
import xyz.mackan.crystallurgy.registry.ModProperties;
import xyz.mackan.crystallurgy.util.CauldronUtil;

import java.util.Optional;

public class CrystalFluidCauldronBlockEntity extends AbstractFluidCauldronBlockEntity {
    public boolean isHeating = false;

    public CrystalFluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(FabricModBlockEntities.CRYSTAL_FLUID_CAULDRON, pos, state);
    }

    public boolean getIsCrafting() {
        return this.isCrafting;
    }

    @Override
    public Fluid getFluid() {
        return FabricModFluids.STILL_CRYSTAL_FLUID;
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