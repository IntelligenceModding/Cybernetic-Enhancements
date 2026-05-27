package de.artemis.cyberneticenhancements.common.consumable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public record CyberConsumableDefinition(
        String id,
        String displayName,
        String description,
        Item motifItem,
        Rarity rarity,
        CyberConsumableProfile profile,
        CyberConsumableCategory category,
        int cooldownTicks,
        int overdosePoints
) {
    public String descriptionKey() {
        return "item.cyberneticenhancements." + id + ".desc";
    }
}
