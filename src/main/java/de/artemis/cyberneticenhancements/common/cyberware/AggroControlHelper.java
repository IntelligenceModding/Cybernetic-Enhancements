package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class AggroControlHelper {
    private AggroControlHelper() {
    }

    public static void clearNearbyAggro(Player player, double radius, Predicate<Mob> shouldAffect) {
        clearNearbyAggro(player, radius, shouldAffect, 0.0D);
    }

    public static void clearNearbyAggro(Player player, double radius, Predicate<Mob> shouldAffect, double minDistanceSqr) {
        for (Mob mob : collectNearbyMobs(player, radius)) {
            if (!shouldAffect.test(mob) || mob.distanceToSqr(player) < minDistanceSqr) {
                continue;
            }
            clearMobAggro(mob, player);
        }
    }

    public static void scrambleNearbyAggro(
            Player player,
            double radius,
            Predicate<Mob> shouldAffect,
            double minDistanceSqr,
            BiFunction<Mob, List<Mob>, LivingEntity> redirectPicker
    ) {
        List<Mob> nearbyMobs = collectNearbyMobs(player, radius);
        for (Mob mob : nearbyMobs) {
            if (!shouldAffect.test(mob) || mob.distanceToSqr(player) < minDistanceSqr) {
                continue;
            }

            LivingEntity redirectedTarget = redirectPicker.apply(mob, nearbyMobs);
            resetPlayerAggroState(mob, player);
            mob.getNavigation().stop();
            if (redirectedTarget != null && redirectedTarget.isAlive() && redirectedTarget != player) {
                mob.setTarget(redirectedTarget);
                mob.getLookControl().setLookAt(redirectedTarget, 40.0F, 40.0F);
            } else {
                mob.setTarget(null);
                Vec3 offset = new Vec3(
                        (mob.getRandom().nextDouble() - 0.5D) * 6.0D,
                        0.0D,
                        (mob.getRandom().nextDouble() - 0.5D) * 6.0D
                );
                Vec3 lookPoint = mob.position().add(offset);
                mob.getLookControl().setLookAt(lookPoint.x, lookPoint.y + mob.getEyeHeight(), lookPoint.z, 40.0F, 40.0F);
            }
        }
    }

    public static boolean hasDirectAggro(Mob mob, Player player) {
        return mob.getTarget() == player
                || mob.getLastHurtByMob() == player
                || isNeutralAngryAt(mob, player);
    }

    public static boolean isNeutralAngryAt(Mob mob, Player player) {
        if (mob instanceof NeutralMob neutralMob) {
            UUID angerTarget = neutralMob.getPersistentAngerTarget();
            return angerTarget != null && angerTarget.equals(player.getUUID());
        }
        return false;
    }

    public static void clearMobAggro(Mob mob, Player player) {
        resetPlayerAggroState(mob, player);
        mob.setTarget(null);
        mob.getNavigation().stop();
    }

    private static List<Mob> collectNearbyMobs(Player player, double radius) {
        return new ArrayList<>(player.level().getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(radius),
                Mob::isAlive
        ));
    }

    private static void resetPlayerAggroState(Mob mob, Player player) {
        if (mob.getTarget() == player) {
            mob.setTarget(null);
        }
        if (mob.getLastHurtByMob() == player) {
            mob.setLastHurtByMob(null);
        }
        mob.setLastHurtByPlayer(null);
        if (mob instanceof NeutralMob neutralMob && isNeutralAngryAt(mob, player)) {
            neutralMob.setPersistentAngerTarget(null);
            neutralMob.setRemainingPersistentAngerTime(0);
        }
    }
}
