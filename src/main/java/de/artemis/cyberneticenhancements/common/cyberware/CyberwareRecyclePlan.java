package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.item.ItemStack;

public record CyberwareRecyclePlan(ItemStack output) {
    public static CyberwareRecyclePlan empty() {
        return new CyberwareRecyclePlan(ItemStack.EMPTY);
    }

    public boolean isAvailable() {
        return !output.isEmpty();
    }
}
