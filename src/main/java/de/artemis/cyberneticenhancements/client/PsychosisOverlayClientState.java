package de.artemis.cyberneticenhancements.client;

import de.artemis.cyberneticenhancements.common.network.PsychosisOverlayPayload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PsychosisOverlayClientState {
    private static PsychosisOverlayPayload snapshot = new PsychosisOverlayPayload(java.util.List.of());
    private static final Map<String, TimerState> TIMERS = new HashMap<>();

    private PsychosisOverlayClientState() {
    }

    public static void update(PsychosisOverlayPayload payload) {
        snapshot = payload;
        long now = System.currentTimeMillis();
        Set<String> seenKeys = new HashSet<>();
        for (PsychosisOverlayPayload.Entry entry : payload.entries()) {
            String key = timerKey(entry);
            seenKeys.add(key);
            if (entry.remainingSeconds() <= 0 || entry.totalSeconds() <= 0) {
                TIMERS.remove(key);
                continue;
            }

            TimerState existing = TIMERS.get(key);
            if (existing != null && existing.totalSeconds() == entry.totalSeconds()) {
                if (existing.reportedSeconds() == entry.remainingSeconds()) {
                    continue;
                }
                if (entry.remainingSeconds() < existing.reportedSeconds() && existing.endTimeMs() > now) {
                    TIMERS.put(key, new TimerState(entry.totalSeconds(), entry.remainingSeconds(), existing.endTimeMs()));
                    continue;
                }
            }
            TIMERS.put(key, new TimerState(entry.totalSeconds(), entry.remainingSeconds(), now + entry.remainingSeconds() * 1000L));
        }
        TIMERS.keySet().retainAll(seenKeys);
    }

    public static PsychosisOverlayPayload get() {
        return snapshot;
    }

    public static float getRatio(PsychosisOverlayPayload.Entry entry) {
        TimerState timer = TIMERS.get(timerKey(entry));
        if (timer == null || timer.totalSeconds() <= 0) {
            return clamp((float) entry.remainingSeconds() / Math.max(1, entry.totalSeconds()));
        }

        long remainingMs = Math.max(0L, timer.endTimeMs() - System.currentTimeMillis());
        return clamp(remainingMs / (timer.totalSeconds() * 1000.0F));
    }

    public static void clear() {
        snapshot = new PsychosisOverlayPayload(java.util.List.of());
        TIMERS.clear();
    }

    private static float clamp(float ratio) {
        return Math.max(0.0F, Math.min(1.0F, ratio));
    }

    private static String timerKey(PsychosisOverlayPayload.Entry entry) {
        return entry.playerName() + ":" + entry.tier();
    }

    private record TimerState(int totalSeconds, int reportedSeconds, long endTimeMs) {
    }
}
