package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import xyz.mackan.crystallurgy.Constants;

public class FabricModItemGroup {
    public static final ItemGroup CRYSTALLURGY = FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(ModItems.DIAMOND_RESONATOR_CRYSTAL))
            .displayName(Text.translatable("text.crystallurgy.item_group"))
            .entries(((displayContext, entries) -> {
                entries.add(FabricModItems.RESONANCE_FORGE);
                //entries.add(FabricModItems.FLUID_SYNTHESIZER);
                entries.add(ModItems.CRYSTAL_SEED);
                entries.add(FabricModFluids.CRYSTAL_FLUID_BUCKET);
                entries.add(FabricModFluids.COOLING_FLUID_BUCKET);

                entries.add(ModItems.CRYSTAL_SEED_RESONATOR_CRYSTAL);
                entries.add(ModItems.COAL_RESONATOR_CRYSTAL);
                entries.add(ModItems.IRON_RESONATOR_CRYSTAL);
                entries.add(ModItems.GOLD_RESONATOR_CRYSTAL);
                entries.add(ModItems.DIAMOND_RESONATOR_CRYSTAL);
                entries.add(ModItems.NETHERITE_RESONATOR_CRYSTAL);
                entries.add(ModItems.LAPIS_RESONATOR_CRYSTAL);
                entries.add(ModItems.EMERALD_RESONATOR_CRYSTAL);
                entries.add(ModItems.QUARTZ_RESONATOR_CRYSTAL);
                entries.add(ModItems.REDSTONE_RESONATOR_CRYSTAL);

                entries.add(ModItems.UNREFINED_CRYSTAL_SEED_RESONATOR_CRYSTAL);
                entries.add(ModItems.UNREFINED_COAL_RESONATOR_CRYSTAL);
                entries.add(ModItems.UNREFINED_IRON_RESONATOR_CRYSTAL);
                entries.add(ModItems.UNREFINED_GOLD_RESONATOR_CRYSTAL);
                entries.add(ModItems.UNREFINED_DIAMOND_RESONATOR_CRYSTAL);
                entries.add(ModItems.UNREFINED_NETHERITE_RESONATOR_CRYSTAL);
                entries.add(ModItems.UNREFINED_LAPIS_RESONATOR_CRYSTAL);
                entries.add(ModItems.UNREFINED_EMERALD_RESONATOR_CRYSTAL);
                entries.add(ModItems.UNREFINED_QUARTZ_RESONATOR_CRYSTAL);
                entries.add(ModItems.UNREFINED_REDSTONE_RESONATOR_CRYSTAL);
            }))
            .build();

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, Constants.id("item_group"), CRYSTALLURGY);
    };
}
