package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableDefinition;
import de.artemis.cyberneticenhancements.common.item.ChipwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberConsumableItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareModuleItem;
import de.artemis.cyberneticenhancements.common.registry.ModItems;
import net.minecraft.world.item.ItemStack;

public final class CyberwareRecycleHelper {
    private CyberwareRecycleHelper() {
    }

    public static CyberwareRecyclePlan resolve(ItemStack inputStack) {
        if (inputStack.isEmpty()) {
            return CyberwareRecyclePlan.empty();
        }
        if (inputStack.getItem() instanceof CyberwareItem cyberwareItem) {
            return recycleCyberware(inputStack, cyberwareItem.getDefinition());
        }
        if (inputStack.getItem() instanceof ChipwareItem chipwareItem) {
            return new CyberwareRecyclePlan(new ItemStack(
                    CyberwareServiceHelper.componentItem(chipwareItem.getDefinition().tier()),
                    Math.max(1, chipwareItem.getDefinition().tier().ordinal())
            ));
        }
        if (inputStack.getItem() instanceof CyberwareModuleItem moduleItem) {
            return new CyberwareRecyclePlan(new ItemStack(
                    switch (moduleItem.getDefinition().category()) {
                        case ARMS, LEGS -> ModItems.REPLACEMENT_JOINT.get();
                    },
                    1 + moduleItem.getDefinition().tier().ordinal() / 2
            ));
        }
        if (inputStack.getItem() instanceof CyberConsumableItem consumableItem) {
            return recycleConsumable(consumableItem.getDefinition());
        }
        return CyberwareRecyclePlan.empty();
    }

    private static CyberwareRecyclePlan recycleCyberware(ItemStack inputStack, CyberwareDefinition definition) {
        double integrityRatio = CyberwareConditionHelper.getIntegrityRatio(inputStack, definition);
        int baseCount = 1 + definition.tier().ordinal();
        int penalty = integrityRatio < 0.45D ? 2 : integrityRatio < 0.75D ? 1 : 0;
        int outputCount = Math.max(1, baseCount - penalty);
        return new CyberwareRecyclePlan(new ItemStack(CyberwareServiceHelper.componentItem(definition.tier()), outputCount));
    }

    private static CyberwareRecyclePlan recycleConsumable(CyberConsumableDefinition definition) {
        return switch (definition.category()) {
            case MEDICAL, SUPPRESSANT -> new CyberwareRecyclePlan(new ItemStack(ModItems.CONDUCTIVE_PASTE.get(), 1));
            case NEURAL -> new CyberwareRecyclePlan(new ItemStack(ModItems.BASIC_CIRCUIT_PLATE.get(), 1));
            case BOOSTER, STREET -> new CyberwareRecyclePlan(new ItemStack(ModItems.COPPER_WIRING.get(), 1));
        };
    }
}
