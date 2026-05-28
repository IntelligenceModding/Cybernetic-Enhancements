package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareAbilities;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlot;
import de.artemis.cyberneticenhancements.common.cyberware.CyberpsychosisClientState;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleHandler;
import de.artemis.cyberneticenhancements.common.menu.RipperStationMenu;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModPayloads {
    private ModPayloads() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(CyberpsychosisControlPayload.TYPE, CyberpsychosisControlPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> CyberpsychosisClientState.setControlLocked(payload.locked())))
                .playToServer(ActivateCyberwarePayload.TYPE, ActivateCyberwarePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> CyberwareAbilities.activate(context.player())))
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
                        }));
    }
}
