package de.artemis.cyberneticenhancements.client;

import de.artemis.cyberneticenhancements.common.network.CyberwareHudPayload;

public final class CyberwareHudClientState {
    private static CyberwareHudPayload snapshot;

    private CyberwareHudClientState() {
    }

    public static void update(CyberwareHudPayload payload) {
        snapshot = payload;
    }

    public static CyberwareHudPayload get() {
        return snapshot;
    }

    public static void clear() {
        snapshot = null;
    }
}
