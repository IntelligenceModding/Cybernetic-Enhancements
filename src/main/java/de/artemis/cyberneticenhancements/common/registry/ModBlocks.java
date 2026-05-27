package de.artemis.cyberneticenhancements.common.registry;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.block.RecyclerStationBlock;
import de.artemis.cyberneticenhancements.common.block.RipperStationBlock;
import de.artemis.cyberneticenhancements.common.block.TechstationBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CyberneticEnhancements.MOD_ID);

    public static final DeferredBlock<RipperStationBlock> RIPPER_STATION = register(
            "ripper_station",
            RipperStationBlock::new,
            properties -> properties.strength(3.5F).sound(SoundType.METAL)
    );

    public static final DeferredBlock<TechstationBlock> TECHSTATION = register(
            "techstation",
            TechstationBlock::new,
            properties -> properties.strength(3.5F).sound(SoundType.METAL)
    );

    public static final DeferredBlock<RecyclerStationBlock> RECYCLER_STATION = register(
            "recycler_station",
            RecyclerStationBlock::new,
            properties -> properties.strength(3.5F).sound(SoundType.METAL)
    );

    private ModBlocks() {
    }

    private static <T extends Block> DeferredBlock<T> register(
            String name,
            Function<BlockBehaviour.Properties, T> blockFactory,
            UnaryOperator<BlockBehaviour.Properties> properties
    ) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, blockFactory, properties.apply(BlockBehaviour.Properties.of()));
        ModItems.ITEMS.registerSimpleBlockItem(block, new Item.Properties());
        return block;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
