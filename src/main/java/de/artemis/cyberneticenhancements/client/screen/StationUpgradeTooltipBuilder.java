package de.artemis.cyberneticenhancements.client.screen;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

final class StationUpgradeTooltipBuilder {
    record Tooltip(List<Component> lines, boolean showProgressBar) {
    }

    private StationUpgradeTooltipBuilder() {
    }

    static Tooltip build(
            Component title,
            CyberwareTier supportedTier,
            CyberwareTier nextTier,
            ItemStack requiredComponent,
            boolean canUpgrade,
            boolean hasRequiredUpgradeComponent,
            String defaultHintKey
    ) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(title);
        tooltip.add(Component.translatable(
                "screen.cyberneticenhancements.ripper_station.slot_supported_tier",
                Component.translatable(supportedTier.translationKey()).withStyle(supportedTier.getColor())
        ));

        if (supportedTier == CyberwareTier.TIER_5) {
            tooltip.add(Component.translatable("screen.cyberneticenhancements.ripper_station.slot_upgrade_maxed"));
            return new Tooltip(tooltip, false);
        }

        if (!requiredComponent.isEmpty()) {
            tooltip.add(Component.translatable(
                    "screen.cyberneticenhancements.ripper_station.slot_upgrade_cost",
                    Component.literal("1x ").append(requiredComponent.getHoverName())
            ));
        }

        if (!canUpgrade) {
            tooltip.add(Component.translatable(defaultHintKey));
        } else if (hasRequiredUpgradeComponent) {
            tooltip.add(Component.translatable(
                    "screen.cyberneticenhancements.ripper_station.slot_upgrade_hold",
                    Component.translatable(nextTier.translationKey()).withStyle(nextTier.getColor())
            ));
        } else {
            tooltip.add(Component.translatable(
                    "screen.cyberneticenhancements.ripper_station.slot_upgrade_missing",
                    requiredComponent.isEmpty() ? Component.empty() : requiredComponent.getHoverName()
            ));
        }

        return new Tooltip(tooltip, true);
    }
}
