package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CyberwareAbilities {
    private static final Map<UUID, Long> ACTIVE_UNTIL_TICK = new HashMap<>();
    private static final Map<UUID, Long> ACTIVE_COOLDOWN_UNTIL_TICK = new HashMap<>();
    private static final Map<UUID, OperatingSystemFamily> ACTIVE_FAMILY = new HashMap<>();
    private static final Map<UUID, Long> BIOMONITOR_COOLDOWN_UNTIL_TICK = new HashMap<>();
    private static final Map<UUID, Long> BLOOD_PUMP_COOLDOWN_UNTIL_TICK = new HashMap<>();
    private static final Map<UUID, Long> SECOND_HEART_COOLDOWN_UNTIL_TICK = new HashMap<>();
    private static final Map<UUID, Long> REFLEX_TUNER_COOLDOWN_UNTIL_TICK = new HashMap<>();

    private CyberwareAbilities() {
    }

    public static void activate(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        PlayerCyberwareInventory inventory = new PlayerCyberwareInventory(player);
        CyberwareItem operatingSystem = inventory.getInstalledCyberwareBySlotType(CyberwareSlotType.OPERATING_SYSTEM);
        if (operatingSystem == null) {
            notify(player, "message.cyberneticenhancements.ability.no_operating_system");
            return;
        }

        OperatingSystemFamily family = getOperatingSystemFamily(operatingSystem);
        if (family == OperatingSystemFamily.NONE) {
            notify(player, "message.cyberneticenhancements.ability.no_active_ability");
            return;
        }

        long gameTime = player.level().getGameTime();
        long cooldownUntil = ACTIVE_COOLDOWN_UNTIL_TICK.getOrDefault(player.getUUID(), 0L);
        if (cooldownUntil > gameTime) {
            notify(player, "message.cyberneticenhancements.ability.cooldown", (cooldownUntil - gameTime + 19L) / 20L);
            return;
        }

        switch (family) {
            case SANDEVISTAN -> activateTimedOperatingSystem(player, operatingSystem, inventory, family, gameTime, 8, 30, "message.cyberneticenhancements.ability.sandevistan");
            case BERSERK -> activateTimedOperatingSystem(player, operatingSystem, inventory, family, gameTime, 10, 35, "message.cyberneticenhancements.ability.berserk");
            default -> notify(player, "message.cyberneticenhancements.ability.no_active_ability");
        }
    }

    public static void applyTriggeredAbilities(Player player, PlayerCyberwareInventory inventory) {
        if (player.level().isClientSide()) {
            return;
        }

        long gameTime = player.level().getGameTime();
        float healthRatio = player.getHealth() / Math.max(1.0F, player.getMaxHealth());

        if (healthRatio <= 0.35F && inventory.hasInstalledCyberware("biomonitor") && isReady(BIOMONITOR_COOLDOWN_UNTIL_TICK, player, gameTime)) {
            player.heal(4.0F);
            TemporaryCyberwareEffectManager.addEffects(player, 160,
                    CyberwareEffect.healthRegen(1.0D),
                    CyberwareEffect.bonusAbsorption(4.0D));
            BIOMONITOR_COOLDOWN_UNTIL_TICK.put(player.getUUID(), gameTime + 45L * 20L);
            notify(player, "message.cyberneticenhancements.trigger.biomonitor");
        }

        if (healthRatio <= 0.25F && inventory.hasInstalledCyberware("blood_pump") && isReady(BLOOD_PUMP_COOLDOWN_UNTIL_TICK, player, gameTime)) {
            player.heal(6.0F);
            TemporaryCyberwareEffectManager.addEffects(player, 200,
                    CyberwareEffect.bonusAbsorption(8.0D),
                    CyberwareEffect.damageReduction(0.10D));
            BLOOD_PUMP_COOLDOWN_UNTIL_TICK.put(player.getUUID(), gameTime + 60L * 20L);
            notify(player, "message.cyberneticenhancements.trigger.blood_pump");
        }

        if (healthRatio <= 0.15F && inventory.hasInstalledCyberware("second_heart") && isReady(SECOND_HEART_COOLDOWN_UNTIL_TICK, player, gameTime)) {
            float healTarget = Math.max(player.getMaxHealth() * 0.5F, player.getHealth() + 10.0F);
            player.setHealth(Math.min(player.getMaxHealth(), healTarget));
            TemporaryCyberwareEffectManager.addEffects(player, 240,
                    CyberwareEffect.healthRegen(1.5D),
                    CyberwareEffect.bonusAbsorption(12.0D),
                    CyberwareEffect.damageReduction(0.20D));
            SECOND_HEART_COOLDOWN_UNTIL_TICK.put(player.getUUID(), gameTime + 180L * 20L);
            notify(player, "message.cyberneticenhancements.trigger.second_heart");
        }

        if (healthRatio <= 0.30F && inventory.hasInstalledCyberware("reflex_tuner") && isReady(REFLEX_TUNER_COOLDOWN_UNTIL_TICK, player, gameTime)) {
            TemporaryCyberwareEffectManager.addEffects(player, 120,
                    CyberwareEffect.moveSpeed(0.40D),
                    CyberwareEffect.breakSpeed(0.20D),
                    CyberwareEffect.attackSpeed(0.20D));
            REFLEX_TUNER_COOLDOWN_UNTIL_TICK.put(player.getUUID(), gameTime + 40L * 20L);
            notify(player, "message.cyberneticenhancements.trigger.reflex_tuner");
        }
    }

    public static void mergeActiveEffects(Player player, PlayerCyberwareInventory inventory, EnumMap<CyberwareEffectType, Double> totals) {
        long gameTime = player.level().getGameTime();
        long activeUntil = ACTIVE_UNTIL_TICK.getOrDefault(player.getUUID(), 0L);
        if (activeUntil <= gameTime) {
            ACTIVE_FAMILY.remove(player.getUUID());
            return;
        }

        CyberwareItem operatingSystem = inventory.getInstalledCyberwareBySlotType(CyberwareSlotType.OPERATING_SYSTEM);
        if (operatingSystem == null) {
            ACTIVE_UNTIL_TICK.remove(player.getUUID());
            ACTIVE_FAMILY.remove(player.getUUID());
            return;
        }

        OperatingSystemFamily family = ACTIVE_FAMILY.getOrDefault(player.getUUID(), OperatingSystemFamily.NONE);
        switch (family) {
            case SANDEVISTAN -> {
                merge(totals, CyberwareEffectType.MOVEMENT_SPEED, movementBonusForTier(operatingSystem.getTier()));
                merge(totals, CyberwareEffectType.ATTACK_SPEED, attackSpeedBonusForTier(operatingSystem.getTier()));
                merge(totals, CyberwareEffectType.JUMP_POWER, jumpBonusForTier(operatingSystem.getTier()));
            }
            case BERSERK -> {
                merge(totals, CyberwareEffectType.ATTACK_DAMAGE, damageBonusForTier(operatingSystem.getTier()));
                merge(totals, CyberwareEffectType.ARMOR, armorBonusForTier(operatingSystem.getTier()));
                merge(totals, CyberwareEffectType.KNOCKBACK_RESISTANCE, 0.25D);
                merge(totals, CyberwareEffectType.BONUS_ABSORPTION, absorptionBonusForTier(operatingSystem.getTier()));
                merge(totals, CyberwareEffectType.DAMAGE_REDUCTION, resistanceBonusForTier(operatingSystem.getTier()));
            }
            default -> {
            }
        }
    }

    public static OperatingSystemFamily getOperatingSystemFamily(CyberwareItem operatingSystem) {
        String id = operatingSystem.getDefinition().id();
        if (id.contains("sandevistan") || id.contains("warp_dancer") || id.contains("falcon") || id.contains("apogee")) {
            return OperatingSystemFamily.SANDEVISTAN;
        }
        if (id.contains("berserk")) {
            return OperatingSystemFamily.BERSERK;
        }
        return OperatingSystemFamily.NONE;
    }

    private static void activateTimedOperatingSystem(
            Player player,
            CyberwareItem operatingSystem,
            PlayerCyberwareInventory inventory,
            OperatingSystemFamily family,
            long gameTime,
            int durationSeconds,
            int cooldownSeconds,
            String translationKey
    ) {
        long adjustedCooldown = Math.max(20L, Math.round(cooldownSeconds * 20L * CyberstrainManager.getAbilityCooldownMultiplier(player, inventory)));
        ACTIVE_FAMILY.put(player.getUUID(), family);
        ACTIVE_UNTIL_TICK.put(player.getUUID(), gameTime + durationSeconds * 20L);
        ACTIVE_COOLDOWN_UNTIL_TICK.put(player.getUUID(), gameTime + adjustedCooldown);
        CyberwareEffects.refreshPlayerCyberware(player);
        notify(player, translationKey, durationSeconds);
    }

    private static boolean isReady(Map<UUID, Long> cooldowns, Player player, long gameTime) {
        return cooldowns.getOrDefault(player.getUUID(), 0L) <= gameTime;
    }

    private static void merge(EnumMap<CyberwareEffectType, Double> totals, CyberwareEffectType type, double amount) {
        totals.merge(type, amount, type.isMobEffect() ? Math::max : Double::sum);
    }

    private static double movementBonusForTier(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1 -> 0.20D;
            case TIER_2 -> 0.25D;
            case TIER_3 -> 0.30D;
            case TIER_4 -> 0.35D;
            case TIER_5 -> 0.45D;
        };
    }

    private static double attackSpeedBonusForTier(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1 -> 0.20D;
            case TIER_2 -> 0.25D;
            case TIER_3 -> 0.30D;
            case TIER_4 -> 0.35D;
            case TIER_5 -> 0.45D;
        };
    }

    private static double jumpBonusForTier(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1, TIER_2 -> 0.0D;
            case TIER_3, TIER_4 -> 0.25D;
            case TIER_5 -> 0.45D;
        };
    }

    private static double damageBonusForTier(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1 -> 2.0D;
            case TIER_2 -> 3.0D;
            case TIER_3 -> 4.0D;
            case TIER_4 -> 5.0D;
            case TIER_5 -> 6.0D;
        };
    }

    private static double armorBonusForTier(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1 -> 3.0D;
            case TIER_2 -> 4.0D;
            case TIER_3 -> 5.0D;
            case TIER_4 -> 6.0D;
            case TIER_5 -> 8.0D;
        };
    }

    private static double absorptionBonusForTier(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1, TIER_2 -> 0.0D;
            case TIER_3, TIER_4 -> 8.0D;
            case TIER_5 -> 12.0D;
        };
    }

    private static double resistanceBonusForTier(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1, TIER_2 -> 0.0D;
            case TIER_3, TIER_4 -> 0.10D;
            case TIER_5 -> 0.20D;
        };
    }

    private static void notify(Player player, String translationKey, Object... args) {
        player.displayClientMessage(Component.translatable(translationKey, args).withStyle(ChatFormatting.AQUA), true);
    }
}
