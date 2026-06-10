package de.artemis.cyberneticenhancements.common.menu;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerStationUpgradeData {
    private static final String DATA_NAME = CyberneticEnhancements.MOD_ID + "_player_station_upgrades";
    private static final String TECH_REPAIR_TIER_KEY = "TechRepairTier";
    private static final String TECH_UPGRADE_TIER_KEY = "TechUpgradeTier";
    private static final String RECYCLER_TIER_KEY = "RecyclerTier";
    private static final SavedData.Factory<StoredData> FACTORY =
            new SavedData.Factory<>(StoredData::new, StoredData::load, null);

    private PlayerStationUpgradeData() {
    }

    public static CyberwareTier getTechRepairTier(Player player) {
        return getTier(player, TECH_REPAIR_TIER_KEY);
    }

    public static CyberwareTier getTechUpgradeTier(Player player) {
        return getTier(player, TECH_UPGRADE_TIER_KEY);
    }

    public static CyberwareTier getRecyclerTier(Player player) {
        return getTier(player, RECYCLER_TIER_KEY);
    }

    public static boolean tryUpgradeTechRepairTier(Player player) {
        return tryUpgrade(player, TECH_REPAIR_TIER_KEY);
    }

    public static boolean tryUpgradeTechUpgradeTier(Player player) {
        return tryUpgrade(player, TECH_UPGRADE_TIER_KEY);
    }

    public static boolean tryUpgradeRecyclerTier(Player player) {
        return tryUpgrade(player, RECYCLER_TIER_KEY);
    }

    public static void migrateTechRepairTier(Player player, int legacyOrdinal) {
        migrateTier(player, TECH_REPAIR_TIER_KEY, legacyOrdinal);
    }

    public static void migrateTechUpgradeTier(Player player, int legacyOrdinal) {
        migrateTier(player, TECH_UPGRADE_TIER_KEY, legacyOrdinal);
    }

    public static void migrateRecyclerTier(Player player, int legacyOrdinal) {
        migrateTier(player, RECYCLER_TIER_KEY, legacyOrdinal);
    }

    private static CyberwareTier getTier(Player player, String key) {
        CompoundTag tag = getPlayerTag(player);
        return toTier(tag.getInt(key));
    }

    private static boolean tryUpgrade(Player player, String key) {
        if (!(player.level() instanceof ServerLevel)) {
            return false;
        }

        CompoundTag tag = getPlayerTag(player);
        CyberwareTier current = toTier(tag.getInt(key));
        if (current == CyberwareTier.TIER_5) {
            return false;
        }

        tag.putInt(key, current.ordinal() + 1);
        setPlayerTag(player, tag);
        return true;
    }

    private static void migrateTier(Player player, String key, int legacyOrdinal) {
        if (!(player.level() instanceof ServerLevel)) {
            return;
        }

        CyberwareTier legacyTier = toTier(legacyOrdinal);
        CompoundTag tag = getPlayerTag(player);
        CyberwareTier current = toTier(tag.getInt(key));
        if (legacyTier.ordinal() <= current.ordinal()) {
            return;
        }

        tag.putInt(key, legacyTier.ordinal());
        setPlayerTag(player, tag);
    }

    private static CompoundTag getPlayerTag(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return new CompoundTag();
        }
        return get(serverLevel).getPlayerUpgrades(player.getUUID());
    }

    private static void setPlayerTag(Player player, CompoundTag tag) {
        if (player.level() instanceof ServerLevel serverLevel) {
            get(serverLevel).setPlayerUpgrades(player.getUUID(), tag);
        }
    }

    private static StoredData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static CyberwareTier toTier(int ordinal) {
        return ordinal >= 0 && ordinal < CyberwareTier.values().length ? CyberwareTier.values()[ordinal] : CyberwareTier.TIER_1;
    }

    private static final class StoredData extends SavedData {
        private final Map<UUID, CompoundTag> playerUpgrades = new HashMap<>();

        private static StoredData load(CompoundTag tag, HolderLookup.Provider registries) {
            StoredData data = new StoredData();
            ListTag playersTag = tag.getList("players", Tag.TAG_COMPOUND);
            for (Tag playerTag : playersTag) {
                if (!(playerTag instanceof CompoundTag compoundTag) || !compoundTag.hasUUID("uuid")) {
                    continue;
                }
                data.playerUpgrades.put(compoundTag.getUUID("uuid"), compoundTag.getCompound("upgrades").copy());
            }
            return data;
        }

        public CompoundTag getPlayerUpgrades(UUID playerId) {
            CompoundTag stored = playerUpgrades.get(playerId);
            return stored == null ? new CompoundTag() : stored.copy();
        }

        public void setPlayerUpgrades(UUID playerId, CompoundTag tag) {
            CompoundTag normalized = new CompoundTag();
            copyIfPresent(tag, normalized, TECH_REPAIR_TIER_KEY);
            copyIfPresent(tag, normalized, TECH_UPGRADE_TIER_KEY);
            copyIfPresent(tag, normalized, RECYCLER_TIER_KEY);
            if (normalized.isEmpty()) {
                playerUpgrades.remove(playerId);
            } else {
                playerUpgrades.put(playerId, normalized);
            }
            setDirty();
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            ListTag playersTag = new ListTag();
            for (Map.Entry<UUID, CompoundTag> entry : playerUpgrades.entrySet()) {
                CompoundTag playerTag = new CompoundTag();
                playerTag.putUUID("uuid", entry.getKey());
                playerTag.put("upgrades", entry.getValue().copy());
                playersTag.add(playerTag);
            }
            tag.put("players", playersTag);
            return tag;
        }

        private static void copyIfPresent(CompoundTag from, CompoundTag to, String key) {
            if (from.contains(key, Tag.TAG_INT)) {
                to.putInt(key, from.getInt(key));
            }
        }
    }
}
