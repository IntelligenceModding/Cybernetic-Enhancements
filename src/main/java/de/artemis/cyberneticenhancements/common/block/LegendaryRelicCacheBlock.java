package de.artemis.cyberneticenhancements.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public final class LegendaryRelicCacheBlock extends RelicCacheBlock {
    public static final MapCodec<LegendaryRelicCacheBlock> CODEC = simpleCodec(LegendaryRelicCacheBlock::new);

    public LegendaryRelicCacheBlock(BlockBehaviour.Properties properties) {
        super(properties, RelicCacheTier.LEGENDARY);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
