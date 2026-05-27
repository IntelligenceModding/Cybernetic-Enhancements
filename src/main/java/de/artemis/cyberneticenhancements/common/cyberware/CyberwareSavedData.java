package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CyberwareSavedData extends SavedData {
    private static final String DATA_NAME = CyberneticEnhancements.MOD_ID + "_player_cyberware";
    private static final Factory<CyberwareSavedData> FACTORY =
            new Factory<>(CyberwareSavedData::new, CyberwareSavedData::load, null);

    private final Map<UUID, CompoundTag> playerInventories = new HashMap<>();

    public static CyberwareSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static CyberwareSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        CyberwareSavedData data = new CyberwareSavedData();
        ListTag playersTag = tag.getList("players", Tag.TAG_COMPOUND);
        for (Tag playerTag : playersTag) {
            if (!(playerTag instanceof CompoundTag compoundTag) || !compoundTag.hasUUID("uuid")) {
                continue;
            }
            data.playerInventories.put(compoundTag.getUUID("uuid"), compoundTag.getCompound("inventory").copy());
        }
        return data;
    }

    public CompoundTag getInventory(UUID playerId) {
        CompoundTag stored = playerInventories.get(playerId);
        return stored == null ? new CompoundTag() : stored.copy();
    }

    public void setInventory(UUID playerId, CompoundTag inventoryTag) {
        if (inventoryTag.isEmpty()) {
            playerInventories.remove(playerId);
        } else {
            playerInventories.put(playerId, inventoryTag.copy());
        }
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag playersTag = new ListTag();
        for (Map.Entry<UUID, CompoundTag> entry : playerInventories.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("uuid", entry.getKey());
            playerTag.put("inventory", entry.getValue().copy());
            playersTag.add(playerTag);
        }
        tag.put("players", playersTag);
        return tag;
    }
}
