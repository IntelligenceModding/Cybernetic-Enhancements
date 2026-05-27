package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.level.ItemLike;

import java.util.List;

public record CyberwareModuleDefinition(
        String id,
        String displayName,
        CyberwareModuleCategory category,
        CyberwareTier tier,
        String description,
        ItemLike motifItem,
        List<CyberwareEffect> effects
) {
    public String descriptionKey() {
        return "tooltip.cyberneticenhancements.module." + id;
    }

    public int chromeCost() {
        int categoryBase = switch (category) {
            case ARMS -> 3;
            case LEGS -> 2;
        };
        int tierBonus = switch (tier) {
            case TIER_1 -> 0;
            case TIER_2 -> 1;
            case TIER_3 -> 2;
            case TIER_4 -> 4;
            case TIER_5 -> 6;
        };
        return categoryBase + tierBonus;
    }
}
