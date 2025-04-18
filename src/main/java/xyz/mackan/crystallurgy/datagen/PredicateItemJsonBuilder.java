package xyz.mackan.crystallurgy.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.Item;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PredicateItemJsonBuilder extends ItemModelGenerator {
    public PredicateItemJsonBuilder(BiConsumer<Identifier, Supplier<JsonElement>> writer) {
        super(writer);
    }

    public record Override() {}

    public static JsonObject createItemModelWithOverrides(
            Identifier id,
            Map<TextureKey, Identifier> textures,
            List<Pair<Map<String, Number>, Identifier>> overrides
    ) {
        // Create the base model JSON (e.g., "generated" with one layer)
        JsonObject jsonObject = Models.GENERATED.createJson(id, textures);

        // Create overrides array
        JsonArray jsonOverrides = new JsonArray();
        for (Pair<Map<String, Number>, Identifier> override : overrides) {
            JsonObject overrideObj = new JsonObject();
            JsonObject predicate = new JsonObject();

            for (Map.Entry<String, Number> entry : override.getLeft().entrySet()) {
                predicate.addProperty(entry.getKey(), entry.getValue());
            }

            overrideObj.add("predicate", predicate);
            overrideObj.addProperty("model", override.getRight().toString());
            jsonOverrides.add(overrideObj);
        }

        jsonObject.add("overrides", jsonOverrides);
        return jsonObject;
    }
}
