package de.artemis.cyberneticenhancements.common.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public final class RelicCacheStructurePlacementSavedData extends SavedData {
    private static final String DATA_NAME = "cyberneticenhancements_relic_cache_structures";
    private static final String PLACED_STRUCTURES_TAG = "PlacedStructures";
    private static final Factory<RelicCacheStructurePlacementSavedData> FACTORY = new Factory<>(
            RelicCacheStructurePlacementSavedData::new,
            RelicCacheStructurePlacementSavedData::load,
            null
    );

    private final Set<String> placedStructures = new HashSet<>();

    public static RelicCacheStructurePlacementSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static RelicCacheStructurePlacementSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        RelicCacheStructurePlacementSavedData data = new RelicCacheStructurePlacementSavedData();
        ListTag placed = tag.getList(PLACED_STRUCTURES_TAG, Tag.TAG_STRING);
        for (Tag entry : placed) {
            data.placedStructures.add(entry.getAsString());
        }
        return data;
    }

    public boolean isPlaced(String structureKey) {
        return placedStructures.contains(structureKey);
    }

    public void markPlaced(String structureKey) {
        if (placedStructures.add(structureKey)) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag placed = new ListTag();
        for (String entry : placedStructures) {
            placed.add(StringTag.valueOf(entry));
        }
        tag.put(PLACED_STRUCTURES_TAG, placed);
        return tag;
    }
}
