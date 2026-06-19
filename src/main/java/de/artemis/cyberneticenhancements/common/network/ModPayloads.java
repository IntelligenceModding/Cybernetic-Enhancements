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
import de.artemis.cyberneticenhancements.client.ArchiveContactsClientState;
import de.artemis.cyberneticenhancements.client.ArchiveQuestsClientState;
import de.artemis.cyberneticenhancements.client.FaceHazardHighlightClientState;
import de.artemis.cyberneticenhancements.client.PsychosisOverlayClientState;
import de.artemis.cyberneticenhancements.client.PlayerMotionSyncClient;
import de.artemis.cyberneticenhancements.common.entity.AbstractCityNpcEntity;
import de.artemis.cyberneticenhancements.common.menu.RelicCacheHackMenu;
import de.artemis.cyberneticenhancements.common.quest.PlayerQuestManager;
import de.artemis.cyberneticenhancements.common.menu.RecyclerStationMenu;
import de.artemis.cyberneticenhancements.common.menu.RipperStationMenu;
import de.artemis.cyberneticenhancements.common.menu.TechStationMenu;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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
                .playToClient(ArchiveContactsPayload.TYPE, ArchiveContactsPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> ArchiveContactsClientState.update(payload)))
                .playToClient(ArchiveQuestsPayload.TYPE, ArchiveQuestsPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> ArchiveQuestsClientState.update(payload)))
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
                        }))
                .playToServer(RelicCacheHackSelectPayload.TYPE, RelicCacheHackSelectPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (payload.cellIndex() < 0) {
                                return;
                            }
                            if (context.player().containerMenu instanceof RelicCacheHackMenu menu
                                    && context.player() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                                if (serverPlayer.level().getBlockEntity(menu.getBlockPos()) instanceof de.artemis.cyberneticenhancements.common.blockentity.RelicCacheBlockEntity blockEntity) {
                                    blockEntity.trySelectHackCell(serverPlayer, payload.cellIndex());
                                }
                            }
                        }))
                .playToServer(FixerDialogueChoicePayload.TYPE, FixerDialogueChoicePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (context.player().containerMenu instanceof de.artemis.cyberneticenhancements.common.menu.FixerDialogMenu menu
                                    && context.player() instanceof ServerPlayer serverPlayer) {
                                menu.handleChoice(serverPlayer, payload.optionId());
                            }
                        }))
                .playToServer(NpcIdentityNicknamePayload.TYPE, NpcIdentityNicknamePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (context.player().containerMenu instanceof de.artemis.cyberneticenhancements.common.menu.FixerDialogMenu menu
                                    && context.player() instanceof ServerPlayer serverPlayer) {
                                menu.handleNicknameUpdate(serverPlayer, payload.nickname());
                            }
                        }))
                .playToServer(NpcIdentityAppearancePayload.TYPE, NpcIdentityAppearancePayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (context.player().containerMenu instanceof de.artemis.cyberneticenhancements.common.menu.FixerDialogMenu menu
                                    && context.player() instanceof ServerPlayer serverPlayer) {
                                menu.handleAppearanceUpdate(serverPlayer, payload.appearanceId());
                            }
                        }))
                .playToServer(NpcIdentityNameColorPayload.TYPE, NpcIdentityNameColorPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (context.player().containerMenu instanceof de.artemis.cyberneticenhancements.common.menu.FixerDialogMenu menu
                                    && context.player() instanceof ServerPlayer serverPlayer) {
                                menu.handleNameColorUpdate(serverPlayer, payload.colorId());
                            }
                        }))
                .playToServer(ArchiveContactsRequestPayload.TYPE, ArchiveContactsRequestPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (context.player() instanceof ServerPlayer serverPlayer) {
                                PacketDistributor.sendToPlayer(serverPlayer, ArchiveContactsPayload.capture(serverPlayer));
                            }
                        }))
                .playToServer(ArchiveQuestsRequestPayload.TYPE, ArchiveQuestsRequestPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (context.player() instanceof ServerPlayer serverPlayer) {
                                PacketDistributor.sendToPlayer(serverPlayer, ArchiveQuestsPayload.capture(serverPlayer));
                            }
                        }))
                .playToServer(ArchiveQuestActionPayload.TYPE, ArchiveQuestActionPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                                return;
                            }
                            boolean success = switch (payload.actionId()) {
                                case ArchiveQuestActionPayload.ACTION_DISCARD -> PlayerQuestManager.abandonQuestById(serverPlayer, payload.questId()) == PlayerQuestManager.Result.OK;
                                default -> false;
                            };
                            if (!success) {
                                serverPlayer.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.cyberneticenhancements.archive.quest_discard_unavailable"), true);
                                return;
                            }
                            PacketDistributor.sendToPlayer(serverPlayer, ArchiveQuestsPayload.capture(serverPlayer));
                            PacketDistributor.sendToPlayer(serverPlayer, ArchiveContactsPayload.capture(serverPlayer));
                        }))
                .playToServer(ArchiveContactActionPayload.TYPE, ArchiveContactActionPayload.STREAM_CODEC, (payload, context) ->
                        context.enqueueWork(() -> {
                            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                                return;
                            }
                            java.util.UUID npcId;
                            try {
                                npcId = java.util.UUID.fromString(payload.npcId());
                            } catch (IllegalArgumentException ignored) {
                                return;
                            }

                            boolean success = switch (payload.actionId()) {
                                case ArchiveContactActionPayload.ACTION_LOCATE -> AbstractCityNpcEntity.locateFromArchive(serverPlayer, npcId);
                                case ArchiveContactActionPayload.ACTION_MEETUP -> AbstractCityNpcEntity.summonFromArchive(serverPlayer, npcId);
                                case ArchiveContactActionPayload.ACTION_TOGGLE_PIN -> AbstractCityNpcEntity.togglePinnedFromArchive(serverPlayer, npcId);
                                case ArchiveContactActionPayload.ACTION_TOGGLE_HIDE -> AbstractCityNpcEntity.toggleHiddenFromArchive(serverPlayer, npcId);
                                default -> false;
                            };
                            if (!success) {
                                String messageKey = switch (payload.actionId()) {
                                    case ArchiveContactActionPayload.ACTION_MEETUP -> "message.cyberneticenhancements.archive.contact_meetup_unavailable";
                                    case ArchiveContactActionPayload.ACTION_TOGGLE_PIN, ArchiveContactActionPayload.ACTION_TOGGLE_HIDE -> "message.cyberneticenhancements.archive.contact_update_unavailable";
                                    default -> "message.cyberneticenhancements.archive.contact_locate_unavailable";
                                };
                                serverPlayer.displayClientMessage(net.minecraft.network.chat.Component.translatable(messageKey), true);
                                return;
                            }
                            PacketDistributor.sendToPlayer(serverPlayer, ArchiveContactsPayload.capture(serverPlayer));
                        }));
    }
}
