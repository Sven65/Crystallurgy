package xyz.mackan.crystallurgy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.registry.ModBlockEntities;

public class CrystalFluidCauldronBlockEntity extends BlockEntity {
    public CrystalFluidCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTAL_FLUID_CAULDRON, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state, CrystalFluidCauldronBlockEntity entity) {
        if (!world.isClient()) {
            return;
        }

        Crystallurgy.LOGGER.info("Ticked fluid cauldron");
    }
}
