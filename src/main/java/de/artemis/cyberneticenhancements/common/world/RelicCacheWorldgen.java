package de.artemis.cyberneticenhancements.common.world;

import de.artemis.cyberneticenhancements.common.block.RelicCacheBlock;
import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RelicCacheWorldgen {
    private static final Map<ResourceLocation, PlacementRule> RULES = Map.ofEntries(
            Map.entry(ResourceLocation.withDefaultNamespace("igloo"), new PlacementRule(ModBlocks.UNCOMMON_RELIC_CACHE.get())),
            Map.entry(ResourceLocation.withDefaultNamespace("jungle_temple"), new PlacementRule(ModBlocks.UNCOMMON_RELIC_CACHE.get())),
            Map.entry(ResourceLocation.withDefaultNamespace("pillager_outpost"), new PlacementRule(ModBlocks.UNCOMMON_RELIC_CACHE.get())),
            Map.entry(ResourceLocation.withDefaultNamespace("desert_pyramid"), new PlacementRule(ModBlocks.RARE_RELIC_CACHE.get())),
            Map.entry(ResourceLocation.withDefaultNamespace("woodland_mansion"), new PlacementRule(ModBlocks.RARE_RELIC_CACHE.get())),
            Map.entry(ResourceLocation.withDefaultNamespace("stronghold"), new PlacementRule(ModBlocks.RARE_RELIC_CACHE.get())),
            Map.entry(ResourceLocation.withDefaultNamespace("trial_chambers"), new PlacementRule(ModBlocks.EPIC_RELIC_CACHE.get())),
            Map.entry(ResourceLocation.withDefaultNamespace("ancient_city"), new PlacementRule(ModBlocks.EPIC_RELIC_CACHE.get())),
            Map.entry(ResourceLocation.withDefaultNamespace("bastion_remnant"), new PlacementRule(ModBlocks.EPIC_RELIC_CACHE.get())),
            Map.entry(ResourceLocation.withDefaultNamespace("end_city"), new PlacementRule(ModBlocks.LEGENDARY_RELIC_CACHE.get()))
    );
    private static final Map<ResourceLocation, List<LevelChunk>> PENDING_CHUNKS = new HashMap<>();

    private RelicCacheWorldgen() {
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.isNewChunk() || !(event.getLevel() instanceof ServerLevel serverLevel) || !(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }
        PENDING_CHUNKS.computeIfAbsent(serverLevel.dimension().location(), ignored -> new ArrayList<>()).add(chunk);
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        List<LevelChunk> pending = PENDING_CHUNKS.get(serverLevel.dimension().location());
        if (pending == null || pending.isEmpty()) {
            return;
        }

        List<LevelChunk> deferred = new ArrayList<>();
        for (LevelChunk chunk : pending) {
            if (!chunk.getFullStatus().isOrAfter(FullChunkStatus.FULL)) {
                deferred.add(chunk);
                continue;
            }
            tryPlaceCaches(serverLevel, chunk);
        }

        if (deferred.isEmpty()) {
            PENDING_CHUNKS.remove(serverLevel.dimension().location());
        } else {
            PENDING_CHUNKS.put(serverLevel.dimension().location(), deferred);
        }
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            PENDING_CHUNKS.remove(serverLevel.dimension().location());
        }
    }

    private static void tryPlaceCaches(ServerLevel level, LevelChunk chunk) {
        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        RelicCacheStructurePlacementSavedData savedData = RelicCacheStructurePlacementSavedData.get(level);
        ChunkPos chunkPos = chunk.getPos();

        for (StructureStart start : level.structureManager().startsForStructure(chunkPos, structure -> true)) {
            ResourceLocation structureId = structureRegistry.getKey(start.getStructure());
            if (structureId == null) {
                continue;
            }

            PlacementRule rule = RULES.get(structureId);
            if (rule == null) {
                continue;
            }

            String placementKey = structurePlacementKey(structureId, start);
            if (savedData.isPlaced(placementKey)) {
                continue;
            }

            BlockPos placementPos = findPlacementCandidate(level, chunk, start);
            if (placementPos == null) {
                continue;
            }

            placeCache(level, placementPos, rule.block());
            savedData.markPlaced(placementKey);
        }
    }

    private static BlockPos findPlacementCandidate(ServerLevel level, LevelChunk chunk, StructureStart start) {
        BoundingBox box = start.getBoundingBox();
        BlockPos center = box.getCenter();

        return chunk.getBlockEntitiesPos().stream()
                .filter(box::isInside)
                .filter(pos -> level.structureManager().structureHasPieceAt(pos, start))
                .filter(pos -> isSupportedContainer(level.getBlockState(pos)))
                .min(Comparator
                        .comparingInt((BlockPos pos) -> horizontalDistanceSquared(pos, center))
                        .thenComparingInt(BlockPos::getY))
                .orElse(null);
    }

    private static void placeCache(ServerLevel level, BlockPos pos, RelicCacheBlock cacheBlock) {
        BlockState oldState = level.getBlockState(pos);
        Direction facing = oldState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? oldState.getValue(BlockStateProperties.HORIZONTAL_FACING)
                : Direction.NORTH;
        BlockState cacheState = cacheBlock.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.setRemoved();
        }
        level.setBlock(pos, cacheState, Block.UPDATE_ALL);
    }

    private static boolean isSupportedContainer(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL;
    }

    private static int horizontalDistanceSquared(BlockPos a, BlockPos b) {
        int dx = a.getX() - b.getX();
        int dz = a.getZ() - b.getZ();
        return dx * dx + dz * dz;
    }

    private static String structurePlacementKey(ResourceLocation structureId, StructureStart start) {
        ChunkPos chunkPos = start.getChunkPos();
        return structureId + "|" + chunkPos.x + "|" + chunkPos.z;
    }

    private record PlacementRule(RelicCacheBlock block) {
    }
}
