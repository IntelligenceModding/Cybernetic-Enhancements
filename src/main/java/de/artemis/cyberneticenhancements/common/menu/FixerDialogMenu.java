package de.artemis.cyberneticenhancements.common.menu;

import de.artemis.cyberneticenhancements.common.dialogue.NpcDialogueChoice;
import de.artemis.cyberneticenhancements.common.dialogue.NpcDialogueTree;
import de.artemis.cyberneticenhancements.common.dialogue.NpcDialogueTrees;
import de.artemis.cyberneticenhancements.common.cyberware.CyberstrainManager;
import de.artemis.cyberneticenhancements.common.economy.PlayerEurodollarManager;
import de.artemis.cyberneticenhancements.common.entity.AbstractCityNpcEntity;
import de.artemis.cyberneticenhancements.common.entity.NpcType;
import de.artemis.cyberneticenhancements.common.quest.QuestContract;
import de.artemis.cyberneticenhancements.common.quest.QuestType;
import de.artemis.cyberneticenhancements.common.quest.PlayerQuestManager;
import de.artemis.cyberneticenhancements.common.registry.ModItems;
import de.artemis.cyberneticenhancements.common.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public final class FixerDialogMenu extends AbstractBaseMenu {
    public static final int OPTION_INTRO = 0;
    public static final int OPTION_CACHE_INTEL = 1;
    public static final int OPTION_STABILIZE = 2;
    public static final int OPTION_PURGE = 3;
    public static final int OPTION_BUY_MAXDOC_MK2 = 4;
    public static final int OPTION_BUY_RAM_JOLT = 5;
    public static final int OPTION_BUY_CHROME_SUPPRESSANT = 6;
    public static final int OPTION_BUY_IMMUNOBLOCKERS = 7;
    public static final int OPTION_ACCEPT_CONTRACT_1 = 8;
    public static final int OPTION_ACCEPT_CONTRACT_2 = 9;
    public static final int OPTION_ACCEPT_CONTRACT_3 = 10;
    public static final int OPTION_TURN_IN_CONTRACT = 11;
    public static final int OPTION_ABANDON_CONTRACT = 12;

    public static final int RESPONSE_GREETING = 0;
    public static final int RESPONSE_INTRO = 1;
    public static final int RESPONSE_CACHE_INTEL = 2;
    public static final int RESPONSE_STABILIZE_OK = 3;
    public static final int RESPONSE_NEED_FUNDS = 4;
    public static final int RESPONSE_PURGE_LOCKED = 5;
    public static final int RESPONSE_PURGE_COOLDOWN = 6;
    public static final int RESPONSE_PURGE_OK = 7;
    public static final int RESPONSE_MAXDOC_OK = 8;
    public static final int RESPONSE_RAM_JOLT_OK = 9;
    public static final int RESPONSE_CHROME_SUPPRESSANT_OK = 10;
    public static final int RESPONSE_IMMUNOBLOCKERS_LOCKED = 11;
    public static final int RESPONSE_IMMUNOBLOCKERS_OK = 12;
    public static final int RESPONSE_BUSY_STATE = 13;
    public static final int RESPONSE_COOL_OFF = 14;
    public static final int RESPONSE_CONTRACTS = 15;
    public static final int RESPONSE_CONTRACT_ACCEPTED = 16;
    public static final int RESPONSE_CONTRACT_TURNED_IN = 17;
    public static final int RESPONSE_CONTRACT_NOT_READY = 18;
    public static final int RESPONSE_CONTRACT_MISSING_ITEMS = 19;
    public static final int RESPONSE_CONTRACT_ABANDONED = 20;
    public static final int RESPONSE_CONTRACT_MAX_ACTIVE = 21;
    public static final int RESPONSE_CONTRACT_ALREADY_ACTIVE = 22;
    public static final int RESPONSE_IDENTITY_LOCKED = 23;
    public static final int RESPONSE_IDENTITY_NICKNAME_OK = 24;
    public static final int RESPONSE_IDENTITY_APPEARANCE_OK = 25;
    public static final int RESPONSE_IDENTITY_INVALID = 26;
    public static final int RESPONSE_IDENTITY_COLOR_OK = 27;
    private static final int TRUST_MAX_POINTS = 300;

    private static final int DATA_TRUST = 0;
    private static final int DATA_TRUST_POINTS = 1;
    private static final int DATA_RESPONSE = 2;
    private static final int DATA_OPTION_MASK = 3;
    private static final int DATA_STATUS = 4;
    private static final int DATA_PURGE_READY = 5;
    private static final int DATA_BALANCE = 6;
    private static final int DATA_STABILIZE_PRICE = 7;
    private static final int DATA_PURGE_PRICE = 8;
    private static final int DATA_MAXDOC_PRICE = 9;
    private static final int DATA_RAM_JOLT_PRICE = 10;
    private static final int DATA_CHROME_SUPPRESSANT_PRICE = 11;
    private static final int DATA_IMMUNOBLOCKERS_PRICE = 12;
    private static final int DATA_ACTIVE_PRESENT = 13;
    private static final int DATA_ACTIVE_TYPE = 14;
    private static final int DATA_ACTIVE_TIER = 15;
    private static final int DATA_ACTIVE_TARGET_INDEX = 16;
    private static final int DATA_ACTIVE_TARGET_COUNT = 17;
    private static final int DATA_ACTIVE_PROGRESS = 18;
    private static final int DATA_ACTIVE_READY = 19;
    private static final int DATA_ACTIVE_REWARD_MONEY = 20;
    private static final int DATA_ACTIVE_REWARD_TRUST = 21;
    private static final int DATA_ACTIVE_REWARD_ITEM_KIND = 22;
    private static final int DATA_ACTIVE_REWARD_ITEM_COUNT = 23;
    private static final int DATA_ACTIVE_HAS_LOCATION = 24;
    private static final int DATA_ACTIVE_LOCATION_X = 25;
    private static final int DATA_ACTIVE_LOCATION_Y = 26;
    private static final int DATA_ACTIVE_LOCATION_Z = 27;
    private static final int DATA_OFFER_BASE = 28;
    private static final int DATA_PER_OFFER = 9;
    private static final int OFFER_SLOTS = 3;
    private static final int DATA_DIALOGUE_NODE = DATA_OFFER_BASE + DATA_PER_OFFER * OFFER_SLOTS;
    private static final int DATA_COUNT = DATA_DIALOGUE_NODE + 1;
    private static final int BASE_PRICE_STABILIZE = 5;
    private static final int BASE_PRICE_PURGE = 12;
    private static final int BASE_PRICE_MAXDOC = 3;
    private static final int BASE_PRICE_RAM_JOLT = 4;
    private static final int BASE_PRICE_CHROME_SUPPRESSANT = 5;
    private static final int BASE_PRICE_IMMUNOBLOCKERS = 8;

    private final Player player;
    private final AbstractCityNpcEntity npc;
    private final int[] syncedData = new int[DATA_COUNT];
    private int responseId;
    private int currentDialogueNodeId;

    public FixerDialogMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, (AbstractCityNpcEntity) inventory.player.level().getEntity(extraData.readVarInt()));
    }

    public FixerDialogMenu(int containerId, Inventory inventory, AbstractCityNpcEntity npc) {
        super(ModMenuTypes.FIXER_DIALOG.get(), containerId);
        this.player = inventory.player;
        this.npc = npc;
        this.responseId = npc == null ? RESPONSE_BUSY_STATE : RESPONSE_GREETING;
        this.currentDialogueNodeId = dialogueTree().rootNodeId();
        this.syncedData[DATA_DIALOGUE_NODE] = this.currentDialogueNodeId;
        addStateSlots();
    }

    private void addStateSlots() {
        for (int dataIndex = 0; dataIndex < DATA_COUNT; dataIndex++) {
            final int slotIndex = dataIndex;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return serverValue(slotIndex);
                }

                @Override
                public void set(int value) {
                    syncedData[slotIndex] = value;
                }
            });
        }
    }

    private int serverValue(int dataIndex) {
        if (npc == null) {
            return 0;
        }
        PlayerQuestManager.BoardSnapshot board = player instanceof ServerPlayer serverPlayer && npc.npcType().supportsContracts()
                ? PlayerQuestManager.snapshotForIssuer(serverPlayer, npc)
                : null;
        QuestContract activeContract = board == null ? null : board.active();
        return switch (dataIndex) {
            case DATA_TRUST -> npc.trustLevel(player);
            case DATA_TRUST_POINTS -> npc.trustPoints(player);
            case DATA_RESPONSE -> responseId;
            case DATA_OPTION_MASK -> optionMask();
            case DATA_STATUS -> npc.getBehaviorStatus();
            case DATA_PURGE_READY -> purgeReady() ? 1 : 0;
            case DATA_BALANCE -> PlayerEurodollarManager.balance(player);
            case DATA_STABILIZE_PRICE -> priceForServerOption(OPTION_STABILIZE);
            case DATA_PURGE_PRICE -> priceForServerOption(OPTION_PURGE);
            case DATA_MAXDOC_PRICE -> priceForServerOption(OPTION_BUY_MAXDOC_MK2);
            case DATA_RAM_JOLT_PRICE -> priceForServerOption(OPTION_BUY_RAM_JOLT);
            case DATA_CHROME_SUPPRESSANT_PRICE -> priceForServerOption(OPTION_BUY_CHROME_SUPPRESSANT);
            case DATA_IMMUNOBLOCKERS_PRICE -> priceForServerOption(OPTION_BUY_IMMUNOBLOCKERS);
            case DATA_ACTIVE_PRESENT -> activeContract == null ? 0 : 1;
            case DATA_ACTIVE_TYPE -> activeContract == null ? -1 : activeContract.type().ordinal();
            case DATA_ACTIVE_TIER -> activeContract == null ? 0 : activeContract.tier();
            case DATA_ACTIVE_TARGET_INDEX -> activeContract == null ? 0 : activeContract.targetIndex();
            case DATA_ACTIVE_TARGET_COUNT -> activeContract == null ? 0 : activeContract.targetCount();
            case DATA_ACTIVE_PROGRESS -> activeContract == null ? 0 : activeContract.progress();
            case DATA_ACTIVE_READY -> activeContract != null && activeContract.isReady() ? 1 : 0;
            case DATA_ACTIVE_REWARD_MONEY -> activeContract == null ? 0 : activeContract.rewardMoney();
            case DATA_ACTIVE_REWARD_TRUST -> activeContract == null ? 0 : activeContract.rewardTrust();
            case DATA_ACTIVE_REWARD_ITEM_KIND -> activeContract == null ? 0 : activeContract.rewardItemKind();
            case DATA_ACTIVE_REWARD_ITEM_COUNT -> activeContract == null ? 0 : activeContract.rewardItemCount();
            case DATA_ACTIVE_HAS_LOCATION -> activeContract != null && activeContract.targetPos() != null && !activeContract.dimensionId().isBlank() ? 1 : 0;
            case DATA_ACTIVE_LOCATION_X -> activeContract == null || activeContract.targetPos() == null ? 0 : activeContract.targetPos().getX();
            case DATA_ACTIVE_LOCATION_Y -> activeContract == null || activeContract.targetPos() == null ? 0 : activeContract.targetPos().getY();
            case DATA_ACTIVE_LOCATION_Z -> activeContract == null || activeContract.targetPos() == null ? 0 : activeContract.targetPos().getZ();
            case DATA_DIALOGUE_NODE -> currentDialogueNodeId;
            default -> offerServerValue(dataIndex, board);
        };
    }

    private int offerServerValue(int dataIndex, PlayerQuestManager.BoardSnapshot board) {
        if (dataIndex < DATA_OFFER_BASE || board == null) {
            return 0;
        }
        int relative = dataIndex - DATA_OFFER_BASE;
        int slot = relative / DATA_PER_OFFER;
        int field = relative % DATA_PER_OFFER;
        if (slot < 0 || slot >= board.offers().size()) {
            return field == 0 ? 0 : 0;
        }
        QuestContract offer = board.offers().get(slot);
        return switch (field) {
            case 0 -> 1;
            case 1 -> offer.type().ordinal();
            case 2 -> offer.tier();
            case 3 -> offer.targetIndex();
            case 4 -> offer.targetCount();
            case 5 -> offer.rewardMoney();
            case 6 -> offer.rewardTrust();
            case 7 -> offer.rewardItemKind();
            case 8 -> offer.rewardItemCount();
            default -> 0;
        };
    }

    public void handleChoice(ServerPlayer serverPlayer, int optionId) {
        if (npc == null) {
            responseId = RESPONSE_BUSY_STATE;
            broadcastChanges();
            return;
        }
        if (npc.isRefusing(serverPlayer)) {
            responseId = RESPONSE_COOL_OFF;
            broadcastChanges();
            return;
        }
        if (NpcDialogueTrees.isDialogueChoice(optionId)) {
            handleDialogueChoice(serverPlayer, optionId);
            broadcastChanges();
            return;
        }
        switch (optionId) {
            case OPTION_INTRO -> {
                if (!npc.hasMet(serverPlayer)) {
                    npc.markMet(serverPlayer);
                    npc.raiseTrust(serverPlayer, 3);
                }
                responseId = RESPONSE_INTRO;
            }
            case OPTION_CACHE_INTEL -> {
                if (!npc.hasMet(serverPlayer)) {
                    npc.markMet(serverPlayer);
                }
                responseId = RESPONSE_CACHE_INTEL;
            }
            case OPTION_STABILIZE -> {
                if (!chargeFunds(serverPlayer, priceForServerOption(OPTION_STABILIZE), "Paid for stabilization")) {
                    responseId = npc.isRefusing(serverPlayer) ? RESPONSE_COOL_OFF : RESPONSE_NEED_FUNDS;
                } else {
                    CyberstrainManager.applySuppressionDose(serverPlayer, 18, 90L * 20L, false);
                    npc.raiseTrust(serverPlayer, 1);
                    npc.recordPaidTransaction(serverPlayer);
                    responseId = RESPONSE_STABILIZE_OK;
                }
            }
            case OPTION_PURGE -> {
                if (npc.trustLevel(serverPlayer) < 2) {
                    responseId = RESPONSE_PURGE_LOCKED;
                } else if (!purgeReady()) {
                    responseId = RESPONSE_PURGE_COOLDOWN;
                } else if (!chargeFunds(serverPlayer, priceForServerOption(OPTION_PURGE), "Paid for full purge")) {
                    responseId = npc.isRefusing(serverPlayer) ? RESPONSE_COOL_OFF : RESPONSE_NEED_FUNDS;
                } else {
                    CyberstrainManager.purgeNegativeStatuses(serverPlayer, true);
                    npc.setLastPurgeDay(serverPlayer, currentDay());
                    npc.raiseTrust(serverPlayer, 2);
                    npc.recordPaidTransaction(serverPlayer);
                    responseId = RESPONSE_PURGE_OK;
                }
            }
            case OPTION_BUY_MAXDOC_MK2 -> {
                if (!chargeFunds(serverPlayer, priceForServerOption(OPTION_BUY_MAXDOC_MK2), "Purchased stock")) {
                    responseId = npc.isRefusing(serverPlayer) ? RESPONSE_COOL_OFF : RESPONSE_NEED_FUNDS;
                } else {
                    grantShopItem(serverPlayer, optionId);
                    npc.raiseTrust(serverPlayer, 1);
                    npc.recordPaidTransaction(serverPlayer);
                    responseId = RESPONSE_MAXDOC_OK;
                }
            }
            case OPTION_BUY_RAM_JOLT -> {
                if (!chargeFunds(serverPlayer, priceForServerOption(OPTION_BUY_RAM_JOLT), "Purchased stock")) {
                    responseId = npc.isRefusing(serverPlayer) ? RESPONSE_COOL_OFF : RESPONSE_NEED_FUNDS;
                } else {
                    grantShopItem(serverPlayer, optionId);
                    npc.raiseTrust(serverPlayer, 1);
                    npc.recordPaidTransaction(serverPlayer);
                    responseId = RESPONSE_RAM_JOLT_OK;
                }
            }
            case OPTION_BUY_CHROME_SUPPRESSANT -> {
                if (npc.trustLevel(serverPlayer) < 1) {
                    responseId = RESPONSE_INTRO;
                } else if (!chargeFunds(serverPlayer, priceForServerOption(OPTION_BUY_CHROME_SUPPRESSANT), "Purchased stock")) {
                    responseId = npc.isRefusing(serverPlayer) ? RESPONSE_COOL_OFF : RESPONSE_NEED_FUNDS;
                } else {
                    grantShopItem(serverPlayer, optionId);
                    npc.raiseTrust(serverPlayer, 1);
                    npc.recordPaidTransaction(serverPlayer);
                    responseId = RESPONSE_CHROME_SUPPRESSANT_OK;
                }
            }
            case OPTION_BUY_IMMUNOBLOCKERS -> {
                if (npc.trustLevel(serverPlayer) < 3) {
                    responseId = RESPONSE_IMMUNOBLOCKERS_LOCKED;
                } else if (!chargeFunds(serverPlayer, priceForServerOption(OPTION_BUY_IMMUNOBLOCKERS), "Purchased stock")) {
                    responseId = npc.isRefusing(serverPlayer) ? RESPONSE_COOL_OFF : RESPONSE_NEED_FUNDS;
                } else {
                    grantShopItem(serverPlayer, optionId);
                    npc.raiseTrust(serverPlayer, 2);
                    npc.recordPaidTransaction(serverPlayer);
                    responseId = RESPONSE_IMMUNOBLOCKERS_OK;
                }
            }
            case OPTION_ACCEPT_CONTRACT_1, OPTION_ACCEPT_CONTRACT_2, OPTION_ACCEPT_CONTRACT_3 -> {
                int offerIndex = optionId - OPTION_ACCEPT_CONTRACT_1;
                PlayerQuestManager.Result result = PlayerQuestManager.acceptIssuerOffer(serverPlayer, npc, offerIndex);
                responseId = switch (result) {
                    case OK -> RESPONSE_CONTRACT_ACCEPTED;
                    case MAX_ACTIVE -> RESPONSE_CONTRACT_MAX_ACTIVE;
                    case HAS_ACTIVE -> RESPONSE_CONTRACT_ALREADY_ACTIVE;
                    case INVALID -> RESPONSE_BUSY_STATE;
                };
            }
            case OPTION_TURN_IN_CONTRACT -> {
                PlayerQuestManager.TurnInResult result = PlayerQuestManager.turnInIssuerActive(serverPlayer, npc);
                responseId = switch (result) {
                    case OK -> RESPONSE_CONTRACT_TURNED_IN;
                    case MISSING_ITEMS -> RESPONSE_CONTRACT_MISSING_ITEMS;
                    case NOT_READY -> RESPONSE_CONTRACT_NOT_READY;
                };
            }
            case OPTION_ABANDON_CONTRACT -> {
                PlayerQuestManager.Result result = PlayerQuestManager.abandonIssuerActive(serverPlayer, npc);
                responseId = result == PlayerQuestManager.Result.OK ? RESPONSE_CONTRACT_ABANDONED : RESPONSE_BUSY_STATE;
            }
            default -> {
            }
        }
        broadcastChanges();
    }

    private void handleDialogueChoice(ServerPlayer serverPlayer, int choiceId) {
        NpcDialogueChoice choice;
        try {
            choice = dialogueTree().choice(currentDialogueNodeId, choiceId);
        } catch (IllegalArgumentException ignored) {
            return;
        }

        if (choice.trustDelta() > 0) {
            if (choice.rewardMemoryKey() == null || choice.rewardMemoryKey().isBlank()) {
                npc.raiseTrust(serverPlayer, choice.trustDelta());
            } else {
                npc.grantDialogueTrustOnce(serverPlayer, choice.rewardMemoryKey(), choice.trustDelta());
            }
        } else if (choice.trustDelta() < 0 || choice.annoyanceDelta() > 0 || choice.closeConversation()) {
            npc.applyDialoguePenalty(
                    serverPlayer,
                    Math.max(0, -choice.trustDelta()),
                    choice.annoyanceDelta(),
                    choice.closeConversation()
            );
        }

        currentDialogueNodeId = choice.nextNodeId();
        responseId = switch (choice.view()) {
            case CONTRACTS -> RESPONSE_CONTRACTS;
            default -> RESPONSE_GREETING;
        };
    }

    private boolean chargeFunds(ServerPlayer player, int count, String note) {
        boolean paid = PlayerEurodollarManager.tryWithdraw(
                player,
                count,
                "npc_purchase",
                note,
                npc == null ? null : npc.getUUID(),
                npc == null ? null : npc.getName().getString()
        );
        if (!paid && npc != null) {
            npc.penalizeNoFundsAttempt(player);
        }
        return paid;
    }

    private void grantShopItem(ServerPlayer player, int optionId) {
        ItemStack stack = switch (npcType()) {
            case FIXER -> switch (optionId) {
                case OPTION_BUY_MAXDOC_MK2 -> new ItemStack(ModItems.consumable("maxdoc_mk2").get());
                case OPTION_BUY_RAM_JOLT -> new ItemStack(ModItems.consumable("ram_jolt").get());
                case OPTION_BUY_CHROME_SUPPRESSANT -> new ItemStack(ModItems.consumable("chrome_suppressant").get());
                case OPTION_BUY_IMMUNOBLOCKERS -> new ItemStack(ModItems.consumable("immunoblockers").get());
                default -> ItemStack.EMPTY;
            };
            case RIPPERDOC -> switch (optionId) {
                case OPTION_BUY_MAXDOC_MK2 -> new ItemStack(ModItems.consumable("maxdoc_mk2").get(), 2);
                case OPTION_BUY_RAM_JOLT -> new ItemStack(ModItems.consumable("bounce_back_mk2").get());
                case OPTION_BUY_CHROME_SUPPRESSANT -> new ItemStack(ModItems.consumable("chrome_suppressant").get(), 2);
                case OPTION_BUY_IMMUNOBLOCKERS -> new ItemStack(ModItems.consumable("immunoblockers").get());
                default -> ItemStack.EMPTY;
            };
            case TECHIE -> switch (optionId) {
                case OPTION_BUY_MAXDOC_MK2 -> new ItemStack(ModItems.COMMON_ITEM_COMPONENTS.get(), 2);
                case OPTION_BUY_RAM_JOLT -> new ItemStack(ModItems.SENSOR_LENS.get(), 2);
                case OPTION_BUY_CHROME_SUPPRESSANT -> new ItemStack(ModItems.SERVO_SCREWS.get(), 4);
                case OPTION_BUY_IMMUNOBLOCKERS -> new ItemStack(ModItems.REPLACEMENT_JOINT.get(), 2);
                default -> ItemStack.EMPTY;
            };
            case NETRUNNER -> switch (optionId) {
                case OPTION_BUY_MAXDOC_MK2 -> new ItemStack(ModItems.consumable("ram_jolt").get(), 2);
                case OPTION_BUY_RAM_JOLT -> new ItemStack(ModItems.cyberware("relic_scanner").get());
                case OPTION_BUY_CHROME_SUPPRESSANT -> new ItemStack(ModItems.BASIC_CIRCUIT_PLATE.get(), 2);
                case OPTION_BUY_IMMUNOBLOCKERS -> new ItemStack(ModItems.MICRO_BATTERY.get(), 2);
                default -> ItemStack.EMPTY;
            };
            case MERC -> switch (optionId) {
                case OPTION_BUY_MAXDOC_MK2 -> new ItemStack(ModItems.consumable("maxdoc_mk2").get());
                case OPTION_BUY_RAM_JOLT -> new ItemStack(ModItems.consumable("asskick").get());
                case OPTION_BUY_CHROME_SUPPRESSANT -> new ItemStack(ModItems.consumable("black_lace").get());
                case OPTION_BUY_IMMUNOBLOCKERS -> new ItemStack(ModItems.consumable("bounce_back_mk3").get());
                default -> ItemStack.EMPTY;
            };
        };
        if (!stack.isEmpty()) {
            player.addItem(stack);
        }
    }

    private long currentDay() {
        return player.level().getDayTime() / 24000L;
    }

    private boolean purgeReady() {
        return npc != null && npc.lastPurgeDay(player) < currentDay();
    }

    private int optionMask() {
        int mask = 0;
        mask |= 1 << OPTION_INTRO;
        if (npcType().supportsIntel()) {
            mask |= 1 << OPTION_CACHE_INTEL;
        }
        if (npcType().supportsMedicalServices()) {
            mask |= 1 << OPTION_STABILIZE;
        }
        if (npcType().supportsShop()) {
            mask |= 1 << OPTION_BUY_MAXDOC_MK2;
            mask |= 1 << OPTION_BUY_RAM_JOLT;
        }
        if (npcType().supportsShop() && npc.trustLevel(player) >= 1) {
            mask |= 1 << OPTION_BUY_CHROME_SUPPRESSANT;
        }
        if (npcType().supportsMedicalServices() && npc.trustLevel(player) >= 2) {
            mask |= 1 << OPTION_PURGE;
        }
        if (npcType().supportsShop() && npc.trustLevel(player) >= 3) {
            mask |= 1 << OPTION_BUY_IMMUNOBLOCKERS;
        }
        return mask;
    }

    public int entityId() {
        return npc == null ? -1 : npc.getId();
    }

    public int trustLevel() {
        return syncedData[DATA_TRUST];
    }

    public int trustPoints() {
        return syncedData[DATA_TRUST_POINTS];
    }

    public NpcType npcType() {
        return npc == null ? NpcType.FIXER : npc.npcType();
    }

    public String npcTypeId() {
        return npcType().id();
    }

    public int currentDialogueNodeId() {
        int nodeId = syncedData[DATA_DIALOGUE_NODE];
        try {
            dialogueTree().node(nodeId);
            return nodeId;
        } catch (IllegalArgumentException ignored) {
            return dialogueTree().rootNodeId();
        }
    }

    public NpcDialogueTree dialogueTree() {
        return NpcDialogueTrees.tree(npcType());
    }

    public Component npcTypeLabel() {
        return npcType().displayName();
    }

    public boolean supportsContracts() {
        return npcType().supportsContracts();
    }

    public boolean supportsServices() {
        return npcType().supportsMedicalServices();
    }

    public boolean supportsShop() {
        return npcType().supportsShop();
    }

    public boolean supportsIntel() {
        return npcType().supportsIntel();
    }

    public boolean identityUnlocked() {
        if (npc != null && player instanceof ServerPlayer serverPlayer) {
            return npc.hasMaxTrust(serverPlayer);
        }
        return trustPoints() >= TRUST_MAX_POINTS;
    }

    public int responseId() {
        return syncedData[DATA_RESPONSE];
    }

    public void handleNicknameUpdate(ServerPlayer serverPlayer, String nickname) {
        if (npc == null) {
            responseId = RESPONSE_BUSY_STATE;
        } else if (!identityUnlocked()) {
            responseId = RESPONSE_IDENTITY_LOCKED;
        } else if (!npc.applyNickname(serverPlayer, nickname)) {
            responseId = RESPONSE_IDENTITY_INVALID;
        } else {
            responseId = RESPONSE_IDENTITY_NICKNAME_OK;
        }
        broadcastChanges();
    }

    public void handleAppearanceUpdate(ServerPlayer serverPlayer, String appearanceId) {
        if (npc == null) {
            responseId = RESPONSE_BUSY_STATE;
        } else if (!identityUnlocked()) {
            responseId = RESPONSE_IDENTITY_LOCKED;
        } else if (!npc.applyAppearance(serverPlayer, appearanceId)) {
            responseId = RESPONSE_IDENTITY_INVALID;
        } else {
            responseId = RESPONSE_IDENTITY_APPEARANCE_OK;
        }
        broadcastChanges();
    }

    public void handleNameColorUpdate(ServerPlayer serverPlayer, String colorId) {
        if (npc == null) {
            responseId = RESPONSE_BUSY_STATE;
        } else if (!identityUnlocked()) {
            responseId = RESPONSE_IDENTITY_LOCKED;
        } else if (!npc.applyNameColor(serverPlayer, colorId)) {
            responseId = RESPONSE_IDENTITY_INVALID;
        } else {
            responseId = RESPONSE_IDENTITY_COLOR_OK;
        }
        broadcastChanges();
    }

    public int status() {
        return syncedData[DATA_STATUS];
    }

    public boolean purgeReadyClient() {
        return syncedData[DATA_PURGE_READY] == 1;
    }

    public int priceForOption(int optionId) {
        return switch (optionId) {
            case OPTION_STABILIZE -> syncedData[DATA_STABILIZE_PRICE];
            case OPTION_PURGE -> syncedData[DATA_PURGE_PRICE];
            case OPTION_BUY_MAXDOC_MK2 -> syncedData[DATA_MAXDOC_PRICE];
            case OPTION_BUY_RAM_JOLT -> syncedData[DATA_RAM_JOLT_PRICE];
            case OPTION_BUY_CHROME_SUPPRESSANT -> syncedData[DATA_CHROME_SUPPRESSANT_PRICE];
            case OPTION_BUY_IMMUNOBLOCKERS -> syncedData[DATA_IMMUNOBLOCKERS_PRICE];
            default -> 0;
        };
    }

    public int balance() {
        return syncedData[DATA_BALANCE];
    }

    public int trustDiscountPercent() {
        return discountPercentForTrustPoints(trustPoints());
    }

    public int trustProgressPercent() {
        return trustProgressPercentForPoints(trustPoints());
    }

    public boolean isOptionAvailable(int optionId) {
        return (syncedData[DATA_OPTION_MASK] & (1 << optionId)) != 0;
    }

    public boolean hasActiveContract() {
        return syncedData[DATA_ACTIVE_PRESENT] == 1;
    }

    public boolean activeContractReady() {
        return syncedData[DATA_ACTIVE_READY] == 1;
    }

    public int activeContractTier() {
        return syncedData[DATA_ACTIVE_TIER];
    }

    public int activeContractTargetIndex() {
        return syncedData[DATA_ACTIVE_TARGET_INDEX];
    }

    public int activeContractTargetCount() {
        return syncedData[DATA_ACTIVE_TARGET_COUNT];
    }

    public int activeContractProgress() {
        return syncedData[DATA_ACTIVE_PROGRESS];
    }

    public int activeContractRewardMoney() {
        return syncedData[DATA_ACTIVE_REWARD_MONEY];
    }

    public int activeContractRewardTrust() {
        return syncedData[DATA_ACTIVE_REWARD_TRUST];
    }

    public int activeContractRewardItemKind() {
        return syncedData[DATA_ACTIVE_REWARD_ITEM_KIND];
    }

    public int activeContractRewardItemCount() {
        return syncedData[DATA_ACTIVE_REWARD_ITEM_COUNT];
    }

    public boolean activeContractHasLocation() {
        return syncedData[DATA_ACTIVE_HAS_LOCATION] == 1;
    }

    public int activeContractLocationX() {
        return syncedData[DATA_ACTIVE_LOCATION_X];
    }

    public int activeContractLocationY() {
        return syncedData[DATA_ACTIVE_LOCATION_Y];
    }

    public int activeContractLocationZ() {
        return syncedData[DATA_ACTIVE_LOCATION_Z];
    }

    public QuestType activeContractType() {
        return questTypeFromOrdinal(syncedData[DATA_ACTIVE_TYPE]);
    }

    public boolean offerPresent(int slot) {
        return offerValue(slot, 0) == 1;
    }

    public QuestType offerType(int slot) {
        return questTypeFromOrdinal(offerValue(slot, 1));
    }

    public int offerTier(int slot) {
        return offerValue(slot, 2);
    }

    public int offerTargetIndex(int slot) {
        return offerValue(slot, 3);
    }

    public int offerTargetCount(int slot) {
        return offerValue(slot, 4);
    }

    public int offerRewardMoney(int slot) {
        return offerValue(slot, 5);
    }

    public int offerRewardTrust(int slot) {
        return offerValue(slot, 6);
    }

    public int offerRewardItemKind(int slot) {
        return offerValue(slot, 7);
    }

    public int offerRewardItemCount(int slot) {
        return offerValue(slot, 8);
    }

    private int priceForServerOption(int optionId) {
        int basePrice = basePriceForOption(optionId);
        int trustDiscountPercent = npc == null || !npc.npcType().supportsTrustDiscount() ? 0 : discountPercentForTrustPoints(npc.trustPoints(player));
        int discountedPrice = Math.max(1, Math.round(basePrice * (100 - trustDiscountPercent) / 100.0F));
        float priceMultiplier = npc == null ? 1.0F : npc.priceMultiplier(player);
        return Math.max(1, Math.round(discountedPrice * priceMultiplier));
    }

    private int basePriceForOption(int optionId) {
        return switch (npcType()) {
            case FIXER -> switch (optionId) {
                case OPTION_STABILIZE -> BASE_PRICE_STABILIZE;
                case OPTION_PURGE -> BASE_PRICE_PURGE;
                case OPTION_BUY_MAXDOC_MK2 -> BASE_PRICE_MAXDOC;
                case OPTION_BUY_RAM_JOLT -> BASE_PRICE_RAM_JOLT;
                case OPTION_BUY_CHROME_SUPPRESSANT -> BASE_PRICE_CHROME_SUPPRESSANT;
                case OPTION_BUY_IMMUNOBLOCKERS -> BASE_PRICE_IMMUNOBLOCKERS;
                default -> 0;
            };
            case RIPPERDOC -> switch (optionId) {
                case OPTION_STABILIZE -> 4;
                case OPTION_PURGE -> 10;
                case OPTION_BUY_MAXDOC_MK2 -> 5;
                case OPTION_BUY_RAM_JOLT -> 5;
                case OPTION_BUY_CHROME_SUPPRESSANT -> 7;
                case OPTION_BUY_IMMUNOBLOCKERS -> 9;
                default -> 0;
            };
            case TECHIE -> switch (optionId) {
                case OPTION_BUY_MAXDOC_MK2 -> 4;
                case OPTION_BUY_RAM_JOLT -> 5;
                case OPTION_BUY_CHROME_SUPPRESSANT -> 4;
                case OPTION_BUY_IMMUNOBLOCKERS -> 6;
                default -> 0;
            };
            case NETRUNNER -> switch (optionId) {
                case OPTION_BUY_MAXDOC_MK2 -> 5;
                case OPTION_BUY_RAM_JOLT -> 9;
                case OPTION_BUY_CHROME_SUPPRESSANT -> 5;
                case OPTION_BUY_IMMUNOBLOCKERS -> 6;
                default -> 0;
            };
            case MERC -> switch (optionId) {
                case OPTION_BUY_MAXDOC_MK2 -> 4;
                case OPTION_BUY_RAM_JOLT -> 6;
                case OPTION_BUY_CHROME_SUPPRESSANT -> 8;
                case OPTION_BUY_IMMUNOBLOCKERS -> 7;
                default -> 0;
            };
        };
    }

    private static int discountPercentForTrustPoints(int trustPoints) {
        return Math.min(25, (trustProgressPercentForPoints(trustPoints) + 3) / 4);
    }

    private static int trustProgressPercentForPoints(int trustPoints) {
        return Math.max(0, Math.min(100, Math.round(trustPoints * 100.0F / TRUST_MAX_POINTS)));
    }

    public Component activeContractTitle() {
        return QuestContract.titleForType(activeContractType());
    }

    public Component activeContractObjective() {
        return objectiveFor(activeContractType(), activeContractTargetIndex(), activeContractProgress(), activeContractTargetCount(), activeContractReady());
    }

    public Component activeContractRewardSummary() {
        return rewardSummary(activeContractRewardMoney(), activeContractRewardTrust(), activeContractRewardItemKind(), activeContractRewardItemCount());
    }

    public Component activeContractLocationSummary() {
        if (!activeContractHasLocation()) {
            return Component.empty();
        }
        return Component.translatable(
                "screen.cyberneticenhancements.fixer.contract.location",
                activeContractLocationX(),
                activeContractLocationY(),
                activeContractLocationZ()
        );
    }

    public Component offerTitle(int slot) {
        return QuestContract.titleForType(offerType(slot));
    }

    public Component offerSummary(int slot) {
        return objectiveFor(offerType(slot), offerTargetIndex(slot), 0, offerTargetCount(slot), false);
    }

    public Component offerRewardSummary(int slot) {
        return rewardSummary(offerRewardMoney(slot), offerRewardTrust(slot), offerRewardItemKind(slot), offerRewardItemCount(slot));
    }

    private Component objectiveFor(QuestType type, int targetIndex, int progress, int targetCount, boolean ready) {
        if (ready) {
            return Component.translatable("quest.cyberneticenhancements.objective.return", npc == null ? "contact" : npc.getName());
        }
        return switch (type) {
            case DELIVERY -> Component.translatable("quest.cyberneticenhancements.objective.delivery.stash");
            case EXTRACTION -> Component.translatable("quest.cyberneticenhancements.objective.extraction", targetLabel(type, targetIndex));
            case INVESTIGATION -> Component.translatable("quest.cyberneticenhancements.objective.investigation", progress, targetCount);
            case BREACH -> Component.translatable("quest.cyberneticenhancements.objective.breach", targetLabel(type, targetIndex));
            case ELIMINATION -> Component.translatable("quest.cyberneticenhancements.objective.elimination", progress, targetCount, targetLabel(type, targetIndex));
            case RECOVERY -> Component.translatable("quest.cyberneticenhancements.objective.recovery", progress, targetCount, targetLabel(type, targetIndex));
        };
    }

    private Component targetLabel(QuestType type, int targetIndex) {
        return switch (type) {
            case DELIVERY -> Component.translatable("quest.cyberneticenhancements.target.hidden_stash");
            case EXTRACTION -> Component.translatable("quest.cyberneticenhancements.target.exfil_shard");
            case INVESTIGATION -> Component.translatable("quest.cyberneticenhancements.target.signal_site");
            case BREACH -> Component.translatable("quest.cyberneticenhancements.target.breach_cache");
            case ELIMINATION -> QuestContract.safeEliminationTarget(targetIndex).getDescription();
            case RECOVERY -> QuestContract.safeRecoveryItem(targetIndex).getDefaultInstance().getHoverName();
        };
    }

    private Component rewardSummary(int money, int trust, int rewardItemKind, int rewardItemCount) {
        ItemStack rewardStack = switch (rewardItemKind) {
            case QuestContract.REWARD_MAXDOC -> new ItemStack(ModItems.consumable("maxdoc_mk2").get(), rewardItemCount);
            case QuestContract.REWARD_RAM_JOLT -> new ItemStack(ModItems.consumable("ram_jolt").get(), rewardItemCount);
            case QuestContract.REWARD_CHROME_SUPPRESSANT -> new ItemStack(ModItems.consumable("chrome_suppressant").get(), rewardItemCount);
            default -> ItemStack.EMPTY;
        };
        return rewardStack.isEmpty()
                ? Component.translatable("quest.cyberneticenhancements.reward.bundle", money, trust)
                : Component.translatable("quest.cyberneticenhancements.reward.bundle.item", money, trust, rewardStack.getHoverName());
    }

    private int offerValue(int slot, int offset) {
        if (slot < 0 || slot >= OFFER_SLOTS) {
            return 0;
        }
        return syncedData[DATA_OFFER_BASE + slot * DATA_PER_OFFER + offset];
    }

    private static QuestType questTypeFromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= QuestType.values().length) {
            return QuestType.DELIVERY;
        }
        return QuestType.values()[ordinal];
    }

    @Override
    public boolean stillValid(Player player) {
        return npc != null
                && npc.isAlive()
                && npc.distanceToSqr(player) <= 64.0D
                && !npc.isRefusing(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (npc != null) {
            npc.clearCustomer(player);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
