package de.artemis.cyberneticenhancements.common.item;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffect;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleDefinition;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class CyberwareModuleItem extends Item {
    private final CyberwareModuleDefinition definition;

    public CyberwareModuleItem(Properties properties, CyberwareModuleDefinition definition) {
        super(properties.stacksTo(1).rarity(definition.tier().getRarity()));
        this.definition = definition;
    }

    public CyberwareModuleDefinition getDefinition() {
        return definition;
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(definition.tier().getColor());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.translatable(definition.descriptionKey()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.module_category", Component.translatable(definition.category().translationKey()))
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.cyberware_tier", Component.translatable(definition.tier().translationKey()))
                .withStyle(definition.tier().getColor()));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.chrome_cost", definition.chromeCost())
                .withStyle(ChatFormatting.AQUA));
        for (CyberwareEffect effect : definition.effects()) {
            tooltipComponents.add(effect.describe().copy().withStyle(ChatFormatting.GREEN));
        }
    }
}
