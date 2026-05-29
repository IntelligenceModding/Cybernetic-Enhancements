package de.artemis.cyberneticenhancements.common.datagen;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableCatalog;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.ChipwareCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.ChipwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffectType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlotType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import de.artemis.cyberneticenhancements.common.registry.ModItems;
import net.minecraft.data.PackOutput;

public final class ModLanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, CyberneticEnhancements.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.cyberneticenhancements", "Cybernetic Enhancements");
        add("itemGroup.cyberneticenhancements.cyberware", "Cyberware");
        add("key.categories.cyberneticenhancements", "Cybernetic Enhancements");
        add("key.cyberneticenhancements.activate_cyberware", "Activate Cyberware");

        add("tooltip.cyberneticenhancements.cyberware_slot", "Slot: %s");
        add("tooltip.cyberneticenhancements.cyberware_tier", "Tier: %s");
        add("tooltip.cyberneticenhancements.chrome_cost", "Chrome Cost: %s");
        add("tooltip.cyberneticenhancements.integrity", "Integrity: %s / %s");
        add("tooltip.cyberneticenhancements.chip_slots", "Chip Sockets: %s");
        add("tooltip.cyberneticenhancements.module_slots", "Module Bays: %s");
        add("tooltip.cyberneticenhancements.capacity_bonus", "Chrome Capacity: +%s");
        add("tooltip.cyberneticenhancements.placeholder_effect", "Special functionality not implemented yet.");
        add("tooltip.cyberneticenhancements.chipware", "Chip Load: %s");
        add("tooltip.cyberneticenhancements.installed_chipware", "Installed Chipware: %s");
        add("tooltip.cyberneticenhancements.installed_modules", "Installed Modules: %s");
        add("tooltip.cyberneticenhancements.module_category", "Module Bay: %s");
        add("tooltip.cyberneticenhancements.consumable_category", "Category: %s");
        add("tooltip.cyberneticenhancements.consumable_cooldown", "Cooldown: %s s");
        add("tooltip.cyberneticenhancements.consumable_overdose", "Overdose Load: %s");

        addMessageTranslations();
        addEffectTranslations();
        addSlotTranslations();
        addTierTranslations();
        addScreenTranslations();
        addMaterialTranslations();
        addConsumableTranslations();
        addChipwareTranslations();
        addModuleTranslations();
        addCyberwareTranslations();
        addBlockTranslations();
    }

    private void addEffectTranslations() {
        add(CyberwareEffectType.MAX_HEALTH.translationKey(), "+%s Hearts");
        add(CyberwareEffectType.ARMOR.translationKey(), "+%s Armor");
        add(CyberwareEffectType.ATTACK_DAMAGE.translationKey(), "+%s Attack Damage");
        add(CyberwareEffectType.ATTACK_SPEED.translationKey(), "+%s Attack Speed");
        add(CyberwareEffectType.MOVEMENT_SPEED.translationKey(), "+%s%% Movement Speed");
        add(CyberwareEffectType.BLOCK_BREAK_SPEED.translationKey(), "+%s%% Mining Speed");
        add(CyberwareEffectType.BLOCK_REACH.translationKey(), "+%s Block Reach");
        add(CyberwareEffectType.ENTITY_REACH.translationKey(), "+%s Entity Reach");
        add(CyberwareEffectType.STEP_HEIGHT.translationKey(), "+%s Step Height");
        add(CyberwareEffectType.SAFE_FALL_DISTANCE.translationKey(), "+%s Safe Fall Distance");
        add(CyberwareEffectType.FALL_DAMAGE_REDUCTION.translationKey(), "%s%% Fall Damage");
        add(CyberwareEffectType.KNOCKBACK_RESISTANCE.translationKey(), "+%s%% Knockback Resistance");
        add(CyberwareEffectType.CHROME_CAPACITY.translationKey(), "+%s Chrome Headroom");
        add(CyberwareEffectType.HEALTH_REGEN.translationKey(), "+%s Hearts/sec");
        add(CyberwareEffectType.DAMAGE_REDUCTION.translationKey(), "+%s%% Damage Reduction");
        add(CyberwareEffectType.BONUS_ABSORPTION.translationKey(), "+%s Absorption Hearts");
        add(CyberwareEffectType.JUMP_POWER.translationKey(), "+%s%% Jump Power");
        add(CyberwareEffectType.NIGHT_VISION.translationKey(), "Night Vision");
        add(CyberwareEffectType.FIRE_RESISTANCE.translationKey(), "Fire Resistance");
        add(CyberwareEffectType.WATER_BREATHING.translationKey(), "Water Breathing");
    }

    private void addMessageTranslations() {
        add("message.cyberneticenhancements.ability.no_operating_system", "No operating system installed.");
        add("message.cyberneticenhancements.ability.no_active_ability", "Installed operating system has no active ability.");
        add("message.cyberneticenhancements.ability.cooldown", "Cyberware cooling down: %s s");
        add("message.cyberneticenhancements.ability.sandevistan", "Sandevistan engaged for %s seconds.");
        add("message.cyberneticenhancements.ability.berserk", "Berserk engaged for %s seconds.");
        add("message.cyberneticenhancements.trigger.biomonitor", "Biomonitor injected an emergency recovery cycle.");
        add("message.cyberneticenhancements.trigger.blood_pump", "Blood Pump forced emergency circulation.");
        add("message.cyberneticenhancements.trigger.second_heart", "Second Heart restarted your core systems.");
        add("message.cyberneticenhancements.trigger.reflex_tuner", "Reflex Tuner kicked into emergency overdrive.");
        add("message.cyberneticenhancements.consumable.immunoblockers", "Immunoblockers forced your chrome pressure down, but the dose hits hard.");
        add("message.cyberneticenhancements.consumable.chrome_suppressant", "Chrome Suppressant steadied your overloaded systems.");
        add("message.cyberneticenhancements.consumable.ram_jolt", "RAM Jolt tightened your neural recovery cycle.");
        add("message.cyberneticenhancements.consumable.cooldown", "%s is still on cooldown.");
        add("message.cyberneticenhancements.consumable.used", "%s applied.");
        add("message.cyberneticenhancements.consumable.overdose_minor", "Your system is rejecting the chemical stack.");
        add("message.cyberneticenhancements.consumable.overdose_major", "Overdose spike. Back off the stims.");
        add("message.cyberneticenhancements.psychosis.triggered", "Cyberpsychosis episode triggered. You are losing control.");
        add("message.cyberneticenhancements.psychosis.forced", "Forced cyberpsychosis test episode started.");
        add("message.cyberneticenhancements.psychosis.ended", "You regained control of your body.");
    }

    private void addSlotTranslations() {
        add(CyberwareSlotType.FRONTAL_CORTEX.translationKey(), "Frontal Cortex");
        add(CyberwareSlotType.OPERATING_SYSTEM.translationKey(), "Operating System");
        add(CyberwareSlotType.ARMS.translationKey(), "Arms");
        add(CyberwareSlotType.FACE.translationKey(), "Face");
        add(CyberwareSlotType.SKELETON.translationKey(), "Skeleton");
        add(CyberwareSlotType.HANDS.translationKey(), "Hands");
        add(CyberwareSlotType.NERVOUS_SYSTEM.translationKey(), "Nervous System");
        add(CyberwareSlotType.CIRCULATORY_SYSTEM.translationKey(), "Circulatory System");
        add(CyberwareSlotType.INTEGUMENTARY_SYSTEM.translationKey(), "Integumentary System");
        add(CyberwareSlotType.LEGS.translationKey(), "Legs");

        for (CyberwareSlotType type : CyberwareSlotType.values()) {
            for (int slotNumber = 1; slotNumber <= type.getBaseSlotCount(); slotNumber++) {
                add(type.translationKey() + "." + slotNumber, humanizeSlot(type) + " Slot " + slotNumber);
            }
        }
    }

    private void addTierTranslations() {
        add(CyberwareTier.TIER_1.translationKey(), "Tier 1");
        add(CyberwareTier.TIER_2.translationKey(), "Tier 2");
        add(CyberwareTier.TIER_3.translationKey(), "Tier 3");
        add(CyberwareTier.TIER_4.translationKey(), "Tier 4");
        add(CyberwareTier.TIER_5.translationKey(), "Tier 5");
    }

    private void addScreenTranslations() {
        add("screen.cyberneticenhancements.ripper_station.body_overview", "Body Overview");
        add("screen.cyberneticenhancements.ripper_station.ripper_matrix", "Ripper Matrix");
        add("screen.cyberneticenhancements.ripper_station.system_summary", "System Summary");
        add("screen.cyberneticenhancements.ripper_station.stage", "Stage");
        add("screen.cyberneticenhancements.ripper_station.stage.organic", "Organic");
        add("screen.cyberneticenhancements.ripper_station.stage.augmented", "Augmented");
        add("screen.cyberneticenhancements.ripper_station.stage.cybernetic", "Cybernetic");
        add("screen.cyberneticenhancements.ripper_station.stage.synthetic", "Synthetic");
        add("screen.cyberneticenhancements.ripper_station.stage.android", "Full Android");
        add("screen.cyberneticenhancements.ripper_station.chrome", "Chrome");
        add("screen.cyberneticenhancements.ripper_station.cyberstrain", "Cyberstrain");
        add("screen.cyberneticenhancements.ripper_station.integrity", "Integrity");
        add("screen.cyberneticenhancements.ripper_station.cyberware_slots", "Cyberware");
        add("screen.cyberneticenhancements.ripper_station.installed_parts", "Installed");
        add("screen.cyberneticenhancements.ripper_station.chrome_profile", "Chrome Profile");
        add("screen.cyberneticenhancements.ripper_station.empty", "Empty");
        add("screen.cyberneticenhancements.ripper_station.slot_hint", "Install matching cyberware here.");
        add("screen.cyberneticenhancements.ripper_station.slot_supported_tier", "Supported Tier: %s");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_cost", "Upgrade Cost: %s");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_hold", "Hold LMB for 5s to unlock %s.");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_missing", "Missing: %s");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_progress", "Upgrade Progress: %s");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_maxed", "Slot already supports Tier 5.");
        add("screen.cyberneticenhancements.ripper_station.chipware", "Chipware");
        add("screen.cyberneticenhancements.ripper_station.chip_slot_hint", "Install a compatible skillchip here.");
        add("screen.cyberneticenhancements.ripper_station.chip_slot_locked", "Install a chipware socket to unlock this bay.");
        add("screen.cyberneticenhancements.ripper_station.no_chip_socket", "No socket installed");
        add("screen.cyberneticenhancements.ripper_station.arm_modules", "Arm Modules");
        add("screen.cyberneticenhancements.ripper_station.leg_modules", "Leg Modules");
        add("screen.cyberneticenhancements.ripper_station.module_slot_hint", "Install a compatible limb module here.");
        add("screen.cyberneticenhancements.ripper_station.module_slot_locked", "Higher-tier limb cyberware unlocks this module bay.");
        add("screen.cyberneticenhancements.ripper_station.no_arm_cyberware", "No arm cyberware installed");
        add("screen.cyberneticenhancements.ripper_station.no_leg_cyberware", "No leg cyberware installed");
        add("screen.cyberneticenhancements.techstation.service_bay", "Techstation Service Bay");
        add("screen.cyberneticenhancements.techstation.operation", "Operation");
        add("screen.cyberneticenhancements.techstation.input", "Input");
        add("screen.cyberneticenhancements.techstation.materials", "Materials");
        add("screen.cyberneticenhancements.techstation.output", "Output");
        add("screen.cyberneticenhancements.techstation.repair", "Repair");
        add("screen.cyberneticenhancements.techstation.upgrade", "Upgrade");
        add("screen.cyberneticenhancements.techstation.unavailable", "Unavailable");
        add("screen.cyberneticenhancements.techstation.no_valid_operation", "Load the right materials to service this implant.");
        add("screen.cyberneticenhancements.techstation.ready_repair", "Repair package ready.");
        add("screen.cyberneticenhancements.techstation.ready_upgrade", "Upgrade package ready.");
        add("screen.cyberneticenhancements.recycler_station.recycler_bay", "Recycler Station");
        add("screen.cyberneticenhancements.recycler_station.scrap_input", "Scrap Input");
        add("screen.cyberneticenhancements.recycler_station.reclaimed_output", "Recovered Output");
        add("screen.cyberneticenhancements.recycler_station.no_recycle_output", "Insert cyberware, chips, modules, or consumables to recycle them.");
        add("screen.cyberneticenhancements.recycler_station.recovery", "Recovery Yield");
    }

    private void addMaterialTranslations() {
        addItem(ModItems.COPPER_WIRING, "Copper Wiring");
        addItem(ModItems.CONDUCTIVE_PASTE, "Conductive Paste");
        addItem(ModItems.SERVO_SCREWS, "Servo Screws");
        addItem(ModItems.SENSOR_LENS, "Sensor Lens");
        addItem(ModItems.MICRO_BATTERY, "Micro Battery");
        addItem(ModItems.REPLACEMENT_JOINT, "Replacement Joint");
        addItem(ModItems.BASIC_CIRCUIT_PLATE, "Basic Circuit Plate");
        addItem(ModItems.COMMON_ITEM_COMPONENTS, "Common Item Components");
        addItem(ModItems.UNCOMMON_ITEM_COMPONENTS, "Uncommon Item Components");
        addItem(ModItems.RARE_ITEM_COMPONENTS, "Rare Item Components");
        addItem(ModItems.EPIC_ITEM_COMPONENTS, "Epic Item Components");
        addItem(ModItems.LEGENDARY_ITEM_COMPONENTS, "Legendary Item Components");
        addItem(ModItems.WHOS_READY_FOR_TOMORROW_MUSIC_DISC, "Music Disc");
        add("jukebox_song.cyberneticenhancements.whos_ready_for_tomorrow_instrumental", "RAT BOY - Who's Ready for Tomorrow (Instrumental)");
    }

    private void addConsumableTranslations() {
        add("tooltip.cyberneticenhancements.consumable_category.medical", "Medical");
        add("tooltip.cyberneticenhancements.consumable_category.booster", "Booster");
        add("tooltip.cyberneticenhancements.consumable_category.neural", "Neural");
        add("tooltip.cyberneticenhancements.consumable_category.suppressant", "Suppressant");
        add("tooltip.cyberneticenhancements.consumable_category.street", "Street");

        for (CyberConsumableDefinition definition : CyberConsumableCatalog.definitions()) {
            addItem(ModItems.consumable(definition.id()), definition.displayName());
            add(definition.descriptionKey(), definition.description());
        }
    }

    private void addChipwareTranslations() {
        for (ChipwareDefinition definition : ChipwareCatalog.definitions()) {
            addItem(ModItems.chipware(definition.id()), definition.displayName());
            add(definition.descriptionKey(), definition.description());
        }
    }

    private void addModuleTranslations() {
        add("tooltip.cyberneticenhancements.module_category.arms", "Arms");
        add("tooltip.cyberneticenhancements.module_category.legs", "Legs");

        for (CyberwareModuleDefinition definition : CyberwareModuleCatalog.definitions()) {
            addItem(ModItems.module(definition.id()), definition.displayName());
            add(definition.descriptionKey(), definition.description());
        }
    }

    private void addCyberwareTranslations() {
        for (CyberwareDefinition definition : CyberwareCatalog.definitions()) {
            addItem(ModItems.cyberware(definition.id()), definition.displayName());
            add(definition.descriptionKey(), definition.description());
        }
    }

    private void addBlockTranslations() {
        addBlock(ModBlocks.RIPPER_STATION, "Ripper Station");
        addBlock(ModBlocks.TECHSTATION, "Techstation");
        addBlock(ModBlocks.RECYCLER_STATION, "Recycler Station");
    }

    private static String humanizeSlot(CyberwareSlotType type) {
        return switch (type) {
            case FRONTAL_CORTEX -> "Frontal Cortex";
            case OPERATING_SYSTEM -> "Operating System";
            case ARMS -> "Arms";
            case FACE -> "Face";
            case SKELETON -> "Skeleton";
            case HANDS -> "Hands";
            case NERVOUS_SYSTEM -> "Nervous System";
            case CIRCULATORY_SYSTEM -> "Circulatory System";
            case INTEGUMENTARY_SYSTEM -> "Integumentary System";
            case LEGS -> "Legs";
        };
    }
}
