package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.network.CyberpsychosisControlPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

public final class CyberstrainManager {
    private static final String SUPPRESSION_VALUE_KEY = "cyberneticenhancements.cyberstrain_suppression";
    private static final String SUPPRESSION_UNTIL_KEY = "cyberneticenhancements.cyberstrain_suppression_until";
    private static final String RAM_JOLT_UNTIL_KEY = "cyberneticenhancements.ram_jolt_until";
    private static final String RAM_JOLT_MULTIPLIER_KEY = "cyberneticenhancements.ram_jolt_multiplier";
    private static final String PSYCHOSIS_UNTIL_KEY = "cyberneticenhancements.psychosis_until";
    private static final String PSYCHOSIS_COOLDOWN_UNTIL_KEY = "cyberneticenhancements.psychosis_cooldown_until";
    private static final String PSYCHOSIS_LAST_ATTACK_KEY = "cyberneticenhancements.psychosis_last_attack";
    private static final String PSYCHOSIS_WANDER_YAW_KEY = "cyberneticenhancements.psychosis_wander_yaw";
    private static final int PSYCHOSIS_THRESHOLD = 50;
    private static final int CRITICAL_THRESHOLD = 35;
    private static final int UNSTABLE_THRESHOLD = 20;
    private static final int STRAINED_THRESHOLD = 10;

    private CyberstrainManager() {
    }

    public static void onPlayerTick(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        long gameTime = player.level().getGameTime();
        cleanupExpiredState(player, gameTime);
        PlayerCyberwareInventory inventory = new PlayerCyberwareInventory(player);
        int effectiveCyberstrain = getEffectiveCyberstrain(player, inventory, gameTime);

        if (isPsychosisActive(player, gameTime)) {
            runPsychosisEpisode(player, gameTime);
        } else {
            maybeStartPsychosisEpisode(player, effectiveCyberstrain, gameTime);
            if (effectiveCyberstrain >= PSYCHOSIS_THRESHOLD) {
                applyCriticalInstability(player, gameTime);
            }
        }

        if (player instanceof ServerPlayer serverPlayer && player.tickCount % 20 == 0) {
            syncControlLock(serverPlayer);
        }
    }

    public static void applyPenalties(Player player, PlayerCyberwareInventory inventory, EnumMap<CyberwareEffectType, Double> totals) {
        long gameTime = player.level().getGameTime();
        int effectiveCyberstrain = getEffectiveCyberstrain(player, inventory, gameTime);
        if (effectiveCyberstrain <= 0) {
            return;
        }

        if (effectiveCyberstrain >= STRAINED_THRESHOLD) {
            merge(totals, CyberwareEffectType.MOVEMENT_SPEED, -0.05D);
            merge(totals, CyberwareEffectType.BLOCK_BREAK_SPEED, -0.10D);
        }
        if (effectiveCyberstrain >= UNSTABLE_THRESHOLD) {
            merge(totals, CyberwareEffectType.ATTACK_SPEED, -0.10D);
            merge(totals, CyberwareEffectType.MOVEMENT_SPEED, -0.05D);
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0, true, false, false));
        }
        if (effectiveCyberstrain >= CRITICAL_THRESHOLD) {
            merge(totals, CyberwareEffectType.MAX_HEALTH, -4.0D);
            merge(totals, CyberwareEffectType.BLOCK_BREAK_SPEED, -0.10D);
            merge(totals, CyberwareEffectType.MOVEMENT_SPEED, -0.05D);
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0, true, false, false));
        }
        if (effectiveCyberstrain >= PSYCHOSIS_THRESHOLD) {
            merge(totals, CyberwareEffectType.ATTACK_SPEED, -0.15D);
            merge(totals, CyberwareEffectType.BLOCK_BREAK_SPEED, -0.15D);
            merge(totals, CyberwareEffectType.MAX_HEALTH, -4.0D);
        }
        if (isPsychosisActive(player, gameTime)) {
            merge(totals, CyberwareEffectType.MOVEMENT_SPEED, 0.15D);
            merge(totals, CyberwareEffectType.ATTACK_DAMAGE, 3.0D);
            merge(totals, CyberwareEffectType.DAMAGE_REDUCTION, 0.10D);
            merge(totals, CyberwareEffectType.KNOCKBACK_RESISTANCE, 0.15D);
        }
    }

    public static double getHealingMultiplier(Player player) {
        PlayerCyberwareInventory inventory = new PlayerCyberwareInventory(player);
        int effectiveCyberstrain = getEffectiveCyberstrain(player, inventory);
        if (effectiveCyberstrain >= PSYCHOSIS_THRESHOLD) {
            return 0.30D;
        }
        if (effectiveCyberstrain >= CRITICAL_THRESHOLD) {
            return 0.45D;
        }
        if (effectiveCyberstrain >= UNSTABLE_THRESHOLD) {
            return 0.65D;
        }
        if (effectiveCyberstrain >= STRAINED_THRESHOLD) {
            return 0.85D;
        }
        return 1.0D;
    }

    public static double getAbilityCooldownMultiplier(Player player, PlayerCyberwareInventory inventory) {
        long gameTime = player.level().getGameTime();
        int effectiveCyberstrain = getEffectiveCyberstrain(player, inventory, gameTime);
        double multiplier = 1.0D;
        if (effectiveCyberstrain >= PSYCHOSIS_THRESHOLD) {
            multiplier *= 2.00D;
        } else if (effectiveCyberstrain >= CRITICAL_THRESHOLD) {
            multiplier *= 1.60D;
        } else if (effectiveCyberstrain >= UNSTABLE_THRESHOLD) {
            multiplier *= 1.30D;
        } else if (effectiveCyberstrain >= STRAINED_THRESHOLD) {
            multiplier *= 1.15D;
        }

        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(RAM_JOLT_UNTIL_KEY) && persistentData.getLong(RAM_JOLT_UNTIL_KEY) > gameTime) {
            multiplier *= persistentData.getDouble(RAM_JOLT_MULTIPLIER_KEY);
        }

        return Math.max(0.25D, multiplier);
    }

    public static int getEffectiveCyberstrain(Player player, PlayerCyberwareInventory inventory) {
        return getEffectiveCyberstrain(player, inventory, player.level().getGameTime());
    }

    public static int getEffectiveCyberstrain(Player player, PlayerCyberwareInventory inventory, long gameTime) {
        cleanupExpiredState(player, gameTime);
        return Math.max(0, inventory.getCyberstrain() - getSuppressionAmount(player, gameTime));
    }

    public static int getSuppressionAmount(Player player) {
        return getSuppressionAmount(player, player.level().getGameTime());
    }

    public static void applySuppressionDose(Player player, int suppressionAmount, long durationTicks, boolean hallucinationSideEffect) {
        CompoundTag persistentData = player.getPersistentData();
        long until = player.level().getGameTime() + durationTicks;
        persistentData.putInt(SUPPRESSION_VALUE_KEY, Math.max(suppressionAmount, persistentData.getInt(SUPPRESSION_VALUE_KEY)));
        persistentData.putLong(SUPPRESSION_UNTIL_KEY, Math.max(until, persistentData.getLong(SUPPRESSION_UNTIL_KEY)));

        if (hallucinationSideEffect) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0, true, false, false));
            player.displayClientMessage(Component.translatable("message.cyberneticenhancements.consumable.immunoblockers"), true);
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0, true, false, false));
            player.displayClientMessage(Component.translatable("message.cyberneticenhancements.consumable.chrome_suppressant"), true);
        }
    }

    public static void applyRamJolt(Player player, long durationTicks, double cooldownMultiplier) {
        CompoundTag persistentData = player.getPersistentData();
        long until = player.level().getGameTime() + durationTicks;
        persistentData.putLong(RAM_JOLT_UNTIL_KEY, Math.max(until, persistentData.getLong(RAM_JOLT_UNTIL_KEY)));
        persistentData.putDouble(RAM_JOLT_MULTIPLIER_KEY, Math.min(cooldownMultiplier, persistentData.contains(RAM_JOLT_MULTIPLIER_KEY) ? persistentData.getDouble(RAM_JOLT_MULTIPLIER_KEY) : 1.0D));
        player.displayClientMessage(Component.translatable("message.cyberneticenhancements.consumable.ram_jolt"), true);
    }

    public static void forcePsychosisEpisode(ServerPlayer player, int durationSeconds) {
        startPsychosisEpisode(player, Math.max(5, durationSeconds) * 20L, true);
    }

    public static void clearPsychosis(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        persistentData.remove(PSYCHOSIS_UNTIL_KEY);
        persistentData.remove(PSYCHOSIS_COOLDOWN_UNTIL_KEY);
        persistentData.remove(PSYCHOSIS_LAST_ATTACK_KEY);
        persistentData.remove(PSYCHOSIS_WANDER_YAW_KEY);
        syncControlLock(player);
    }

    public static boolean isPsychosisActive(Player player) {
        return isPsychosisActive(player, player.level().getGameTime());
    }

    public static boolean isPsychosisActive(Player player, long gameTime) {
        cleanupExpiredState(player, gameTime);
        return player.getPersistentData().getLong(PSYCHOSIS_UNTIL_KEY) > gameTime;
    }

    public static long getPsychosisSecondsRemaining(Player player) {
        long remainingTicks = Math.max(0L, player.getPersistentData().getLong(PSYCHOSIS_UNTIL_KEY) - player.level().getGameTime());
        return (remainingTicks + 19L) / 20L;
    }

    public static String getPsychosisStateLabel(Player player, PlayerCyberwareInventory inventory) {
        if (isPsychosisActive(player)) {
            return "PSYCHOTIC";
        }

        int effectiveCyberstrain = getEffectiveCyberstrain(player, inventory);
        if (effectiveCyberstrain >= PSYCHOSIS_THRESHOLD) {
            return "EPISODE_RISK";
        }
        if (effectiveCyberstrain >= CRITICAL_THRESHOLD) {
            return "CRITICAL";
        }
        if (effectiveCyberstrain >= UNSTABLE_THRESHOLD) {
            return "UNSTABLE";
        }
        if (effectiveCyberstrain >= STRAINED_THRESHOLD) {
            return "STRAINED";
        }
        return "STABLE";
    }

    public static void syncControlLock(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new CyberpsychosisControlPayload(isPsychosisActive(player)));
    }

    public static void copyState(Player fromPlayer, Player toPlayer) {
        CompoundTag fromData = fromPlayer.getPersistentData();
        CompoundTag toData = toPlayer.getPersistentData();
        copyLong(fromData, toData, SUPPRESSION_UNTIL_KEY);
        copyInt(fromData, toData, SUPPRESSION_VALUE_KEY);
        copyLong(fromData, toData, RAM_JOLT_UNTIL_KEY);
        copyDouble(fromData, toData, RAM_JOLT_MULTIPLIER_KEY);
        copyLong(fromData, toData, PSYCHOSIS_UNTIL_KEY);
        copyLong(fromData, toData, PSYCHOSIS_COOLDOWN_UNTIL_KEY);
        copyLong(fromData, toData, PSYCHOSIS_LAST_ATTACK_KEY);
        copyFloat(fromData, toData, PSYCHOSIS_WANDER_YAW_KEY);
    }

    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        double multiplier = getHealingMultiplier(player);
        if (multiplier < 0.999D) {
            event.setAmount((float) (event.getAmount() * multiplier));
        }
    }

    private static void maybeStartPsychosisEpisode(Player player, int effectiveCyberstrain, long gameTime) {
        if (!(player instanceof ServerPlayer serverPlayer) || effectiveCyberstrain < PSYCHOSIS_THRESHOLD) {
            return;
        }
        if (player.getPersistentData().getLong(PSYCHOSIS_COOLDOWN_UNTIL_KEY) > gameTime || gameTime % 40L != 0L) {
            return;
        }

        double triggerChance = Mth.clamp(0.12D + (effectiveCyberstrain - PSYCHOSIS_THRESHOLD) * 0.012D, 0.12D, 0.60D);
        if (player.getRandom().nextDouble() < triggerChance) {
            long durationTicks = (12L + Math.min(12L, Math.max(0L, effectiveCyberstrain - PSYCHOSIS_THRESHOLD) / 2L)) * 20L;
            startPsychosisEpisode(serverPlayer, durationTicks, false);
        }
    }

    private static void startPsychosisEpisode(ServerPlayer player, long durationTicks, boolean forced) {
        long gameTime = player.level().getGameTime();
        CompoundTag persistentData = player.getPersistentData();
        persistentData.putLong(PSYCHOSIS_UNTIL_KEY, gameTime + durationTicks);
        persistentData.putLong(PSYCHOSIS_COOLDOWN_UNTIL_KEY, gameTime + durationTicks + 30L * 20L);
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, true, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0, true, false, false));
        player.displayClientMessage(Component.translatable(
                forced ? "message.cyberneticenhancements.psychosis.forced" : "message.cyberneticenhancements.psychosis.triggered"), true);
        syncControlLock(player);
    }

    private static void runPsychosisEpisode(Player player, long gameTime) {
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, true, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, true, false, false));
        if (gameTime % 40L == 0L) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, true, false, false));
        }

        LivingEntity target = findPsychosisTarget(player);
        if (target != null) {
            steerTowardTarget(player, target);
            tryAttackTarget(player, target, gameTime);
        } else {
            wanderAggressively(player, gameTime);
        }
    }

    private static void applyCriticalInstability(Player player, long gameTime) {
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, true, false, false));
        if (gameTime % 80L == 0L) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0, true, false, false));
        }
    }

    private static LivingEntity findPsychosisTarget(Player player) {
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(20.0D),
                entity -> entity != player && entity.isAlive() && isPsychosisTarget(entity));

        return targets.stream()
                .min(Comparator
                        .comparingInt(CyberstrainManager::targetPriority)
                        .thenComparingDouble(entity -> entity.distanceToSqr(player)))
                .orElse(null);
    }

    private static boolean isPsychosisTarget(LivingEntity entity) {
        if (entity instanceof Player targetPlayer) {
            return !targetPlayer.isCreative() && !targetPlayer.isSpectator();
        }
        return entity instanceof Monster || entity instanceof Animal;
    }

    private static int targetPriority(LivingEntity entity) {
        if (entity instanceof Player) {
            return 0;
        }
        if (entity instanceof Monster) {
            return 1;
        }
        if (entity instanceof Animal) {
            return 2;
        }
        return 3;
    }

    private static void steerTowardTarget(Player player, LivingEntity target) {
        Vec3 delta = target.position().subtract(player.position());
        Vec3 horizontal = new Vec3(delta.x, 0.0D, delta.z);
        if (horizontal.lengthSqr() < 0.0001D) {
            return;
        }

        Vec3 direction = horizontal.normalize();
        double yaw = Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0D;
        float yawFloat = (float) yaw;
        player.setYRot(yawFloat);
        player.setYHeadRot(yawFloat);
        player.setYBodyRot(yawFloat);
        player.setXRot((float) Mth.clamp(-Math.toDegrees(Math.atan2(delta.y, horizontal.length())), -45.0D, 45.0D));
        drivePsychosisMovement(player, direction, 1.12D, delta.y > 0.6D);
    }

    private static void tryAttackTarget(Player player, LivingEntity target, long gameTime) {
        if (!(player instanceof ServerPlayer serverPlayer) || player.distanceToSqr(target) > 9.0D) {
            return;
        }

        CompoundTag persistentData = player.getPersistentData();
        long lastAttack = persistentData.getLong(PSYCHOSIS_LAST_ATTACK_KEY);
        if (gameTime - lastAttack < 10L) {
            return;
        }

        serverPlayer.attack(target);
        serverPlayer.swing(InteractionHand.MAIN_HAND, true);
        persistentData.putLong(PSYCHOSIS_LAST_ATTACK_KEY, gameTime);
    }

    private static void wanderAggressively(Player player, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        float yaw = persistentData.contains(PSYCHOSIS_WANDER_YAW_KEY) && gameTime % 30L != 0L
                ? persistentData.getFloat(PSYCHOSIS_WANDER_YAW_KEY)
                : player.getYRot() + (player.getRandom().nextFloat() - 0.5F) * 80.0F;
        persistentData.putFloat(PSYCHOSIS_WANDER_YAW_KEY, yaw);
        player.setYRot(yaw);
        player.setYHeadRot(yaw);
        player.setYBodyRot(yaw);
        Vec3 direction = Vec3.directionFromRotation(0.0F, yaw);
        drivePsychosisMovement(player, new Vec3(direction.x, 0.0D, direction.z), 0.82D, false);
    }

    private static void drivePsychosisMovement(Player player, Vec3 direction, double speedMultiplier, boolean forceJump) {
        Vec3 horizontal = new Vec3(direction.x, 0.0D, direction.z);
        if (horizontal.lengthSqr() < 0.0001D) {
            return;
        }

        Vec3 normalized = horizontal.normalize();
        double moveSpeed = Math.max(0.16D, player.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedMultiplier);
        Vec3 desiredMove = normalized.scale(moveSpeed);
        Level level = player.level();
        Vec3 currentPos = player.position();
        Vec3 nextPos = currentPos.add(desiredMove);

        player.setSprinting(true);
        if (level.noCollision(player, player.getBoundingBox().move(desiredMove))) {
            player.moveTo(nextPos.x, nextPos.y, nextPos.z, player.getYRot(), player.getXRot());
        } else {
            Vec3 jumpMove = new Vec3(desiredMove.x, 0.6D, desiredMove.z);
            if (player.onGround() && (forceJump || level.noCollision(player, player.getBoundingBox().move(jumpMove)))) {
                player.moveTo(currentPos.x + desiredMove.x, currentPos.y + 0.6D, currentPos.z + desiredMove.z, player.getYRot(), player.getXRot());
            }
        }

        player.setDeltaMovement(desiredMove.x, player.getDeltaMovement().y, desiredMove.z);
        player.hasImpulse = true;
    }

    private static int getSuppressionAmount(Player player, long gameTime) {
        cleanupExpiredState(player, gameTime);
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(SUPPRESSION_UNTIL_KEY) && persistentData.getLong(SUPPRESSION_UNTIL_KEY) > gameTime) {
            return persistentData.getInt(SUPPRESSION_VALUE_KEY);
        }
        return 0;
    }

    private static void cleanupExpiredState(Player player, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(SUPPRESSION_UNTIL_KEY) && persistentData.getLong(SUPPRESSION_UNTIL_KEY) <= gameTime) {
            persistentData.remove(SUPPRESSION_UNTIL_KEY);
            persistentData.remove(SUPPRESSION_VALUE_KEY);
        }
        if (persistentData.contains(RAM_JOLT_UNTIL_KEY) && persistentData.getLong(RAM_JOLT_UNTIL_KEY) <= gameTime) {
            persistentData.remove(RAM_JOLT_UNTIL_KEY);
            persistentData.remove(RAM_JOLT_MULTIPLIER_KEY);
        }
        if (persistentData.contains(PSYCHOSIS_UNTIL_KEY) && persistentData.getLong(PSYCHOSIS_UNTIL_KEY) <= gameTime) {
            persistentData.remove(PSYCHOSIS_UNTIL_KEY);
            persistentData.remove(PSYCHOSIS_LAST_ATTACK_KEY);
            persistentData.remove(PSYCHOSIS_WANDER_YAW_KEY);
            if (player instanceof ServerPlayer serverPlayer) {
                player.displayClientMessage(Component.translatable("message.cyberneticenhancements.psychosis.ended"), true);
                syncControlLock(serverPlayer);
            }
        }
    }

    private static void merge(EnumMap<CyberwareEffectType, Double> totals, CyberwareEffectType type, double amount) {
        totals.merge(type, amount, type.isMobEffect() ? Math::max : Double::sum);
    }

    private static void copyLong(CompoundTag fromData, CompoundTag toData, String key) {
        if (fromData.contains(key)) {
            toData.putLong(key, fromData.getLong(key));
        } else {
            toData.remove(key);
        }
    }

    private static void copyInt(CompoundTag fromData, CompoundTag toData, String key) {
        if (fromData.contains(key)) {
            toData.putInt(key, fromData.getInt(key));
        } else {
            toData.remove(key);
        }
    }

    private static void copyDouble(CompoundTag fromData, CompoundTag toData, String key) {
        if (fromData.contains(key)) {
            toData.putDouble(key, fromData.getDouble(key));
        } else {
            toData.remove(key);
        }
    }

    private static void copyFloat(CompoundTag fromData, CompoundTag toData, String key) {
        if (fromData.contains(key)) {
            toData.putFloat(key, fromData.getFloat(key));
        } else {
            toData.remove(key);
        }
    }
}
