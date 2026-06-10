package de.artemis.cyberneticenhancements.client;

import de.artemis.cyberneticenhancements.common.network.FaceHazardHighlightPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class FaceHazardHighlightClientState {
    private static final Map<BlockPos, Long> BLOCK_HIGHLIGHTS = new LinkedHashMap<>();
    private static final Map<UUID, Long> ENTITY_HIGHLIGHTS = new LinkedHashMap<>();

    private FaceHazardHighlightClientState() {
    }

    public static void update(FaceHazardHighlightPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        long expiry = minecraft.level.getGameTime() + Math.max(1, payload.ttlTicks());
        for (BlockPos pos : payload.positions()) {
            BLOCK_HIGHLIGHTS.put(pos.immutable(), expiry);
        }
        for (UUID entityId : payload.entityIds()) {
            ENTITY_HIGHLIGHTS.put(entityId, expiry);
        }
    }

    public static Map<BlockPos, Long> activeBlockHighlights() {
        return BLOCK_HIGHLIGHTS;
    }

    public static Map<UUID, Long> activeEntityHighlights() {
        return ENTITY_HIGHLIGHTS;
    }

    public static void prune() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            BLOCK_HIGHLIGHTS.clear();
            ENTITY_HIGHLIGHTS.clear();
            return;
        }

        long now = minecraft.level.getGameTime();
        BLOCK_HIGHLIGHTS.entrySet().removeIf(entry -> entry.getValue() <= now);
        ENTITY_HIGHLIGHTS.entrySet().removeIf(entry -> entry.getValue() <= now);
    }

    public static void clear() {
        BLOCK_HIGHLIGHTS.clear();
        ENTITY_HIGHLIGHTS.clear();
    }
}
