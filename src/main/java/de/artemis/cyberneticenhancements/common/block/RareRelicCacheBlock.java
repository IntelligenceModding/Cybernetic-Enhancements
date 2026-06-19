package de.artemis.cyberneticenhancements.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public final class RareRelicCacheBlock extends RelicCacheBlock {
    public static final MapCodec<RareRelicCacheBlock> CODEC = simpleCodec(RareRelicCacheBlock::new);

    public RareRelicCacheBlock(BlockBehaviour.Properties properties) {
        super(properties, RelicCacheTier.RARE);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
