package de.artemis.cyberneticenhancements.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class StyledNameItem extends Item {
    private final Style nameStyle;

    public StyledNameItem(Properties properties, ChatFormatting nameColor) {
        super(properties);
        this.nameStyle = Style.EMPTY.withColor(TextColor.fromLegacyFormat(nameColor));
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(nameStyle);
    }
}
