package de.artemis.cyberneticenhancements.common.blockentity;

import de.artemis.cyberneticenhancements.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class TechStationBlockEntity extends UpgradeableStationBlockEntity {
    public static final int REPAIR_INPUT_SLOT = 0;
    public static final int UPGRADE_INPUT_SLOT = 1;

    public TechStationBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TECH_STATION.get(), pos, blockState, 2);
    }
}
