package de.artemis.cyberneticenhancements.common.blockentity;

import de.artemis.cyberneticenhancements.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class RecyclerStationBlockEntity extends UpgradeableStationBlockEntity {
    public static final int INPUT_SLOT = 0;

    public RecyclerStationBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.RECYCLER_STATION.get(), pos, blockState, 1);
    }
}
