package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SkeletonCyberwareManager {
    private static final String EPIMORPHIC_SKELETON_ID = "epimorphic_skeleton";
    private static final String FEEN_X_ID = "feen_x";
    private static final String RAM_RECOUP_ID = "ram_recoup";
    private static final String SCAR_COALESCER_ID = "scar_coalescer";
    private static final String SCARAB_ID = "scarab";

    private static final Map<UUID, Long> FEEN_X_READY_UNTIL_TICK = new HashMap<>();
    private static final Map<UUID, Long> RAM_RECOUP_READY_UNTIL_TICK = new HashMap<>();
    private static final Map<UUID, Long> SCAR_COALESCER_READY_UNTIL_TICK = new HashMap<>();

    private SkeletonCyberwareManager() {
    }

    public static boolean hasSpecialBehavior(String id) {
        return switch (id) {
            case EPIMORPHIC_SKELETON_ID,
                 FEEN_X_ID,
                 RAM_RECOUP_ID,
                 SCAR_COALESCER_ID,
                 SCARAB_ID -> true;
            default -> false;
        };
    }

    public static void appendBehaviorTooltip(CyberwareDefinition definition, List<Component> tooltipComponents) {
        if (!hasSpecialBehavior(definition.id())) {
            return;
        }

        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.special." + definition.id())
                .withStyle(ChatFormatting.DARK_GREEN));
    }

    public static void mergePassiveEffects(Player player, PlayerCyberwareInventory inventory, EnumMap<CyberwareEffectType, Double> totals) {
        int scarabCount = inventory.countInstalledCyberware(SCARAB_ID);
        if (scarabCount > 0 && player.isCrouching()) {
            merge(totals, CyberwareEffectType.ARMOR, 2.0D * scarabCount);
            merge(totals, CyberwareEffectType.DAMAGE_REDUCTION, 0.06D * scarabCount);
            merge(totals, CyberwareEffectType.MOVEMENT_SPEED, 0.06D * scarabCount);
        }

        int epimorphicCount = inventory.countInstalledCyberware(EPIMORPHIC_SKELETON_ID);
        if (epimorphicCount > 0 && player.getHealth() < player.getMaxHealth()) {
            double missingRatio = 1.0D - (player.getHealth() / player.getMaxHealth());
            merge(totals, CyberwareEffectType.HEALTH_REGEN, epimorphicCount * (0.30D + missingRatio * 1.10D));
            merge(totals, CyberwareEffectType.KNOCKBACK_RESISTANCE, epimorphicCount * missingRatio * 0.10D);
        }
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        float incomingDamage = event.getAmount();
        if (incomingDamage <= 0.0F) {
            return;
        }

        PlayerCyberwareInventory inventory = new PlayerCyberwareInventory(player);
        long gameTime = player.level().getGameTime();
        float healthAfterHit = player.getHealth() - incomingDamage;
        float lowHealthThreshold = player.getMaxHealth() * 0.35F;
        float dangerHealthThreshold = player.getMaxHealth() * 0.50F;

        triggerRamRecoup(player, inventory, incomingDamage, gameTime);

        int scarCoalescerCount = inventory.countInstalledCyberware(SCAR_COALESCER_ID);
        if (scarCoalescerCount > 0
                && healthAfterHit > 0.0F
                && healthAfterHit <= dangerHealthThreshold
                && SCAR_COALESCER_READY_UNTIL_TICK.getOrDefault(player.getUUID(), 0L) <= gameTime) {
            TemporaryCyberwareEffectManager.addEffects(player, 120L,
                    CyberwareEffect.armor(2.0D * scarCoalescerCount),
                    CyberwareEffect.damageReduction(0.12D * scarCoalescerCount),
                    CyberwareEffect.healthRegen(0.8D * scarCoalescerCount));
            SCAR_COALESCER_READY_UNTIL_TICK.put(player.getUUID(), gameTime + 28L * 20L);
        }

        int feenXCount = inventory.countInstalledCyberware(FEEN_X_ID);
        if (feenXCount > 0
                && healthAfterHit > 0.0F
                && healthAfterHit <= lowHealthThreshold
                && FEEN_X_READY_UNTIL_TICK.getOrDefault(player.getUUID(), 0L) <= gameTime) {
            long flatRefundTicks = 100L * feenXCount;
            double percentRefund = Math.min(0.30D, 0.10D * feenXCount);
            refundTrackedCooldowns(player, flatRefundTicks, percentRefund);
            TemporaryCyberwareEffectManager.addEffects(player, 100L,
                    CyberwareEffect.bonusAbsorption(4.0D * feenXCount),
                    CyberwareEffect.moveSpeed(0.10D * feenXCount),
                    CyberwareEffect.damageReduction(0.08D * feenXCount));
            FEEN_X_READY_UNTIL_TICK.put(player.getUUID(), gameTime + 36L * 20L);
        }

        CyberwareEffects.refreshPlayerCyberware(player);
    }

    public static void clearCooldowns(Player player) {
        UUID playerId = player.getUUID();
        FEEN_X_READY_UNTIL_TICK.remove(playerId);
        RAM_RECOUP_READY_UNTIL_TICK.remove(playerId);
        SCAR_COALESCER_READY_UNTIL_TICK.remove(playerId);
    }

    private static void triggerRamRecoup(Player player, PlayerCyberwareInventory inventory, float incomingDamage, long gameTime) {
        int ramRecoupCount = inventory.countInstalledCyberware(RAM_RECOUP_ID);
        if (ramRecoupCount <= 0 || RAM_RECOUP_READY_UNTIL_TICK.getOrDefault(player.getUUID(), 0L) > gameTime) {
            return;
        }

        long flatRefundTicks = Math.min(100L * ramRecoupCount, Math.round(incomingDamage * 10.0F) * ramRecoupCount);
        double percentRefund = Math.min(0.18D, incomingDamage * 0.008D * ramRecoupCount);
        if (flatRefundTicks <= 0L && percentRefund <= 0.0D) {
            return;
        }

        refundTrackedCooldowns(player, flatRefundTicks, percentRefund);
        RAM_RECOUP_READY_UNTIL_TICK.put(player.getUUID(), gameTime + 6L * 20L);
    }

    private static void refundTrackedCooldowns(Player player, long flatTicks, double percentRefund) {
        CyberwareAbilities.reduceTrackedCooldowns(player, flatTicks, percentRefund);
        ArmCyberwareManager.reduceTrackedCooldowns(player, flatTicks, percentRefund);
        FaceCyberwareManager.reduceTrackedCooldowns(player, flatTicks, percentRefund);
        FrontalCortexManager.reduceTrackedCooldowns(player, flatTicks, percentRefund);
    }

    private static void merge(EnumMap<CyberwareEffectType, Double> totals, CyberwareEffectType type, double amount) {
        totals.merge(type, amount, type.isMobEffect() ? Math::max : Double::sum);
    }
}
