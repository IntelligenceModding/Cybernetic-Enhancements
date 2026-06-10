package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.level.ItemLike;

import java.util.List;

public record CyberwareDefinition(
        String id,
        String displayName,
        CyberwareSlotType slotType,
        CyberwareTier tier,
        String description,
        ItemLike motifItem,
        boolean handheld,
        String upgradeFromId,
        int capacityBonus,
        int chipSlotCount,
        List<CyberwareEffect> effects
) {
    public String descriptionKey() {
        return "tooltip.cyberneticenhancements." + id;
    }

    public int chromeCost() {
        int slotBase = switch (slotType) {
            case OPERATING_SYSTEM -> 12;
            case ARMS -> 10;
            case FACE, HANDS, LEGS -> 8;
            case SKELETON -> 9;
            case FRONTAL_CORTEX, NERVOUS_SYSTEM, CIRCULATORY_SYSTEM, INTEGUMENTARY_SYSTEM -> 7;
        };
        int tierBonus = switch (tier) {
            case TIER_1 -> 0;
            case TIER_2 -> 4;
            case TIER_3 -> 8;
            case TIER_4 -> 14;
            case TIER_5 -> 20;
        };
        return slotBase + tierBonus;
    }

    public boolean isPlaceholder() {
        return capacityBonus == 0
                && effects.isEmpty()
                && chipSlotCount == 0
                && !FrontalCortexManager.hasSpecialBehavior(id)
                && !CyberwareAbilities.hasSpecialBehavior(id)
                && !ArmCyberwareManager.hasSpecialBehavior(id)
                && !FaceCyberwareManager.hasSpecialBehavior(id)
                && !CirculatoryCyberwareManager.hasSpecialBehavior(id)
                && !HandsCyberwareManager.hasSpecialBehavior(id)
                && !IntegumentaryCyberwareManager.hasSpecialBehavior(id)
                && !SkeletonCyberwareManager.hasSpecialBehavior(id)
                && !NervousSystemCyberwareManager.hasSpecialBehavior(id)
                && !LegCyberwareManager.hasSpecialBehavior(id);
    }

    public boolean supportsModules() {
        return slotType == CyberwareSlotType.ARMS || slotType == CyberwareSlotType.LEGS;
    }

    public int moduleSlotCount() {
        if (!supportsModules()) {
            return 0;
        }

        return switch (tier) {
            case TIER_1 -> 1;
            case TIER_2, TIER_3 -> 2;
            case TIER_4, TIER_5 -> 3;
        };
    }

    public CyberwareModuleCategory moduleCategory() {
        return switch (slotType) {
            case ARMS -> CyberwareModuleCategory.ARMS;
            case LEGS -> CyberwareModuleCategory.LEGS;
            default -> throw new IllegalStateException("Cyberware slot type does not support modules: " + slotType);
        };
    }

    public boolean supportsChipware() {
        return chipSlotCount > 0;
    }
}
