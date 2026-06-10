package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CyberwareUpgradeHelper {
    private static final String UPGRADE_LEVEL_KEY = "CyberwareUpgradeLevel";
    private static final Map<String, UpgradeTemplate> TEMPLATES = createTemplates();

    private CyberwareUpgradeHelper() {
    }

    public static int getUpgradeLevel(ItemStack stack) {
        if (!(stack.getItem() instanceof CyberwareItem cyberwareItem)) {
            return 0;
        }

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int storedLevel = tag.contains(UPGRADE_LEVEL_KEY, CompoundTag.TAG_INT) ? tag.getInt(UPGRADE_LEVEL_KEY) : 0;
        return Math.max(0, Math.min(storedLevel, getMaxUpgrades(cyberwareItem.getDefinition())));
    }

    public static int getMaxUpgrades(ItemStack stack) {
        return stack.getItem() instanceof CyberwareItem cyberwareItem ? getMaxUpgrades(cyberwareItem.getDefinition()) : 0;
    }

    public static int getMaxUpgrades(CyberwareDefinition definition) {
        return switch (definition.tier()) {
            case TIER_1 -> 2;
            case TIER_2 -> 3;
            case TIER_3 -> 4;
            case TIER_4, TIER_5 -> 5;
        };
    }

    public static boolean canUpgrade(ItemStack stack) {
        return stack.getItem() instanceof CyberwareItem cyberwareItem
                && getUpgradeLevel(stack) < getMaxUpgrades(cyberwareItem.getDefinition());
    }

    public static ItemStack createUpgradedCopy(ItemStack sourceStack, CyberwareDefinition definition) {
        ItemStack upgraded = CyberwareConditionHelper.createServiceCopy(sourceStack, definition, true);
        setUpgradeLevel(upgraded, getUpgradeLevel(sourceStack) + 1, definition);
        return upgraded;
    }

    public static int getCapacityBonus(ItemStack stack, CyberwareDefinition definition) {
        return definition.capacityBonus() + getUpgradeState(definition, getUpgradeLevel(stack)).capacityBonus();
    }

    public static int getChipSlotCount(ItemStack stack, CyberwareDefinition definition) {
        return definition.chipSlotCount() + getUpgradeState(definition, getUpgradeLevel(stack)).chipSlotBonus();
    }

    public static int getModuleSlotCount(ItemStack stack, CyberwareDefinition definition) {
        return definition.moduleSlotCount() + getUpgradeState(definition, getUpgradeLevel(stack)).moduleSlotBonus();
    }

    public static List<CyberwareEffect> getEffects(ItemStack stack, CyberwareDefinition definition) {
        EnumMap<CyberwareEffectType, Double> totals = new EnumMap<>(CyberwareEffectType.class);
        for (CyberwareEffect effect : definition.effects()) {
            totals.merge(effect.type(), effect.amount(), Double::sum);
        }
        for (CyberwareEffect effect : getUpgradeState(definition, getUpgradeLevel(stack)).effects()) {
            totals.merge(effect.type(), effect.amount(), Double::sum);
        }

        List<CyberwareEffect> combined = new ArrayList<>();
        for (CyberwareEffectType type : CyberwareEffectType.values()) {
            if (!totals.containsKey(type)) {
                continue;
            }
            combined.add(new CyberwareEffect(type, totals.get(type)));
        }
        return combined;
    }

    public static Component getUpgradeStatusComponent(ItemStack stack, CyberwareDefinition definition) {
        return Component.translatable(
                "tooltip.cyberneticenhancements.upgrades",
                getUpgradeLevel(stack),
                getMaxUpgrades(definition)
        );
    }

    public static Component getUpgradePreviewStatusComponent(ItemStack stack, CyberwareDefinition definition) {
        int currentLevel = getUpgradeLevel(stack);
        return Component.translatable(
                "screen.cyberneticenhancements.tech_station.upgrade_progress_preview",
                currentLevel,
                getMaxUpgrades(definition),
                Math.min(getMaxUpgrades(definition), currentLevel + 1),
                getMaxUpgrades(definition)
        );
    }

    public static List<Component> getUpgradeBonusTooltipLines(ItemStack stack, CyberwareDefinition definition) {
        return describeUpgradeState(getUpgradeState(definition, getUpgradeLevel(stack)));
    }

    public static List<Component> getUpgradePreviewLines(ItemStack stack, CyberwareDefinition definition) {
        int currentLevel = getUpgradeLevel(stack);
        int maxUpgrades = getMaxUpgrades(definition);
        if (currentLevel >= maxUpgrades) {
            return List.of();
        }

        UpgradeState current = getUpgradeState(definition, currentLevel);
        UpgradeState next = getUpgradeState(definition, currentLevel + 1);
        return describeUpgradeState(diff(next, current));
    }

    private static void setUpgradeLevel(ItemStack stack, int level, CyberwareDefinition definition) {
        int clampedLevel = Math.max(0, Math.min(level, getMaxUpgrades(definition)));
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (clampedLevel <= 0) {
            tag.remove(UPGRADE_LEVEL_KEY);
        } else {
            tag.putInt(UPGRADE_LEVEL_KEY, clampedLevel);
        }

        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    private static UpgradeState getUpgradeState(CyberwareDefinition definition, int level) {
        int maxUpgrades = getMaxUpgrades(definition);
        int clampedLevel = Math.max(0, Math.min(level, maxUpgrades));
        UpgradeTemplate template = TEMPLATES.getOrDefault(definition.id(), UpgradeTemplate.EMPTY);

        EnumMap<CyberwareEffectType, Double> scaledEffects = new EnumMap<>(CyberwareEffectType.class);
        for (var entry : template.effectCaps().entrySet()) {
            double scaledAmount = entry.getValue() * clampedLevel / (double) maxUpgrades;
            if (Math.abs(scaledAmount) > 0.0001D) {
                scaledEffects.put(entry.getKey(), scaledAmount);
            }
        }

        List<CyberwareEffect> effects = new ArrayList<>();
        for (CyberwareEffectType type : CyberwareEffectType.values()) {
            Double amount = scaledEffects.get(type);
            if (amount != null) {
                effects.add(new CyberwareEffect(type, amount));
            }
        }

        return new UpgradeState(
                effects,
                scaleIntegerBonus(template.capacityBonusCap(), clampedLevel, maxUpgrades),
                scaleIntegerBonus(template.chipSlotCap(), clampedLevel, maxUpgrades),
                scaleIntegerBonus(template.moduleSlotCap(), clampedLevel, maxUpgrades)
        );
    }

    private static UpgradeState diff(UpgradeState target, UpgradeState current) {
        EnumMap<CyberwareEffectType, Double> deltas = new EnumMap<>(CyberwareEffectType.class);
        for (CyberwareEffect effect : target.effects()) {
            deltas.merge(effect.type(), effect.amount(), Double::sum);
        }
        for (CyberwareEffect effect : current.effects()) {
            deltas.merge(effect.type(), -effect.amount(), Double::sum);
        }

        List<CyberwareEffect> effects = new ArrayList<>();
        for (CyberwareEffectType type : CyberwareEffectType.values()) {
            double amount = deltas.getOrDefault(type, 0.0D);
            if (Math.abs(amount) > 0.0001D) {
                effects.add(new CyberwareEffect(type, amount));
            }
        }

        return new UpgradeState(
                effects,
                target.capacityBonus() - current.capacityBonus(),
                target.chipSlotBonus() - current.chipSlotBonus(),
                target.moduleSlotBonus() - current.moduleSlotBonus()
        );
    }

    private static List<Component> describeUpgradeState(UpgradeState state) {
        List<Component> lines = new ArrayList<>();
        for (CyberwareEffect effect : state.effects()) {
            lines.add(Component.literal("-> ").append(effect.describe()));
        }
        if (state.capacityBonus() > 0) {
            lines.add(Component.literal("-> ").append(Component.translatable("tooltip.cyberneticenhancements.capacity_bonus", state.capacityBonus())));
        }
        if (state.chipSlotBonus() > 0) {
            lines.add(Component.literal("-> ").append(Component.translatable("tooltip.cyberneticenhancements.upgrade_chip_slots", state.chipSlotBonus())));
        }
        if (state.moduleSlotBonus() > 0) {
            lines.add(Component.literal("-> ").append(Component.translatable("tooltip.cyberneticenhancements.upgrade_module_slots", state.moduleSlotBonus())));
        }
        return lines;
    }

    private static int scaleIntegerBonus(int cap, int level, int maxUpgrades) {
        if (cap <= 0 || level <= 0 || maxUpgrades <= 0) {
            return 0;
        }
        return (int) Math.floor((double) cap * (double) level / (double) maxUpgrades + 1.0E-6D);
    }

    private static Map<String, UpgradeTemplate> createTemplates() {
        Map<String, UpgradeTemplate> templates = new LinkedHashMap<>();
        for (CyberwareDefinition definition : CyberwareCatalog.definitions()) {
            templates.put(definition.id(), buildTemplate(definition));
        }
        return templates;
    }

    private static UpgradeTemplate buildTemplate(CyberwareDefinition definition) {
        EnumMap<CyberwareEffectType, Double> effectCaps = new EnumMap<>(CyberwareEffectType.class);
        int capacityBonusCap = 0;
        int chipSlotCap = 0;
        int moduleSlotCap = 0;

        CyberwareDefinition current = definition;
        CyberwareDefinition next = CyberwareCatalog.findUpgradeStep(current);
        while (next != null) {
            capacityBonusCap += Math.max(0, next.capacityBonus() - current.capacityBonus());
            chipSlotCap += Math.max(0, next.chipSlotCount() - current.chipSlotCount());
            moduleSlotCap += Math.max(0, next.moduleSlotCount() - current.moduleSlotCount());
            accumulatePositiveEffectDeltas(effectCaps, current.effects(), next.effects());
            current = next;
            next = CyberwareCatalog.findUpgradeStep(current);
        }

        if (effectCaps.isEmpty() && capacityBonusCap <= 0 && chipSlotCap <= 0 && moduleSlotCap <= 0) {
            return buildGenericTemplate(definition);
        }
        return new UpgradeTemplate(effectCaps, capacityBonusCap, chipSlotCap, moduleSlotCap);
    }

    private static void accumulatePositiveEffectDeltas(
            EnumMap<CyberwareEffectType, Double> effectCaps,
            Iterable<CyberwareEffect> currentEffects,
            Iterable<CyberwareEffect> nextEffects
    ) {
        EnumMap<CyberwareEffectType, Double> currentTotals = effectTotals(currentEffects);
        EnumMap<CyberwareEffectType, Double> nextTotals = effectTotals(nextEffects);
        for (CyberwareEffectType type : CyberwareEffectType.values()) {
            if (type.isMobEffect()) {
                continue;
            }
            double delta = nextTotals.getOrDefault(type, 0.0D) - currentTotals.getOrDefault(type, 0.0D);
            if (delta > 0.0001D || (type == CyberwareEffectType.FALL_DAMAGE_REDUCTION && delta < -0.0001D)) {
                effectCaps.merge(type, delta, Double::sum);
            }
        }
    }

    private static UpgradeTemplate buildGenericTemplate(CyberwareDefinition definition) {
        EnumMap<CyberwareEffectType, Double> effectCaps = new EnumMap<>(CyberwareEffectType.class);
        for (CyberwareEffect effect : definition.effects()) {
            if (effect.type().isMobEffect()) {
                continue;
            }
            double cap = getGenericEffectCap(effect);
            if (Math.abs(cap) > 0.0001D) {
                effectCaps.put(effect.type(), cap);
            }
        }

        int capacityBonusCap = definition.capacityBonus() > 0
                ? Math.max(4, (int) Math.round(definition.capacityBonus() * 0.5D))
                : 0;
        if (effectCaps.isEmpty() && capacityBonusCap <= 0) {
            addFallbackEffect(effectCaps, definition.slotType());
        }
        return new UpgradeTemplate(effectCaps, capacityBonusCap, 0, 0);
    }

    private static void addFallbackEffect(EnumMap<CyberwareEffectType, Double> effectCaps, CyberwareSlotType slotType) {
        switch (slotType) {
            case FRONTAL_CORTEX, OPERATING_SYSTEM -> effectCaps.put(CyberwareEffectType.CHROME_CAPACITY, 6.0D);
            case ARMS -> effectCaps.put(CyberwareEffectType.ATTACK_DAMAGE, 1.0D);
            case FACE -> effectCaps.put(CyberwareEffectType.ENTITY_REACH, 0.5D);
            case SKELETON -> effectCaps.put(CyberwareEffectType.ARMOR, 2.0D);
            case HANDS -> effectCaps.put(CyberwareEffectType.ATTACK_SPEED, 0.15D);
            case NERVOUS_SYSTEM -> effectCaps.put(CyberwareEffectType.MOVEMENT_SPEED, 0.10D);
            case CIRCULATORY_SYSTEM -> effectCaps.put(CyberwareEffectType.HEALTH_REGEN, 0.4D);
            case INTEGUMENTARY_SYSTEM -> effectCaps.put(CyberwareEffectType.DAMAGE_REDUCTION, 0.05D);
            case LEGS -> effectCaps.put(CyberwareEffectType.MOVEMENT_SPEED, 0.10D);
        }
    }

    private static double getGenericEffectCap(CyberwareEffect effect) {
        double baseAmount = effect.amount();
        double magnitude = Math.abs(baseAmount);
        double minimum = switch (effect.type()) {
            case MAX_HEALTH, BONUS_ABSORPTION -> 2.0D;
            case ARMOR, ATTACK_DAMAGE, SAFE_FALL_DISTANCE, CHROME_CAPACITY -> 1.0D;
            case ATTACK_SPEED, BLOCK_REACH, ENTITY_REACH, STEP_HEIGHT, HEALTH_REGEN -> 0.25D;
            case MOVEMENT_SPEED, BLOCK_BREAK_SPEED, FALL_DAMAGE_REDUCTION, KNOCKBACK_RESISTANCE, DAMAGE_REDUCTION, JUMP_POWER -> 0.05D;
            case NIGHT_VISION, FIRE_RESISTANCE, WATER_BREATHING, DOLPHINS_GRACE -> 0.0D;
        };

        double signAwareBase = effect.type() == CyberwareEffectType.FALL_DAMAGE_REDUCTION ? -Math.max(minimum, magnitude * 0.5D) : Math.copySign(Math.max(minimum, magnitude * 0.5D), baseAmount);
        return magnitude <= 0.0001D ? 0.0D : signAwareBase;
    }

    private static EnumMap<CyberwareEffectType, Double> effectTotals(Iterable<CyberwareEffect> effects) {
        EnumMap<CyberwareEffectType, Double> totals = new EnumMap<>(CyberwareEffectType.class);
        for (CyberwareEffect effect : effects) {
            totals.merge(effect.type(), effect.amount(), Double::sum);
        }
        return totals;
    }

    private record UpgradeTemplate(
            EnumMap<CyberwareEffectType, Double> effectCaps,
            int capacityBonusCap,
            int chipSlotCap,
            int moduleSlotCap
    ) {
        private static final UpgradeTemplate EMPTY = new UpgradeTemplate(new EnumMap<>(CyberwareEffectType.class), 0, 0, 0);
    }

    private record UpgradeState(
            List<CyberwareEffect> effects,
            int capacityBonus,
            int chipSlotBonus,
            int moduleSlotBonus
    ) {
    }
}
