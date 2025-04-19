package xyz.mackan.crystallurgy.util;

import net.minecraft.item.Item;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    public static List<Item> getAllPublicStaticFinalItems(Class<?> clazz) {
        List<Item> items = new ArrayList<>();

        for (Field field : clazz.getFields()) { // Only public fields
            int modifiers = field.getModifiers();

            // Check if the field is static, final, and of type Item
            if (Modifier.isStatic(modifiers) &&
                    Modifier.isFinal(modifiers) &&
                    Item.class.isAssignableFrom(field.getType())) {
                try {
                    Object value = field.get(null); // static fields have no instance
                    if (value instanceof Item) {
                        items.add((Item) value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace(); // or handle more gracefully
                }
            }
        }

        return items;
    }
}