package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class CyberwarePlayerEvents {
    private CyberwarePlayerEvents() {
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerCyberwareInventory.copyStoredInventory(event.getOriginal(), event.getEntity());
        CyberstrainManager.copyState(event.getOriginal(), event.getEntity());
        CyberConsumableManager.copyState(event.getOriginal(), event.getEntity());
        TemporaryCyberwareEffectManager.copyState(event.getOriginal(), event.getEntity());
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CyberwareEffects.refreshPlayerCyberware(serverPlayer);
            CyberstrainManager.syncControlLock(serverPlayer);
            PsychosisBossBarManager.onPlayerLogin(serverPlayer);
            PsychosisBossBarManager.update(serverPlayer);
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CyberwareEffects.refreshPlayerCyberware(serverPlayer);
            CyberstrainManager.syncControlLock(serverPlayer);
            PsychosisBossBarManager.onPlayerLogin(serverPlayer);
            PsychosisBossBarManager.update(serverPlayer);
        }
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CyberwareEffects.refreshPlayerCyberware(serverPlayer);
            CyberstrainManager.syncControlLock(serverPlayer);
            PsychosisBossBarManager.onPlayerLogin(serverPlayer);
            PsychosisBossBarManager.update(serverPlayer);
        }
    }

    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PsychosisBossBarManager.onPlayerLogout(serverPlayer);
        }
    }
}
