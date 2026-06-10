package de.artemis.cyberneticenhancements.client;

import de.artemis.cyberneticenhancements.common.network.CyberwareHudPayload;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CyberwareHudClientState {
    private static CyberwareHudPayload snapshot;
    private static final Map<String, TimerState> TIMERS = new ConcurrentHashMap<>();

    private CyberwareHudClientState() {
    }

    public static void update(CyberwareHudPayload payload) {
        snapshot = payload;
        long now = System.currentTimeMillis();
        Set<String> seenKeys = new HashSet<>();

        for (CyberwareHudPayload.AbilityEntry entry : payload.abilities()) {
            updateTimer(seenKeys, abilityActiveKey(entry), entry.activeSeconds(), entry.activeTotalSeconds(), now);
            updateTimer(seenKeys, abilityCooldownKey(entry), entry.cooldownSeconds(), entry.cooldownTotalSeconds(), now);
        }
        for (CyberwareHudPayload.StatusEntry entry : payload.statuses()) {
            updateTimer(seenKeys, statusKey(entry), entry.remainingSeconds(), entry.totalSeconds(), now);
        }
        for (CyberwareHudPayload.CooldownEntry entry : payload.cooldowns()) {
            updateTimer(seenKeys, cooldownKey(entry), entry.remainingSeconds(), entry.totalSeconds(), now);
        }

        TIMERS.keySet().retainAll(seenKeys);
    }

    public static CyberwareHudPayload get() {
        return snapshot;
    }

    public static float getAbilityActiveRatio(CyberwareHudPayload.AbilityEntry entry) {
        return getTimerRatio(abilityActiveKey(entry), entry.activeSeconds(), entry.activeTotalSeconds());
    }

    public static float getAbilityCooldownRatio(CyberwareHudPayload.AbilityEntry entry) {
        return getTimerRatio(abilityCooldownKey(entry), entry.cooldownSeconds(), entry.cooldownTotalSeconds());
    }

    public static float getStatusRatio(CyberwareHudPayload.StatusEntry entry) {
        return getTimerRatio(statusKey(entry), entry.remainingSeconds(), entry.totalSeconds());
    }

    public static float getCooldownRatio(CyberwareHudPayload.CooldownEntry entry) {
        return getTimerRatio(cooldownKey(entry), entry.remainingSeconds(), entry.totalSeconds());
    }

    public static void clear() {
        snapshot = null;
        TIMERS.clear();
    }

    private static void updateTimer(Set<String> seenKeys, String key, int remainingSeconds, int totalSeconds, long now) {
        if (remainingSeconds <= 0 || totalSeconds <= 0) {
            TIMERS.remove(key);
            return;
        }

        seenKeys.add(key);
        TimerState existing = TIMERS.get(key);
        if (existing != null && existing.totalSeconds() == totalSeconds) {
            if (existing.reportedSeconds() == remainingSeconds) {
                return;
            }

            if (remainingSeconds < existing.reportedSeconds() && existing.endTimeMs() > now) {
                TIMERS.put(key, new TimerState(totalSeconds, remainingSeconds, existing.endTimeMs()));
                return;
            }
        }

        TIMERS.put(key, new TimerState(totalSeconds, remainingSeconds, now + remainingSeconds * 1000L));
    }

    private static float getTimerRatio(String key, int remainingSeconds, int totalSeconds) {
        if (totalSeconds <= 0) {
            return 0.0F;
        }

        TimerState timer = TIMERS.get(key);
        if (timer == null) {
            return clampRatio((float) remainingSeconds / (float) totalSeconds);
        }

        long remainingMs = Math.max(0L, timer.endTimeMs() - System.currentTimeMillis());
        return clampRatio(remainingMs / (timer.totalSeconds() * 1000.0F));
    }

    private static float clampRatio(float ratio) {
        return Math.max(0.0F, Math.min(1.0F, ratio));
    }

    private static String abilityActiveKey(CyberwareHudPayload.AbilityEntry entry) {
        return "ability_active:" + entry.translationKey();
    }

    private static String abilityCooldownKey(CyberwareHudPayload.AbilityEntry entry) {
        return "ability_cooldown:" + entry.translationKey();
    }

    private static String statusKey(CyberwareHudPayload.StatusEntry entry) {
        return "status:" + entry.translationKey() + ":" + entry.playerApplied();
    }

    private static String cooldownKey(CyberwareHudPayload.CooldownEntry entry) {
        return "cooldown:" + entry.translationKey();
    }

    private record TimerState(int totalSeconds, int reportedSeconds, long endTimeMs) {
    }
}
