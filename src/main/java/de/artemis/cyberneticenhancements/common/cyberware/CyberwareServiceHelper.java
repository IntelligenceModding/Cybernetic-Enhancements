package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public final class CyberwareServiceHelper {
    private static final int MAX_UPGRADE_MATERIAL_COUNT = 64;

    public record MaterialRequirement(ItemStack stack, int count) {
        public boolean isRequired() {
            return count > 0 && !stack.isEmpty();
        }
    }

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
        int secondaryCount = Math.max(1, (int) Math.ceil(missingRatio * tierFactor));
        ItemStack output = CyberwareConditionHelper.createServiceCopy(inputStack, definition, true);
        return new CyberwareServicePlan(
                CyberwareServicePlan.Type.REPAIR,
                output,
                componentStack(definition.tier()),
                primaryCount,
                new ItemStack(slotSupportItem(definition.slotType())),
                secondaryCount,
                new ItemStack(definition.motifItem()),
                1
        );
    }

    public static CyberwareServicePlan getUpgradePlan(ItemStack inputStack) {
        if (!(inputStack.getItem() instanceof CyberwareItem cyberwareItem)) {
            return CyberwareServicePlan.empty();
        }

        CyberwareDefinition sourceDefinition = cyberwareItem.getDefinition();
        if (!CyberwareUpgradeHelper.canUpgrade(inputStack)) {
            return CyberwareServicePlan.empty();
        }

        int currentLevel = CyberwareUpgradeHelper.getUpgradeLevel(inputStack);
        int materialCount = Math.min(MAX_UPGRADE_MATERIAL_COUNT, 4 << currentLevel);
        ItemStack output = CyberwareUpgradeHelper.createUpgradedCopy(inputStack, sourceDefinition);
        return new CyberwareServicePlan(
                CyberwareServicePlan.Type.UPGRADE,
                output,
                componentStack(sourceDefinition.tier()),
                materialCount,
                new ItemStack(slotSupportItem(sourceDefinition.slotType())),
                materialCount,
                ItemStack.EMPTY,
                0
        );
    }

    public static CyberwareServicePlan resolve(ItemStack inputStack, ItemStack... materials) {
        CyberwareServicePlan upgradePlan = getUpgradePlan(inputStack);
        if (matchesMaterials(upgradePlan, materials)) {
            return upgradePlan;
        }

        CyberwareServicePlan repairPlan = getRepairPlan(inputStack);
        if (matchesMaterials(repairPlan, materials)) {
            return repairPlan;
        }
        return CyberwareServicePlan.empty();
    }

    public static boolean matchesMaterials(CyberwareServicePlan plan, ItemStack... providedMaterials) {
        if (!plan.isAvailable()) {
            return false;
        }

        List<MaterialRequirement> requirements = getRequiredMaterials(plan);
        if (requirements.isEmpty()) {
            return true;
        }
        return matchesRequirements(requirements, providedMaterials, new boolean[providedMaterials.length], 0);
    }

    public static List<MaterialRequirement> getRequiredMaterials(CyberwareServicePlan plan) {
        List<MaterialRequirement> requirements = new ArrayList<>(3);
        addRequirement(requirements, plan.primaryMaterial(), plan.primaryCount());
        addRequirement(requirements, plan.secondaryMaterial(), plan.secondaryCount());
        addRequirement(requirements, plan.tertiaryMaterial(), plan.tertiaryCount());
        return requirements;
    }

    private static void addRequirement(List<MaterialRequirement> requirements, ItemStack stack, int count) {
        if (count > 0 && !stack.isEmpty()) {
            requirements.add(new MaterialRequirement(stack, count));
        }
    }

    private static boolean matchesRequirements(List<MaterialRequirement> requirements, ItemStack[] providedMaterials, boolean[] usedSlots, int requirementIndex) {
        if (requirementIndex >= requirements.size()) {
            return true;
        }

        MaterialRequirement requirement = requirements.get(requirementIndex);
        for (int slotIndex = 0; slotIndex < providedMaterials.length; slotIndex++) {
            if (usedSlots[slotIndex] || !matchesRequirement(providedMaterials[slotIndex], requirement)) {
                continue;
            }
            usedSlots[slotIndex] = true;
            if (matchesRequirements(requirements, providedMaterials, usedSlots, requirementIndex + 1)) {
                return true;
            }
            usedSlots[slotIndex] = false;
        }
        return false;
    }

    private static boolean matchesRequirement(ItemStack candidate, MaterialRequirement requirement) {
        return !candidate.isEmpty()
                && ItemStack.isSameItemSameComponents(candidate.copyWithCount(1), requirement.stack().copyWithCount(1))
                && candidate.getCount() >= requirement.count();
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
