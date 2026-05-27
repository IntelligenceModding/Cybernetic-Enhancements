package de.artemis.cyberneticenhancements;

import de.artemis.cyberneticenhancements.common.command.CyberwareDebugCommand;
import de.artemis.cyberneticenhancements.common.command.CyberpsychosisCommand;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffects;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwarePlayerEvents;
import de.artemis.cyberneticenhancements.common.cyberware.CyberstrainManager;
import de.artemis.cyberneticenhancements.common.datagen.DataGenerators;
import de.artemis.cyberneticenhancements.common.network.ModPayloads;
import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import de.artemis.cyberneticenhancements.common.registry.ModCreativeModeTabs;
import de.artemis.cyberneticenhancements.common.registry.ModItems;
import de.artemis.cyberneticenhancements.common.registry.ModMenuTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(CyberneticEnhancements.MOD_ID)
public class CyberneticEnhancements {
    public static final String MOD_ID = "cyberneticenhancements";

    public CyberneticEnhancements(IEventBus modEventBus) {
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        modEventBus.addListener(DataGenerators::gatherData);
        modEventBus.addListener(ModPayloads::register);
        NeoForge.EVENT_BUS.addListener(CyberwareEffects::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(CyberwarePlayerEvents::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(CyberwarePlayerEvents::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(CyberwarePlayerEvents::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(CyberstrainManager::onLivingHeal);
        NeoForge.EVENT_BUS.addListener(CyberwareEffects::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(CyberneticEnhancements::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        CyberwareDebugCommand.register(event.getDispatcher());
        CyberpsychosisCommand.register(event.getDispatcher());
    }
}
