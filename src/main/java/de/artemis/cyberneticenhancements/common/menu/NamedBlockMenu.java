package de.artemis.cyberneticenhancements.common.menu;

import net.minecraft.core.BlockPos;

public interface NamedBlockMenu {
    BlockPos getBlockPos();

    String getBlockDisplayName();
}
