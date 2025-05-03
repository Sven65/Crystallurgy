package xyz.mackan.crystallurgy.forge.registry;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.registry.ModItems;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeModItemGroup {
    public static final DeferredRegister<ItemGroup> ITEM_GROUPS = DeferredRegister.create(RegistryKeys.ITEM_GROUP, Constants.MOD_ID);
    public static RegistryObject<ItemGroup> CRYSTALLURGY_TAB = ITEM_GROUPS.register(Constants.MOD_ID, () -> ItemGroup.builder()
            .displayName(Text.translatable("text.crystallurgy.item_group"))
            .icon(() -> new ItemStack(ModItems.DIAMOND_RESONATOR_CRYSTAL)) // Tab icon (using an item as the icon)
            .entries(((displayContext, entries) -> {
                entries.add(ForgeModItems.RESONANCE_FORGE.get());
                //entries.add(ModItems.FLUID_SYNTHESIZER);
                entries.add(ModItems.CRYSTAL_SEED);
                entries.add(ForgeModFluids.CRYSTAL_FLUID_BUCKET.get());
                entries.add(ForgeModFluids.COOLING_FLUID_BUCKET.get());

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
            .build()
    );


    public static void register(IEventBus modEventBus) {
        ITEM_GROUPS.register("item_group", CRYSTALLURGY_TAB);
        ITEM_GROUPS.register(modEventBus);
    }
}
