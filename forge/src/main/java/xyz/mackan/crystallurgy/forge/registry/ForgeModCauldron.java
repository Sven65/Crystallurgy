package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.world.event.GameEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.blocks.AbstractFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.forge.block.CrystalFluidCauldronBlock;
import xyz.mackan.crystallurgy.forge.block.CrystalFluidCauldronBlockEntity;
import xyz.mackan.crystallurgy.registry.ModProperties;

import java.util.Map;

public class ForgeModCauldron {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS =  DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    public static final Map<Item, CauldronBehavior> CRYSTAL_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();

    public static final RegistryObject<Block> CRYSTAL_CAULDRON =
            BLOCKS.register("crystal_fluid_cauldron", () -> new CrystalFluidCauldronBlock(AbstractBlock.Settings.copy(Blocks.CAULDRON), CRYSTAL_CAULDRON_BEHAVIOR));


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        eventBus.addListener(ForgeModCauldron::onCommonSetup);
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            setupCauldronBehaviors(ForgeModFluids.CRYSTAL_FLUID_BUCKET.get(), CRYSTAL_CAULDRON.get(), CRYSTAL_CAULDRON_BEHAVIOR);
        });
    }

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
