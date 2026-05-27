package de.artemis.cyberneticenhancements.common.item;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareConditionHelper;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffect;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlotType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CyberwareItem extends Item {
    private final CyberwareDefinition definition;

    public CyberwareItem(Properties properties, CyberwareDefinition definition) {
        super(properties.stacksTo(1));
        this.definition = definition;
    }

    public CyberwareDefinition getDefinition() {
        return definition;
    }

    public CyberwareSlotType getSlotType() {
        return definition.slotType();
    }

    public CyberwareTier getTier() {
        return definition.tier();
    }

    public int getChromeCost() {
        return definition.chromeCost();
    }

    public int getCapacityBonus() {
        return definition.capacityBonus();
    }

    public List<CyberwareEffect> getEffects() {
        return definition.effects();
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(definition.tier().getColor());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.translatable(definition.descriptionKey()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.cyberware_slot", Component.translatable(definition.slotType().translationKey()))
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.cyberware_tier", Component.translatable(definition.tier().translationKey()))
                .withStyle(definition.tier().getColor()));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.chrome_cost", definition.chromeCost())
                .withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.translatable(
                        "tooltip.cyberneticenhancements.integrity",
                        CyberwareConditionHelper.getIntegrity(stack, definition),
                        CyberwareConditionHelper.getMaxIntegrity(definition))
                .withStyle(ChatFormatting.YELLOW));
        if (definition.supportsChipware()) {
            tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.chip_slots", definition.chipSlotCount())
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        if (definition.supportsModules()) {
            tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.module_slots", definition.moduleSlotCount())
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        if (definition.capacityBonus() != 0) {
            tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.capacity_bonus", definition.capacityBonus())
                    .withStyle(ChatFormatting.GREEN));
        }
        for (CyberwareEffect effect : definition.effects()) {
            tooltipComponents.add(effect.describe().copy().withStyle(ChatFormatting.GREEN));
        }
        if (definition.isPlaceholder()) {
            tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.placeholder_effect").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
