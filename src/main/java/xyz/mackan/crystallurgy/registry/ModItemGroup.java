package xyz.mackan.crystallurgy.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.mackan.crystallurgy.Crystallurgy;

public class ModItemGroup {
    public static final ItemGroup CRYSTALLURGY = FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(ModItems.DIAMOND_RESONATOR_CRYSTAL))
            // TODO: Make this a translatable
            .displayName(Text.literal("Crystallurgy"))
            .entries(((displayContext, entries) -> {
                entries.add(ModItems.RESONANCE_FORGE);
                entries.add(ModItems.DIAMOND_RESONATOR_CRYSTAL);
                entries.add(ModItems.CRYSTAL_SEED);
                entries.add(ModFluids.CRYSTAL_FLUID_BUCKET);
            }))
            .build();

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(Crystallurgy.MOD_ID, "item_group"), CRYSTALLURGY);
    };
}
