package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.registry.ModItems;

public class ForgeModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "crystallurgy");
    public static void register(IEventBus modEventBus) {
        //Registry.register(Registries.ITEM, Constants.id("resonance_forge"), RESONANCE_FORGE);
        //Registry.register(Registries.ITEM, Constants.id("fluid_synthesizer"), FLUID_SYNTHESIZER);

        ITEMS.register("crystal_seed", () -> ModItems.CRYSTAL_SEED);

        ITEMS.register("crystal_seed_resonator_crystal", () -> ModItems.CRYSTAL_SEED_RESONATOR_CRYSTAL);
        ITEMS.register("coal_resonator_crystal", () -> ModItems.COAL_RESONATOR_CRYSTAL);
        ITEMS.register("iron_resonator_crystal", () -> ModItems.IRON_RESONATOR_CRYSTAL);
        ITEMS.register("gold_resonator_crystal", () -> ModItems.GOLD_RESONATOR_CRYSTAL);
        ITEMS.register("diamond_resonator_crystal", () -> ModItems.DIAMOND_RESONATOR_CRYSTAL);
        ITEMS.register("netherite_resonator_crystal", () -> ModItems.NETHERITE_RESONATOR_CRYSTAL);
        ITEMS.register("lapis_resonator_crystal", () -> ModItems.LAPIS_RESONATOR_CRYSTAL);
        ITEMS.register("emerald_resonator_crystal", () -> ModItems.EMERALD_RESONATOR_CRYSTAL);
        ITEMS.register("quartz_resonator_crystal", () -> ModItems.QUARTZ_RESONATOR_CRYSTAL);
        ITEMS.register("redstone_resonator_crystal", () -> ModItems.REDSTONE_RESONATOR_CRYSTAL);

        ITEMS.register("unrefined_crystal_seed_resonator_crystal", () -> ModItems.UNREFINED_CRYSTAL_SEED_RESONATOR_CRYSTAL);
        ITEMS.register("unrefined_coal_resonator_crystal", () -> ModItems.UNREFINED_COAL_RESONATOR_CRYSTAL);
        ITEMS.register("unrefined_iron_resonator_crystal", () -> ModItems.UNREFINED_IRON_RESONATOR_CRYSTAL);
        ITEMS.register("unrefined_gold_resonator_crystal", () -> ModItems.UNREFINED_GOLD_RESONATOR_CRYSTAL);
        ITEMS.register("unrefined_diamond_resonator_crystal", () -> ModItems.UNREFINED_DIAMOND_RESONATOR_CRYSTAL);
        ITEMS.register("unrefined_netherite_resonator_crystal", () -> ModItems.UNREFINED_NETHERITE_RESONATOR_CRYSTAL);
        ITEMS.register("unrefined_lapis_resonator_crystal", () -> ModItems.UNREFINED_LAPIS_RESONATOR_CRYSTAL);
        ITEMS.register("unrefined_emerald_resonator_crystal", () -> ModItems.UNREFINED_EMERALD_RESONATOR_CRYSTAL);
        ITEMS.register("unrefined_quartz_resonator_crystal", () -> ModItems.UNREFINED_QUARTZ_RESONATOR_CRYSTAL);
        ITEMS.register("unrefined_redstone_resonator_crystal", () -> ModItems.UNREFINED_REDSTONE_RESONATOR_CRYSTAL);

        ITEMS.register(modEventBus);
    }
}
