package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.world.event.GameEvent;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.block.CoolingFluidCauldronBlock;
import xyz.mackan.crystallurgy.block.CrystalFluidCauldronBlock;

import java.util.Map;

public class FabricModCauldron {
    public static Block CRYSTAL_CAULDRON;
    public static Block COOLING_CAULDRON;
    public static final Map<Item, CauldronBehavior> CRYSTAL_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    public static final Map<Item, CauldronBehavior> COOLING_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();

    public static void register() {
        // Register custom cauldron
        CRYSTAL_CAULDRON = Registry.register(Registries.BLOCK,
                Constants.id("crystal_fluid_cauldron"),
                new CrystalFluidCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON), CRYSTAL_CAULDRON_BEHAVIOR));

        COOLING_CAULDRON = Registry.register(Registries.BLOCK,
                Constants.id("cooling_fluid_cauldron"),
                new CoolingFluidCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON), COOLING_CAULDRON_BEHAVIOR));

        setupCauldronBehaviors(FabricModFluids.CRYSTAL_FLUID_BUCKET, CRYSTAL_CAULDRON, CRYSTAL_CAULDRON_BEHAVIOR);
        setupCauldronBehaviors(FabricModFluids.COOLING_FLUID_BUCKET, COOLING_CAULDRON, COOLING_CAULDRON_BEHAVIOR);
    }

    // TODO: Make crystal fluid insertable in cooling cauldron and vice versa.
    private static void setupCauldronBehaviors(Item bucket, Block newCauldron, Map<Item, CauldronBehavior> behaviorMap) {
        // Fill empty cauldron with crystal fluid
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(bucket, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));

                world.setBlockState(pos, newCauldron.getDefaultState().with(ModProperties.FLUID_LEVEL, 3));
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        // Empty crystal cauldron with empty bucket
        behaviorMap.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();

                if (!state.contains(ModProperties.FLUID_LEVEL)) {
                    return ActionResult.PASS;
                }

                if (state.get(ModProperties.FLUID_LEVEL) < 3) return ActionResult.PASS;

                player.setStackInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(bucket)));
                world.setBlockState(pos, state.getBlock().getDefaultState());
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.success(world.isClient);
        });

        // Add all the basic empty cauldron behaviors
        behaviorMap.putAll(CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR);
    }

    // Utility class for item operations (simplified version of vanilla's ItemUsage)
    private static class ItemUtils {
        public static ItemStack createFilledResult(ItemStack stack, net.minecraft.entity.player.PlayerEntity player, ItemStack output) {
            boolean bl = player.getAbilities().creativeMode;
            if (bl) {
                if (!player.getInventory().contains(output)) {
                    player.getInventory().insertStack(output);
                }
                return stack;
            }
            return output;
        }
    }
}
