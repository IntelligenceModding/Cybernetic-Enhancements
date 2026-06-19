package de.artemis.cyberneticenhancements.common.block;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public enum RelicCacheTier {
    UNCOMMON("uncommon_relic_cache"),
    RARE("rare_relic_cache"),
    EPIC("epic_relic_cache"),
    LEGENDARY("legendary_relic_cache");

    private final ResourceKey<LootTable> lootTableKey;
    private final ResourceKey<LootTable> displayLootTableKey;

    RelicCacheTier(String name) {
        this.lootTableKey = ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "gameplay/" + name)
        );
        this.displayLootTableKey = ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "gameplay/" + name + "_display")
        );
    }

    public ResourceKey<LootTable> lootTableKey() {
        return lootTableKey;
    }

    public ResourceKey<LootTable> displayLootTableKey() {
        return displayLootTableKey;
    }
}
