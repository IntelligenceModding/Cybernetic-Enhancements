package de.artemis.cyberneticenhancements.common.menu;

import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.ChipwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import de.artemis.cyberneticenhancements.common.item.ChipwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberConsumableItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareModuleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public final class StationSlotTierHelper {
    private StationSlotTierHelper() {
    }

    public static CyberwareTier getItemTier(ItemStack stack) {
        if (stack.getItem() instanceof CyberwareItem cyberwareItem) {
            return cyberwareItem.getTier();
        }
        if (stack.getItem() instanceof ChipwareItem chipwareItem) {
            ChipwareDefinition definition = chipwareItem.getDefinition();
            return definition.tier();
        }
        if (stack.getItem() instanceof CyberwareModuleItem moduleItem) {
            CyberwareModuleDefinition definition = moduleItem.getDefinition();
            return definition.tier();
        }
        if (stack.getItem() instanceof CyberConsumableItem consumableItem) {
            CyberConsumableDefinition definition = consumableItem.getDefinition();
            return getConsumableTier(definition.rarity());
        }
        return CyberwareTier.TIER_1;
    }

    public static boolean canAccept(ItemStack stack, CyberwareTier supportedTier) {
        return stack.isEmpty() || getItemTier(stack).ordinal() <= supportedTier.ordinal();
    }

    private static CyberwareTier getConsumableTier(Rarity rarity) {
        return switch (rarity) {
            case COMMON -> CyberwareTier.TIER_1;
            case UNCOMMON -> CyberwareTier.TIER_2;
            case RARE -> CyberwareTier.TIER_3;
            case EPIC -> CyberwareTier.TIER_4;
        };
    }
}
