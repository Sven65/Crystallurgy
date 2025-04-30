package xyz.mackan.crystallurgy.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.forge.registry.ForgeModBlocks;
import xyz.mackan.crystallurgy.forge.registry.ForgeModItemGroup;
import xyz.mackan.crystallurgy.forge.registry.ForgeModItems;
import xyz.mackan.crystallurgy.forge.registry.ForgeModRecipes;

@Mod(Constants.MOD_ID)
public final class CrystallurgyForge {
    @SuppressWarnings("removal")
    public CrystallurgyForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Run our common setup.
        CrystallurgyCommon.LOGGER.info("Hello Forge World");

        CrystallurgyCommon.init();

        ForgeModItems.register(modEventBus);
        ForgeModBlocks.register(modEventBus);
        ForgeModItemGroup.register(modEventBus);
        ForgeModRecipes.register(modEventBus);
    }
}
