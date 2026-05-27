package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ChipwareCatalog {
    private static final List<ChipwareDefinition> DEFINITIONS = List.of(
            def("miner_skillchip", "Miner Skillchip", CyberwareTier.TIER_1, "Focused excavation routines for faster mining work.", Items.IRON_PICKAXE, 1, CyberwareEffect.breakSpeed(0.15D)),
            def("builder_skillchip", "Builder Skillchip", CyberwareTier.TIER_1, "Construction support routines for cleaner placement reach.", Items.BRICKS, 1, CyberwareEffect.blockReach(0.5D)),
            def("scout_skillchip", "Scout Skillchip", CyberwareTier.TIER_2, "Recon routines for light movement and awareness boosts.", Items.SPYGLASS, 2, CyberwareEffect.moveSpeed(0.08D), CyberwareEffect.entityReach(0.5D)),
            def("combat_skillchip", "Combat Skillchip", CyberwareTier.TIER_2, "Aggression stack for close-quarters combat response.", Items.IRON_SWORD, 2, CyberwareEffect.damage(1.0D), CyberwareEffect.attackSpeed(0.10D)),
            def("runner_skillchip", "Runner Skillchip", CyberwareTier.TIER_2, "Sprint-biased locomotion tuning package.", Items.SUGAR, 2, CyberwareEffect.moveSpeed(0.10D), CyberwareEffect.safeFall(2.0D)),
            def("trader_skillchip", "Trader Skillchip", CyberwareTier.TIER_3, "Social and handling routines repurposed for barter utility.", Items.EMERALD, 2, CyberwareEffect.entityReach(0.5D), CyberwareEffect.blockReach(0.5D))
    );

    private static final Map<String, ChipwareDefinition> DEFINITIONS_BY_ID = createDefinitionMap();

    private ChipwareCatalog() {
    }

    public static List<ChipwareDefinition> definitions() {
        return DEFINITIONS;
    }

    public static ChipwareDefinition get(String id) {
        ChipwareDefinition definition = DEFINITIONS_BY_ID.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown chipware id: " + id);
        }
        return definition;
    }

    private static Map<String, ChipwareDefinition> createDefinitionMap() {
        Map<String, ChipwareDefinition> definitions = new LinkedHashMap<>();
        for (ChipwareDefinition definition : DEFINITIONS) {
            definitions.put(definition.id(), definition);
        }
        return definitions;
    }

    private static ChipwareDefinition def(
            String id,
            String displayName,
            CyberwareTier tier,
            String description,
            net.minecraft.world.level.ItemLike motifItem,
            int chromeCost,
            CyberwareEffect... effects
    ) {
        return new ChipwareDefinition(id, displayName, tier, description, motifItem, chromeCost, List.of(effects));
    }
}
