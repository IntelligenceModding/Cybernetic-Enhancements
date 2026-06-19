package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.item.ItemStack;

public record CyberwareServicePlan(
        Type type,
        ItemStack output,
        ItemStack primaryMaterial,
        int primaryCount,
        ItemStack secondaryMaterial,
        int secondaryCount,
        ItemStack tertiaryMaterial,
        int tertiaryCount
) {
    private static final int MAX_MATERIAL_COUNT = 64;

    public CyberwareServicePlan {
        primaryCount = normalizeCount(primaryMaterial, primaryCount);
        secondaryCount = normalizeCount(secondaryMaterial, secondaryCount);
        tertiaryCount = normalizeCount(tertiaryMaterial, tertiaryCount);
    }

    public static CyberwareServicePlan empty() {
        return new CyberwareServicePlan(Type.NONE, ItemStack.EMPTY, ItemStack.EMPTY, 0, ItemStack.EMPTY, 0, ItemStack.EMPTY, 0);
    }

    public boolean isAvailable() {
        return type != Type.NONE && !output.isEmpty();
    }

    public boolean requiresSecondaryMaterial() {
        return secondaryCount > 0 && !secondaryMaterial.isEmpty();
    }

    public boolean requiresTertiaryMaterial() {
        return tertiaryCount > 0 && !tertiaryMaterial.isEmpty();
    }

    private static int normalizeCount(ItemStack stack, int count) {
        if (count <= 0 || stack.isEmpty()) {
            return 0;
        }
        return Math.min(Math.min(MAX_MATERIAL_COUNT, stack.getMaxStackSize()), count);
    }

    public enum Type {
        NONE,
        REPAIR,
        UPGRADE
    }
}
