package de.artemis.cyberneticenhancements.common.item;

import de.artemis.cyberneticenhancements.common.cyberware.ChipwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class ChipwareItem extends Item {
    private final ChipwareDefinition definition;

    public ChipwareItem(Properties properties, ChipwareDefinition definition) {
        super(properties.stacksTo(1).rarity(definition.tier().getRarity()));
        this.definition = definition;
    }

    public ChipwareDefinition getDefinition() {
        return definition;
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(definition.tier().getColor());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.translatable(definition.descriptionKey()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.chipware", definition.chromeCost())
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.cyberware_tier", Component.translatable(definition.tier().translationKey()))
                .withStyle(definition.tier().getColor()));
        for (CyberwareEffect effect : definition.effects()) {
            tooltipComponents.add(effect.describe().copy().withStyle(ChatFormatting.GREEN));
        }
    }
}
