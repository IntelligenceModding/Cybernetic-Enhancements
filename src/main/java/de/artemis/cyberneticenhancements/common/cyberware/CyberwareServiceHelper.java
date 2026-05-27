package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class CyberwareServiceHelper {
    private CyberwareServiceHelper() {
    }

    public static CyberwareServicePlan getRepairPlan(ItemStack inputStack) {
        if (!(inputStack.getItem() instanceof CyberwareItem cyberwareItem)) {
            return CyberwareServicePlan.empty();
        }

        CyberwareDefinition definition = cyberwareItem.getDefinition();
        if (!CyberwareConditionHelper.isDamaged(inputStack, definition)) {
            return CyberwareServicePlan.empty();
        }

        double missingRatio = 1.0D - CyberwareConditionHelper.getIntegrityRatio(inputStack, definition);
        int tierFactor = definition.tier().ordinal() + 1;
        int primaryCount = Math.max(1, (int) Math.ceil(missingRatio * tierFactor * 2.0D));
        ItemStack output = CyberwareConditionHelper.createServiceCopy(inputStack, definition, true);
        return new CyberwareServicePlan(
                CyberwareServicePlan.Type.REPAIR,
                output,
                componentStack(definition.tier()),
                primaryCount,
                ItemStack.EMPTY,
                0
        );
    }

    public static CyberwareServicePlan getUpgradePlan(ItemStack inputStack) {
        if (!(inputStack.getItem() instanceof CyberwareItem cyberwareItem)) {
            return CyberwareServicePlan.empty();
        }

        CyberwareDefinition sourceDefinition = cyberwareItem.getDefinition();
        CyberwareDefinition targetDefinition = CyberwareCatalog.findUpgradeStep(sourceDefinition);
        if (targetDefinition == null) {
            return CyberwareServicePlan.empty();
        }

        int targetTierFactor = targetDefinition.tier().ordinal() + 1;
        int primaryCount = Math.max(2, targetTierFactor);
        int secondaryCount = Math.max(1, sourceDefinition.tier().ordinal() + 1);
        ItemStack output = CyberwareConditionHelper.createServiceCopy(inputStack, targetDefinition, true);
        return new CyberwareServicePlan(
                CyberwareServicePlan.Type.UPGRADE,
                output,
                componentStack(targetDefinition.tier()),
                primaryCount,
                new ItemStack(slotSupportItem(sourceDefinition.slotType())),
                secondaryCount
        );
    }

    public static CyberwareServicePlan resolve(ItemStack inputStack, ItemStack firstMaterial, ItemStack secondMaterial) {
        CyberwareServicePlan upgradePlan = getUpgradePlan(inputStack);
        if (matchesMaterials(upgradePlan, firstMaterial, secondMaterial)) {
            return upgradePlan;
        }

        CyberwareServicePlan repairPlan = getRepairPlan(inputStack);
        if (matchesMaterials(repairPlan, firstMaterial, secondMaterial)) {
            return repairPlan;
        }
        return CyberwareServicePlan.empty();
    }

    public static boolean matchesMaterials(CyberwareServicePlan plan, ItemStack firstMaterial, ItemStack secondMaterial) {
        if (!plan.isAvailable()) {
            return false;
        }

        return matchesOrdered(plan, firstMaterial, secondMaterial) || matchesOrdered(plan, secondMaterial, firstMaterial);
    }

    private static boolean matchesOrdered(CyberwareServicePlan plan, ItemStack primarySlot, ItemStack secondarySlot) {
        boolean primaryMatches = ItemStack.isSameItemSameComponents(primarySlot.copyWithCount(1), plan.primaryMaterial().copyWithCount(1))
                && primarySlot.getCount() >= plan.primaryCount();
        if (!primaryMatches) {
            return false;
        }
        if (!plan.requiresSecondaryMaterial()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(secondarySlot.copyWithCount(1), plan.secondaryMaterial().copyWithCount(1))
                && secondarySlot.getCount() >= plan.secondaryCount();
    }

    public static ItemStack componentStack(CyberwareTier tier) {
        return new ItemStack(componentItem(tier));
    }

    public static ItemLike componentItem(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1 -> ModItems.COMMON_ITEM_COMPONENTS.get();
            case TIER_2 -> ModItems.UNCOMMON_ITEM_COMPONENTS.get();
            case TIER_3 -> ModItems.RARE_ITEM_COMPONENTS.get();
            case TIER_4 -> ModItems.EPIC_ITEM_COMPONENTS.get();
            case TIER_5 -> ModItems.LEGENDARY_ITEM_COMPONENTS.get();
        };
    }

    public static ItemLike slotSupportItem(CyberwareSlotType slotType) {
        return switch (slotType) {
            case FRONTAL_CORTEX, OPERATING_SYSTEM, NERVOUS_SYSTEM -> ModItems.BASIC_CIRCUIT_PLATE.get();
            case FACE -> ModItems.SENSOR_LENS.get();
            case ARMS, LEGS, SKELETON -> ModItems.REPLACEMENT_JOINT.get();
            case HANDS -> ModItems.COPPER_WIRING.get();
            case CIRCULATORY_SYSTEM, INTEGUMENTARY_SYSTEM -> ModItems.CONDUCTIVE_PASTE.get();
        };
    }
}
