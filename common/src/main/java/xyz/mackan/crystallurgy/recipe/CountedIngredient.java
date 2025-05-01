package xyz.mackan.crystallurgy.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;

public class CountedIngredient {
    private final Ingredient ingredient;
    private final int count;

    public static final CountedIngredient EMPTY = new CountedIngredient(Ingredient.EMPTY, 0);

    public CountedIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public static CountedIngredient ofItems(int count, Item... items) {
        Ingredient ingredient = Ingredient.ofItems(items);
        return new CountedIngredient(ingredient, count);
    }

    public static CountedIngredient ofStacks(int count, ItemStack... itemStacks) {
        Ingredient ingredient = Ingredient.ofStacks(itemStacks);
        return new CountedIngredient(ingredient, count);
    }

    public static CountedIngredient ofItems(Item... items) {
        return ofItems(1, items);
    }

    public static CountedIngredient ofStacks(ItemStack... itemStacks) {
        return ofStacks(1, itemStacks);
    }

    public Ingredient toIngredient() {
        return ingredient;
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    public int getCount() {
        return count;
    }

    public static CountedIngredient fromJson(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        Ingredient ing = Ingredient.fromJson(obj);
        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
        return new CountedIngredient(ing, count);
    }

    public void write(PacketByteBuf buf) {
        ingredient.write(buf);
        buf.writeVarInt(count);
    }

    public static CountedIngredient read(PacketByteBuf buf) {
        Ingredient ing = Ingredient.fromPacket(buf);
        int count = buf.readVarInt();
        return new CountedIngredient(ing, count);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        // We only need to store the "item" and "count" properties directly
        json.addProperty("item", ingredient.getMatchingStacks()[0].getItem().getRegistryEntry().registryKey().getValue().toString());

        // Add count if it's greater than 1
        if (count > 1) {
            json.addProperty("count", count);
        }

        return json;
    }

    public ItemStack[] getMatchingStacks() {
        return ingredient.getMatchingStacks();
    }
}