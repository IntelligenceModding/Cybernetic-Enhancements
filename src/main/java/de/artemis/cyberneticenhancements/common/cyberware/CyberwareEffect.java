package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.network.chat.Component;

import java.util.Locale;

public record CyberwareEffect(CyberwareEffectType type, double amount) {
    public Component describe() {
        return switch (type) {
            case MAX_HEALTH -> Component.translatable(type.translationKey(), format(amount / 2.0D));
            case ARMOR,
                 ATTACK_DAMAGE,
                 ATTACK_SPEED,
                 BLOCK_REACH,
                 ENTITY_REACH,
                 STEP_HEIGHT,
                 SAFE_FALL_DISTANCE,
                 BONUS_ABSORPTION,
                 CHROME_CAPACITY -> Component.translatable(type.translationKey(), format(type == CyberwareEffectType.BONUS_ABSORPTION ? amount / 2.0D : amount));
            case KNOCKBACK_RESISTANCE -> Component.translatable(type.translationKey(), formatPercent(amount));
            case MOVEMENT_SPEED,
                 BLOCK_BREAK_SPEED,
                 FALL_DAMAGE_REDUCTION,
                 DAMAGE_REDUCTION,
                 JUMP_POWER -> Component.translatable(type.translationKey(), formatPercent(amount));
            case HEALTH_REGEN -> Component.translatable(type.translationKey(), format(amount / 2.0D));
            case NIGHT_VISION,
                 FIRE_RESISTANCE,
                 WATER_BREATHING -> Component.translatable(type.translationKey());
        };
    }

    public static CyberwareEffect hearts(double healthPoints) {
        return new CyberwareEffect(CyberwareEffectType.MAX_HEALTH, healthPoints);
    }

    public static CyberwareEffect armor(double amount) {
        return new CyberwareEffect(CyberwareEffectType.ARMOR, amount);
    }

    public static CyberwareEffect damage(double amount) {
        return new CyberwareEffect(CyberwareEffectType.ATTACK_DAMAGE, amount);
    }

    public static CyberwareEffect attackSpeed(double amount) {
        return new CyberwareEffect(CyberwareEffectType.ATTACK_SPEED, amount);
    }

    public static CyberwareEffect moveSpeed(double amount) {
        return new CyberwareEffect(CyberwareEffectType.MOVEMENT_SPEED, amount);
    }

    public static CyberwareEffect breakSpeed(double amount) {
        return new CyberwareEffect(CyberwareEffectType.BLOCK_BREAK_SPEED, amount);
    }

    public static CyberwareEffect blockReach(double amount) {
        return new CyberwareEffect(CyberwareEffectType.BLOCK_REACH, amount);
    }

    public static CyberwareEffect entityReach(double amount) {
        return new CyberwareEffect(CyberwareEffectType.ENTITY_REACH, amount);
    }

    public static CyberwareEffect stepHeight(double amount) {
        return new CyberwareEffect(CyberwareEffectType.STEP_HEIGHT, amount);
    }

    public static CyberwareEffect safeFall(double amount) {
        return new CyberwareEffect(CyberwareEffectType.SAFE_FALL_DISTANCE, amount);
    }

    public static CyberwareEffect fallReduction(double amount) {
        return new CyberwareEffect(CyberwareEffectType.FALL_DAMAGE_REDUCTION, amount);
    }

    public static CyberwareEffect knockbackResistance(double amount) {
        return new CyberwareEffect(CyberwareEffectType.KNOCKBACK_RESISTANCE, amount);
    }

    public static CyberwareEffect healthRegen(double amount) {
        return new CyberwareEffect(CyberwareEffectType.HEALTH_REGEN, amount);
    }

    public static CyberwareEffect chromeCapacity(double amount) {
        return new CyberwareEffect(CyberwareEffectType.CHROME_CAPACITY, amount);
    }

    public static CyberwareEffect damageReduction(double amount) {
        return new CyberwareEffect(CyberwareEffectType.DAMAGE_REDUCTION, amount);
    }

    public static CyberwareEffect bonusAbsorption(double amount) {
        return new CyberwareEffect(CyberwareEffectType.BONUS_ABSORPTION, amount);
    }

    public static CyberwareEffect jumpPower(double amount) {
        return new CyberwareEffect(CyberwareEffectType.JUMP_POWER, amount);
    }

    public static CyberwareEffect nightVision() {
        return new CyberwareEffect(CyberwareEffectType.NIGHT_VISION, 0);
    }

    public static CyberwareEffect fireResistance() {
        return new CyberwareEffect(CyberwareEffectType.FIRE_RESISTANCE, 0);
    }

    public static CyberwareEffect waterBreathing() {
        return new CyberwareEffect(CyberwareEffectType.WATER_BREATHING, 0);
    }

    private static String format(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001D) {
            return Integer.toString((int) Math.rint(value));
        }
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String formatPercent(double value) {
        return format(value * 100.0D);
    }
}
