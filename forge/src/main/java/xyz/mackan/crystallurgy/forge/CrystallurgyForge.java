package xyz.mackan.crystallurgy.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.mackan.crystallurgy.Constants;
import xyz.mackan.crystallurgy.CrystallurgyCommon;
import xyz.mackan.crystallurgy.forge.registry.*;

@Mod(Constants.MOD_ID)
public final class CrystallurgyForge {
    @SuppressWarnings("removal")
    public CrystallurgyForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Run our common setup.
        CrystallurgyCommon.LOGGER.info("Hello Forge World");

        CrystallurgyCommon.init();
        ForgeModBlocks.register(modEventBus);
        ForgeModItems.register(modEventBus);
        ForgeModRecipes.register(modEventBus);
        ForgeModFluids.register(modEventBus);
        ForgeModCauldron.register(modEventBus);
        ForgeModBlockEntities.register(modEventBus);
        ForgeModTags.register();

        ForgeModItemGroup.register(modEventBus);

        ForgeModMessages.register();
        ForgeModScreens.register(modEventBus);
    }

}
