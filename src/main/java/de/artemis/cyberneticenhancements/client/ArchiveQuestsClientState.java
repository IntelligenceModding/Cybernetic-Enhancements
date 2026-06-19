package de.artemis.cyberneticenhancements.client;

import de.artemis.cyberneticenhancements.common.network.ArchiveQuestsPayload;

import java.util.List;

public final class ArchiveQuestsClientState {
    private static ArchiveQuestsPayload snapshot = new ArchiveQuestsPayload(List.of());
    private static boolean loaded;

    private ArchiveQuestsClientState() {
    }

    public static void update(ArchiveQuestsPayload payload) {
        snapshot = payload;
        loaded = true;
    }

    public static ArchiveQuestsPayload get() {
        return snapshot;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void clear() {
        snapshot = new ArchiveQuestsPayload(List.of());
        loaded = false;
    }
}
