package de.artemis.cyberneticenhancements.client;

import de.artemis.cyberneticenhancements.common.network.ArchiveContactsPayload;

import java.util.List;

public final class ArchiveContactsClientState {
    private static ArchiveContactsPayload snapshot = new ArchiveContactsPayload(List.of());
    private static boolean loaded;

    private ArchiveContactsClientState() {
    }

    public static void update(ArchiveContactsPayload payload) {
        snapshot = payload;
        loaded = true;
    }

    public static ArchiveContactsPayload get() {
        return snapshot;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void clear() {
        snapshot = new ArchiveContactsPayload(List.of());
        loaded = false;
    }
}
