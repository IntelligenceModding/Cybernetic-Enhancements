package de.artemis.cyberneticenhancements.common.datagen;

import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public final class ModBlockLootTableProvider extends BlockLootSubProvider {
    private static final Set<Block> DIRECT_BLOCKS = Set.of(
            ModBlocks.RIPPER_STATION.get(),
            ModBlocks.TECH_STATION.get(),
            ModBlocks.RECYCLER_STATION.get()
    );

    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        DIRECT_BLOCKS.forEach(this::dropSelf);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return List.copyOf(DIRECT_BLOCKS);
    }
}
