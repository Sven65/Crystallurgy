package xyz.mackan.crystallurgy.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.registry.ModFluids;

public class CauldronInteractionHandler {
    public static ActionResult onPlayerInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState blockState = world.getBlockState(pos);

        Crystallurgy.LOGGER.info("Block State" + blockState);

        if (blockState.getBlock() instanceof CauldronBlock) {
            FluidState fluidState = world.getFluidState(pos);

            // If the cauldron is empty and the player is holding a water bucket (or another item for fluid insertion)
            if (fluidState.getFluid() == Fluids.EMPTY) {
                // Handle fluid insertion logic here (e.g., inserting your custom fluid)
                if (player.getStackInHand(hand).getItem() == ModFluids.CRYSTAL_FLUID_BUCKET) {
                    // You can insert your custom fluid here

                    BlockState newState = Blocks.WATER_CAULDRON.getDefaultState().with(Properties.LEVEL_3, 3);
                    world.setBlockState(pos, newState);

                    Crystallurgy.LOGGER.info("Cauldron state at field_31022" + CauldronBlock.field_31022);

                    world.setBlockState(pos, blockState.with(Properties.LEVEL_3, 3));  // Change fluid level
                    return ActionResult.SUCCESS;  // Indicate that the action is handled
                }
            }

            // If the cauldron already has your custom fluid, handle item absorption
//            else if (fluidState.getFluid() == MyMod.MY_CUSTOM_FLUID) {
//                handleItemAbsorption(world, pos, player);
//                return ActionResult.SUCCESS;  // Indicate that the action is handled
//            }
        }

        return ActionResult.PASS;
    }
}
