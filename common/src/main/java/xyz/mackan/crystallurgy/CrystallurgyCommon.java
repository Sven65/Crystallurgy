package xyz.mackan.crystallurgy;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.mackan.crystallurgy.registry.ModMessages;

public class CrystallurgyCommon {
    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);

    public static void init() {
        // Write common init code here.
        LOGGER.info("Hello Common World!");

        ModMessages.register();
    }
}