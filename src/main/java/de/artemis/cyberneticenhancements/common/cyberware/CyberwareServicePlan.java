package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.item.ItemStack;

public record CyberwareServicePlan(
        Type type,
        ItemStack output,
        ItemStack primaryMaterial,
        int primaryCount,
        ItemStack secondaryMaterial,
        int secondaryCount
) {
    public static CyberwareServicePlan empty() {
        return new CyberwareServicePlan(Type.NONE, ItemStack.EMPTY, ItemStack.EMPTY, 0, ItemStack.EMPTY, 0);
    }

    public boolean isAvailable() {
        return type != Type.NONE && !output.isEmpty();
    }

    public boolean requiresSecondaryMaterial() {
        return secondaryCount > 0 && !secondaryMaterial.isEmpty();
    }

    public enum Type {
        NONE,
        REPAIR,
        UPGRADE
    }
}
