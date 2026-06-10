package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.common.cyberware.ArmCyberwareManager;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareAbilities;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlot;
import de.artemis.cyberneticenhancements.common.cyberware.CyberpsychosisClientState;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleHandler;
import de.artemis.cyberneticenhancements.common.cyberware.FaceCyberwareManager;
import de.artemis.cyberneticenhancements.common.cyberware.FrontalCortexManager;
import de.artemis.cyberneticenhancements.common.cyberware.LegCyberwareManager;
import de.artemis.cyberneticenhancements.client.CyberwareHudClientState;
import de.artemis.cyberneticenhancements.client.FaceHazardHighlightClientState;
import de.artemis.cyberneticenhancements.client.PsychosisOverlayClientState;
import de.artemis.cyberneticenhancements.client.PlayerMotionSyncClient;
import de.artemis.cyberneticenhancements.common.menu.RecyclerStationMenu;
import de.artemis.cyberneticenhancements.common.menu.RipperStationMenu;
import de.artemis.cyberneticenhancements.common.menu.TechStationMenu;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModPayloads {
    private ModPayloads() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(CyberpsychosisControlPayload.TYPE, CyberpsychosisControlPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> CyberpsychosisClientState.apply(
                                payload.locked(),
                                payload.yaw(),
                                payload.pitch(),
                                payload.forward(),
                                payload.strafe(),
                                payload.jump(),
                                payload.sprint())))
                .playToClient(CyberwareHudPayload.TYPE, CyberwareHudPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> CyberwareHudClientState.update(payload)))
                .playToClient(PsychosisOverlayPayload.TYPE, PsychosisOverlayPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> PsychosisOverlayClientState.update(payload)))
                .playToClient(FaceHazardHighlightPayload.TYPE, FaceHazardHighlightPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> FaceHazardHighlightClientState.update(payload)))
                .playToClient(PlayerMotionSyncPayload.TYPE, PlayerMotionSyncPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> PlayerMotionSyncClient.apply(payload)))
                .playToServer(ActivateCyberwarePayload.TYPE, ActivateCyberwarePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> CyberwareAbilities.activate(context.player())))
                .playToServer(ActivateAuxiliaryCyberwarePayload.TYPE, ActivateAuxiliaryCyberwarePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> FrontalCortexManager.activateAuxiliary(context.player())))
                .playToServer(ActivateArmCyberwarePayload.TYPE, ActivateArmCyberwarePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> ArmCyberwareManager.activate(context.player())))
                .playToServer(ActivateFaceCyberwarePayload.TYPE, ActivateFaceCyberwarePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> FaceCyberwareManager.activate(context.player())))
                .playToServer(ActivateLegCyberwarePayload.TYPE, ActivateLegCyberwarePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> LegCyberwareManager.activateMidairJump(context.player())))
                .playToServer(UpgradeCyberwareSlotPayload.TYPE, UpgradeCyberwareSlotPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (payload.slotOrdinal() < 0 || payload.slotOrdinal() >= CyberwareSlot.values().length) {
                                return;
                            }
                            if (context.player().containerMenu instanceof RipperStationMenu menu) {
                                menu.tryUpgradeSupportedTier(CyberwareSlot.values()[payload.slotOrdinal()]);
                            }
                        }))
                .playToServer(UpgradeRipperSubSlotPayload.TYPE, UpgradeRipperSubSlotPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (!(context.player().containerMenu instanceof RipperStationMenu menu) || payload.slotIndex() < 0) {
                                return;
                            }

                            switch (payload.kind()) {
                                case UpgradeRipperSubSlotPayload.KIND_CHIPWARE -> {
                                    if (payload.handlerIndex() >= 0 && payload.handlerIndex() < 3 && payload.slotIndex() < 3) {
                                        menu.tryUpgradeChipSupportedTier(payload.handlerIndex(), payload.slotIndex());
                                    }
                                }
                                case UpgradeRipperSubSlotPayload.KIND_ARM_MODULE -> {
                                    if (payload.slotIndex() < CyberwareModuleHandler.MAX_MODULE_SLOTS) {
                                        menu.tryUpgradeArmModuleSupportedTier(payload.slotIndex());
                                    }
                                }
                                case UpgradeRipperSubSlotPayload.KIND_LEG_MODULE -> {
                                    if (payload.slotIndex() < CyberwareModuleHandler.MAX_MODULE_SLOTS) {
                                        menu.tryUpgradeLegModuleSupportedTier(payload.slotIndex());
                                    }
                                }
                                default -> {
                                }
                            }
                        }))
                .playToServer(UpgradeStationInputSlotPayload.TYPE, UpgradeStationInputSlotPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (payload.slotIndex() < 0) {
                                return;
                            }
                            if (context.player().containerMenu instanceof TechStationMenu techStationMenu) {
                                switch (payload.slotIndex()) {
                                    case 0 -> techStationMenu.tryUpgradeRepairSupportedTier();
                                    case 1 -> techStationMenu.tryUpgradeUpgradeSupportedTier();
                                    default -> {
                                    }
                                }
                                return;
                            }
                            if (context.player().containerMenu instanceof RecyclerStationMenu recyclerStationMenu && payload.slotIndex() == 0) {
                                recyclerStationMenu.tryUpgradeSupportedTier();
                            }
                        }));
    }
}
