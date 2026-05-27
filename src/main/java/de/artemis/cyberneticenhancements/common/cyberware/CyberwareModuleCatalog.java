package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CyberwareModuleCatalog {
    private static final List<CyberwareModuleDefinition> DEFINITIONS = List.of(
            def("mining_accelerator", "Mining Accelerator", CyberwareModuleCategory.ARMS, CyberwareTier.TIER_1, "Adds industrial torque to armware.", Items.IRON_PICKAXE, CyberwareEffect.breakSpeed(0.15D)),
            def("reach_extender", "Reach Extender", CyberwareModuleCategory.ARMS, CyberwareTier.TIER_2, "Servo extension package for building and interaction.", Items.STICK, CyberwareEffect.blockReach(1.0D), CyberwareEffect.entityReach(0.5D)),
            def("combat_actuator", "Combat Actuator", CyberwareModuleCategory.ARMS, CyberwareTier.TIER_2, "Aggressive strike servo package.", Items.IRON_SWORD, CyberwareEffect.damage(1.0D), CyberwareEffect.attackSpeed(0.10D)),
            def("builder_grip", "Builder Grip", CyberwareModuleCategory.ARMS, CyberwareTier.TIER_1, "Fine control stabilizer for repetitive placement work.", Items.BRICK, CyberwareEffect.blockReach(0.5D)),
            def("shock_palm", "Shock Palm", CyberwareModuleCategory.ARMS, CyberwareTier.TIER_3, "Palm discharge module for heavier impacts.", Items.LIGHTNING_ROD, CyberwareEffect.damage(2.0D)),

            def("dash_piston", "Dash Piston", CyberwareModuleCategory.LEGS, CyberwareTier.TIER_2, "Impulse piston package for sudden movement bursts.", Items.PISTON, CyberwareEffect.moveSpeed(0.10D)),
            def("fall_damper", "Fall Damper", CyberwareModuleCategory.LEGS, CyberwareTier.TIER_1, "Impact-canceling buffer layer.", Items.HAY_BLOCK, CyberwareEffect.safeFall(4.0D), CyberwareEffect.fallReduction(-0.10D)),
            def("sprint_motor", "Sprint Motor", CyberwareModuleCategory.LEGS, CyberwareTier.TIER_2, "Efficiency-biased sprint assist assembly.", Items.SUGAR, CyberwareEffect.moveSpeed(0.12D)),
            def("climbing_servo", "Climbing Servo", CyberwareModuleCategory.LEGS, CyberwareTier.TIER_2, "Vertical traversal support for ladder and scaffold work.", Items.LADDER, CyberwareEffect.stepHeight(0.5D)),
            def("stealth_foot", "Stealth Foot", CyberwareModuleCategory.LEGS, CyberwareTier.TIER_3, "Low-noise gait module tuned for infiltration.", Items.RABBIT_HIDE, CyberwareEffect.moveSpeed(0.05D), CyberwareEffect.safeFall(2.0D))
    );

    private static final Map<String, CyberwareModuleDefinition> DEFINITIONS_BY_ID = createDefinitionMap();

    private CyberwareModuleCatalog() {
    }

    public static List<CyberwareModuleDefinition> definitions() {
        return DEFINITIONS;
    }

    public static CyberwareModuleDefinition get(String id) {
        CyberwareModuleDefinition definition = DEFINITIONS_BY_ID.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown cyberware module id: " + id);
        }
        return definition;
    }

    private static Map<String, CyberwareModuleDefinition> createDefinitionMap() {
        Map<String, CyberwareModuleDefinition> definitions = new LinkedHashMap<>();
        for (CyberwareModuleDefinition definition : DEFINITIONS) {
            definitions.put(definition.id(), definition);
        }
        return definitions;
    }

    private static CyberwareModuleDefinition def(
            String id,
            String displayName,
            CyberwareModuleCategory category,
            CyberwareTier tier,
            String description,
            net.minecraft.world.level.ItemLike motifItem,
            CyberwareEffect... effects
    ) {
        return new CyberwareModuleDefinition(id, displayName, category, tier, description, motifItem, List.of(effects));
    }
}
