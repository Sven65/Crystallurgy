package xyz.mackan.crystallurgy.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CatalystCrystalItem extends Item {
    public CatalystCrystalItem(int durability) {
        super(new Item.Settings().maxCount(1).maxDamage(durability));
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getDamage() == 0) {
            super.appendTooltip(stack, world, tooltip, context);

            int maxDamage = stack.getMaxDamage();
            int currentDamage = maxDamage - stack.getDamage();

            tooltip.add(Text.translatable("item.durability", currentDamage, maxDamage).formatted(Formatting.WHITE));
        }
    }
}
