package de.artemis.cyberneticenhancements.common.registry;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.menu.RecyclerStationMenu;
import de.artemis.cyberneticenhancements.common.menu.RipperStationMenu;
import de.artemis.cyberneticenhancements.common.menu.TechStationMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, CyberneticEnhancements.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<RipperStationMenu>> RIPPER_STATION =
            MENU_TYPES.register("ripper_station", () -> IMenuTypeExtension.create(RipperStationMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<TechStationMenu>> TECH_STATION =
            MENU_TYPES.register("tech_station", () -> IMenuTypeExtension.create(TechStationMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<RecyclerStationMenu>> RECYCLER_STATION =
            MENU_TYPES.register("recycler_station", () -> IMenuTypeExtension.create(RecyclerStationMenu::new));

    private ModMenuTypes() {
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
