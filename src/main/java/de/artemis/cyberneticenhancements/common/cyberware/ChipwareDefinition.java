package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.level.ItemLike;

import java.util.List;

public record ChipwareDefinition(
        String id,
        String displayName,
        CyberwareTier tier,
        String description,
        ItemLike motifItem,
        int chromeCost,
        List<CyberwareEffect> effects
) {
    public String descriptionKey() {
        return "tooltip.cyberneticenhancements.chip." + id;
    }
}
