package de.artemis.cyberneticenhancements.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public final class EpicRelicCacheBlock extends RelicCacheBlock {
    public static final MapCodec<EpicRelicCacheBlock> CODEC = simpleCodec(EpicRelicCacheBlock::new);

    public EpicRelicCacheBlock(BlockBehaviour.Properties properties) {
        super(properties, RelicCacheTier.EPIC);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
