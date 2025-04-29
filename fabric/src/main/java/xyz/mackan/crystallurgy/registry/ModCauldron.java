package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;
import xyz.mackan.crystallurgy.Crystallurgy;
import xyz.mackan.crystallurgy.blocks.CoolingFluidCauldron;
import xyz.mackan.crystallurgy.blocks.CrystalFluidCauldron;

import java.util.Map;

public class ModCauldron {
    public static final IntProperty FLUID_LEVEL = IntProperty.of("fluid_level", 0, 3);

    // Custom cauldron registration
    public static Block CRYSTAL_CAULDRON;
    public static Block COOLING_CAULDRON;
    public static final Map<Item, CauldronBehavior> CRYSTAL_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    public static final Map<Item, CauldronBehavior> COOLING_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();

    public static void register() {
        // Register cauldron behaviors before registering the block


        // Register custom cauldron
        CRYSTAL_CAULDRON = Registry.register(Registries.BLOCK,
                new Identifier(Crystallurgy.MOD_ID, "crystal_fluid_cauldron"),
                new CrystalFluidCauldron(FabricBlockSettings.copyOf(Blocks.CAULDRON), CRYSTAL_CAULDRON_BEHAVIOR));

        Registry.register(Registries.ITEM,
                new Identifier(Crystallurgy.MOD_ID, "crystal_fluid_cauldron"),
                new BlockItem(CRYSTAL_CAULDRON, new FabricItemSettings()));

        COOLING_CAULDRON = Registry.register(Registries.BLOCK,
                new Identifier(Crystallurgy.MOD_ID, "cooling_fluid_cauldron"),
                new CoolingFluidCauldron(FabricBlockSettings.copyOf(Blocks.CAULDRON), COOLING_CAULDRON_BEHAVIOR));

        Registry.register(Registries.ITEM,
                new Identifier(Crystallurgy.MOD_ID, "cooling_fluid_cauldron"),
                new BlockItem(COOLING_CAULDRON, new FabricItemSettings()));

        setupCauldronBehaviors(ModFluids.CRYSTAL_FLUID_BUCKET, CRYSTAL_CAULDRON, CRYSTAL_CAULDRON_BEHAVIOR);
        setupCauldronBehaviors(ModFluids.COOLING_FLUID_BUCKET, COOLING_CAULDRON, COOLING_CAULDRON_BEHAVIOR);
    }

    private static void setupCauldronBehaviors(Item bucket, Block newCauldron, Map<Item, CauldronBehavior> behaviorMap) {
        // Fill empty cauldron with crystal fluid
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(bucket, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
                world.setBlockState(pos, newCauldron.getDefaultState().with(FLUID_LEVEL, 3));
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        // Empty crystal cauldron with empty bucket
        behaviorMap.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();

                if (state.get(FLUID_LEVEL) < 3) return ActionResult.PASS;

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