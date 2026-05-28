package de.artemis.cyberneticenhancements.common.registry;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CyberneticEnhancements.MOD_ID);

    @SuppressWarnings("unused")
    public static final Supplier<CreativeModeTab> CYBERNETIC_ENHANCEMENTS_CREATIVE_TAB = CREATIVE_MODE_TAB.register(
            "cybernetic_enhancements_creative_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.COPPER_WIRING.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.cyberneticenhancements"))
                    .displayItems((parameters, output) -> {
                        ModItems.MATERIALS.forEach(item -> output.accept(item.get()));
                        ModItems.consumableItems().forEach(item -> output.accept(item.get()));
                        output.accept(ModBlocks.RIPPER_STATION.get());
                        output.accept(ModBlocks.TECHSTATION.get());
                        output.accept(ModBlocks.RECYCLER_STATION.get());
                    })
                    .build()
    );

    @SuppressWarnings("unused")
    public static final Supplier<CreativeModeTab> CYBERWARE_CREATIVE_TAB = CREATIVE_MODE_TAB.register(
            "cyberware_creative_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.cyberware("basic_kiroshi_optics").get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.cyberneticenhancements.cyberware"))
                    .displayItems((parameters, output) -> {
                        ModItems.chipwareItems().forEach(item -> output.accept(item.get()));
                        ModItems.moduleItems().forEach(item -> output.accept(item.get()));
                        ModItems.cyberwareItems().forEach(item -> output.accept(item.get()));
                    })
                    .build()
    );

    private ModCreativeModeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
