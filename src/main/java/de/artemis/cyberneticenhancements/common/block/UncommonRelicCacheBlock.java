package de.artemis.cyberneticenhancements.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public final class UncommonRelicCacheBlock extends RelicCacheBlock {
    public static final MapCodec<UncommonRelicCacheBlock> CODEC = simpleCodec(UncommonRelicCacheBlock::new);

    public UncommonRelicCacheBlock(BlockBehaviour.Properties properties) {
        super(properties, RelicCacheTier.UNCOMMON);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
