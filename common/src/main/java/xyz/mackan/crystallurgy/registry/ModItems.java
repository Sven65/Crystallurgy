package xyz.mackan.crystallurgy.registry;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import xyz.mackan.crystallurgy.items.CatalystCrystalItem;
import xyz.mackan.crystallurgy.items.CrystalSeed;
import xyz.mackan.crystallurgy.items.UnstackableItem;

public class ModItems {
    public static final Item RESONANCE_FORGE = new BlockItem(ModBlocks.RESONANCE_FORGE, new Item.Settings());
    public static final Item FLUID_SYNTHESIZER = new BlockItem(ModBlocks.FLUID_SYNTHESIZER, new Item.Settings());

    public static final Item CRYSTAL_SEED = new CrystalSeed();

    public static final Item CRYSTAL_SEED_RESONATOR_CRYSTAL = new CatalystCrystalItem(1);
    public static final Item COAL_RESONATOR_CRYSTAL         = new CatalystCrystalItem(64);
    public static final Item IRON_RESONATOR_CRYSTAL         = new CatalystCrystalItem(24);
    public static final Item GOLD_RESONATOR_CRYSTAL         = new CatalystCrystalItem(24);
    public static final Item DIAMOND_RESONATOR_CRYSTAL      = new CatalystCrystalItem(32);
    public static final Item NETHERITE_RESONATOR_CRYSTAL    = new CatalystCrystalItem(16);
    public static final Item LAPIS_RESONATOR_CRYSTAL        = new CatalystCrystalItem(20);
    public static final Item EMERALD_RESONATOR_CRYSTAL      = new CatalystCrystalItem(20);
    public static final Item QUARTZ_RESONATOR_CRYSTAL       = new CatalystCrystalItem(20);
    public static final Item REDSTONE_RESONATOR_CRYSTAL     = new CatalystCrystalItem(20);

    public static final Item UNREFINED_CRYSTAL_SEED_RESONATOR_CRYSTAL = new UnstackableItem();
    public static final Item UNREFINED_COAL_RESONATOR_CRYSTAL         = new UnstackableItem();
    public static final Item UNREFINED_IRON_RESONATOR_CRYSTAL         = new UnstackableItem();
    public static final Item UNREFINED_GOLD_RESONATOR_CRYSTAL         = new UnstackableItem();
    public static final Item UNREFINED_DIAMOND_RESONATOR_CRYSTAL      = new UnstackableItem();
    public static final Item UNREFINED_NETHERITE_RESONATOR_CRYSTAL    = new UnstackableItem();
    public static final Item UNREFINED_LAPIS_RESONATOR_CRYSTAL        = new UnstackableItem();
    public static final Item UNREFINED_EMERALD_RESONATOR_CRYSTAL      = new UnstackableItem();
    public static final Item UNREFINED_QUARTZ_RESONATOR_CRYSTAL       = new UnstackableItem();
    public static final Item UNREFINED_REDSTONE_RESONATOR_CRYSTAL     = new UnstackableItem();

    public static void register() {

    }
}
