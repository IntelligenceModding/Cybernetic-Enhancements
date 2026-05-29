package de.artemis.cyberneticenhancements.client.tooltip;

import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record UpgradeProgressTooltip(float progress) implements TooltipComponent {
    public UpgradeProgressTooltip {
        progress = Mth.clamp(progress, 0.0F, 1.0F);
    }
}
