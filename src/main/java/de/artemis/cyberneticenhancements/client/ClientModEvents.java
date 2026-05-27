package de.artemis.cyberneticenhancements.client;

import de.artemis.cyberneticenhancements.client.screen.RecyclerStationScreen;
import de.artemis.cyberneticenhancements.client.screen.RipperStationScreen;
import de.artemis.cyberneticenhancements.client.screen.TechstationScreen;
import de.artemis.cyberneticenhancements.common.cyberware.CyberpsychosisClientState;
import de.artemis.cyberneticenhancements.common.network.ActivateCyberwarePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import de.artemis.cyberneticenhancements.common.registry.ModMenuTypes;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public final class ClientModEvents {
    private ClientModEvents() {
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.RIPPER_STATION.get(), RipperStationScreen::new);
        event.register(ModMenuTypes.TECHSTATION.get(), TechstationScreen::new);
        event.register(ModMenuTypes.RECYCLER_STATION.get(), RecyclerStationScreen::new);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        if (CyberpsychosisClientState.isControlLocked()) {
            releaseControlKeys(minecraft.options);
            if (minecraft.player.input != null) {
                minecraft.player.input.forwardImpulse = 0.0F;
                minecraft.player.input.leftImpulse = 0.0F;
                minecraft.player.input.up = false;
                minecraft.player.input.down = false;
                minecraft.player.input.left = false;
                minecraft.player.input.right = false;
                minecraft.player.input.jumping = false;
                minecraft.player.input.shiftKeyDown = false;
            }
            return;
        }

        while (ModKeyMappings.ACTIVATE_CYBERWARE.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateCyberwarePayload());
        }
    }

    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (!CyberpsychosisClientState.isControlLocked()) {
            return;
        }

        event.getInput().forwardImpulse = 0.0F;
        event.getInput().leftImpulse = 0.0F;
        event.getInput().up = false;
        event.getInput().down = false;
        event.getInput().left = false;
        event.getInput().right = false;
        event.getInput().jumping = false;
        event.getInput().shiftKeyDown = false;
    }

    public static void onInteractionInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (!CyberpsychosisClientState.isControlLocked()) {
            return;
        }

        event.setSwingHand(false);
        event.setCanceled(true);
    }

    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (CyberpsychosisClientState.isControlLocked()) {
            event.setCanceled(true);
        }
    }

    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        CyberpsychosisClientState.setControlLocked(false);
    }

    private static void releaseControlKeys(Options options) {
        options.keyUp.setDown(false);
        options.keyDown.setDown(false);
        options.keyLeft.setDown(false);
        options.keyRight.setDown(false);
        options.keyJump.setDown(false);
        options.keyShift.setDown(false);
        options.keySprint.setDown(false);
        options.keyAttack.setDown(false);
        options.keyUse.setDown(false);
    }
}
