package de.artemis.cyberneticenhancements.common.consumable;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareBalance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CyberConsumableCatalog {
    private static final Map<String, CyberConsumableDefinition> DEFINITIONS = createDefinitions();

    private CyberConsumableCatalog() {
    }

    public static List<CyberConsumableDefinition> definitions() {
        return List.copyOf(DEFINITIONS.values());
    }

    public static CyberConsumableDefinition get(String id) {
        CyberConsumableDefinition definition = DEFINITIONS.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown consumable id: " + id);
        }
        return definition;
    }

    private static Map<String, CyberConsumableDefinition> createDefinitions() {
        Map<String, CyberConsumableDefinition> definitions = new LinkedHashMap<>();
        add(definitions, "maxdoc_mk1", "MaxDoc Mk.1", "Instant emergency inhaler for light combat recovery.", Items.POTION, Rarity.COMMON, CyberConsumableProfile.MAXDOC_MK1, CyberConsumableCategory.MEDICAL);
        add(definitions, "maxdoc_mk2", "MaxDoc Mk.2", "Upgraded MaxDoc inhaler with stronger immediate recovery.", Items.POTION, Rarity.UNCOMMON, CyberConsumableProfile.MAXDOC_MK2, CyberConsumableCategory.MEDICAL);
        add(definitions, "maxdoc_mk3", "MaxDoc Mk.3", "High-grade ripperdoc inhaler for heavy emergency healing.", Items.POTION, Rarity.RARE, CyberConsumableProfile.MAXDOC_MK3, CyberConsumableCategory.MEDICAL);
        add(definitions, "bounce_back_mk1", "Bounce Back Mk.1", "Injector that restores health and keeps recovery rolling.", Items.HONEY_BOTTLE, Rarity.COMMON, CyberConsumableProfile.BOUNCE_BACK_MK1, CyberConsumableCategory.MEDICAL);
        add(definitions, "bounce_back_mk2", "Bounce Back Mk.2", "Improved combat injector with stronger sustained regeneration.", Items.HONEY_BOTTLE, Rarity.UNCOMMON, CyberConsumableProfile.BOUNCE_BACK_MK2, CyberConsumableCategory.MEDICAL);
        add(definitions, "bounce_back_mk3", "Bounce Back Mk.3", "Premium regen injector for operators who do not stop moving.", Items.HONEY_BOTTLE, Rarity.RARE, CyberConsumableProfile.BOUNCE_BACK_MK3, CyberConsumableCategory.MEDICAL);
        add(definitions, "health_booster", "Health Booster", "Long-lasting booster that temporarily thickens your margin for error.", Items.GOLDEN_APPLE, Rarity.UNCOMMON, CyberConsumableProfile.HEALTH_BOOSTER, CyberConsumableCategory.BOOSTER);
        add(definitions, "stamina_booster", "Stamina Booster", "Long-lasting stimulant for movement and work endurance.", Items.SUGAR, Rarity.UNCOMMON, CyberConsumableProfile.STAMINA_BOOSTER, CyberConsumableCategory.BOOSTER);
        add(definitions, "oxy_booster", "Oxy Booster", "Respiratory support shot for underwater work and flood zones.", Items.PUFFERFISH, Rarity.UNCOMMON, CyberConsumableProfile.OXY_BOOSTER, CyberConsumableCategory.BOOSTER);
        add(definitions, "capacity_booster", "Capacity Booster", "A hauling stimulant that lets the body brute-force more load.", Items.CHEST, Rarity.UNCOMMON, CyberConsumableProfile.CAPACITY_BOOSTER, CyberConsumableCategory.BOOSTER);
        add(definitions, "ram_jolt", "RAM Jolt", "A neural spike that helps your systems recover faster under load.", Items.ENDER_EYE, Rarity.UNCOMMON, CyberConsumableProfile.RAM_JOLT, CyberConsumableCategory.NEURAL);
        add(definitions, "immunoblockers", "Immunoblockers", "Hard suppression medicine that pushes cyberpsychosis pressure back for a while.", Items.FERMENTED_SPIDER_EYE, Rarity.RARE, CyberConsumableProfile.IMMUNOBLOCKERS, CyberConsumableCategory.SUPPRESSANT);
        add(definitions, "chrome_suppressant", "Chrome Suppressant", "Safer clinic-grade stabilizer for managing rising cyberstrain.", Items.GHAST_TEAR, Rarity.UNCOMMON, CyberConsumableProfile.CHROME_SUPPRESSANT, CyberConsumableCategory.SUPPRESSANT);
        add(definitions, "black_lace", "Black Lace", "Street-grade combat inhaler with brutal upside and a real health hit.", Items.BLAZE_POWDER, Rarity.RARE, CyberConsumableProfile.BLACK_LACE, CyberConsumableCategory.STREET);
        add(definitions, "asskick", "AssKick", "Black-market health booster that leaves your recovery sluggish.", Items.COOKED_BEEF, Rarity.UNCOMMON, CyberConsumableProfile.ASSKICK, CyberConsumableCategory.STREET);
        add(definitions, "jellytricity", "Jellytricity", "Speed-heavy black-market booster that trades health margin for output.", Items.LIGHT_BLUE_DYE, Rarity.UNCOMMON, CyberConsumableProfile.JELLYTRICITY, CyberConsumableCategory.STREET);
        return definitions;
    }

    private static void add(
            Map<String, CyberConsumableDefinition> definitions,
            String id,
            String displayName,
            String description,
            net.minecraft.world.item.Item motifItem,
            Rarity rarity,
            CyberConsumableProfile profile,
            CyberConsumableCategory category
    ) {
        definitions.put(id, new CyberConsumableDefinition(
                id,
                displayName,
                description,
                motifItem,
                rarity,
                profile,
                category,
                cooldownTicks(id),
                overdosePoints(id)
        ));
    }

    private static int cooldownTicks(String id) {
        return CyberwareBalance.intValue("consumable." + id + ".cooldown_seconds") * 20;
    }

    private static int overdosePoints(String id) {
        return CyberwareBalance.intValue("consumable." + id + ".overdose_points");
    }
}
