package xyz.mackan.crystallurgy;

import net.minecraft.util.Identifier;


public class Constants {
    public static final String MOD_ID = "crystallurgy";

    public static Identifier id(String id) {
        return Identifier.of(MOD_ID, id);
    }
}
