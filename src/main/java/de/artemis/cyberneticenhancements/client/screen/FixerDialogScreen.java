package de.artemis.cyberneticenhancements.client.screen;

import de.artemis.cyberneticenhancements.client.render.FixerEntityRenderer;
import de.artemis.cyberneticenhancements.common.dialogue.NpcDialogueChoice;
import de.artemis.cyberneticenhancements.common.dialogue.NpcDialogueNode;
import de.artemis.cyberneticenhancements.common.dialogue.NpcDialogueTree;
import de.artemis.cyberneticenhancements.common.dialogue.NpcDialogueView;
import de.artemis.cyberneticenhancements.common.entity.AbstractCityNpcEntity;
import de.artemis.cyberneticenhancements.common.entity.FixerEntity;
import de.artemis.cyberneticenhancements.common.entity.MercEntity;
import de.artemis.cyberneticenhancements.common.entity.NetrunnerEntity;
import de.artemis.cyberneticenhancements.common.entity.RipperdocEntity;
import de.artemis.cyberneticenhancements.common.entity.TechieEntity;
import de.artemis.cyberneticenhancements.common.menu.FixerDialogMenu;
import de.artemis.cyberneticenhancements.common.network.FixerDialogueChoicePayload;
import de.artemis.cyberneticenhancements.common.network.NpcIdentityAppearancePayload;
import de.artemis.cyberneticenhancements.common.network.NpcIdentityNameColorPayload;
import de.artemis.cyberneticenhancements.common.network.NpcIdentityNicknamePayload;
import de.artemis.cyberneticenhancements.common.registry.ModEntityTypes;
import de.artemis.cyberneticenhancements.common.world.NpcIdentityConfig.NpcCategory;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class FixerDialogScreen extends AbstractContainerScreen<FixerDialogMenu> {
    private static final int OUTER_BG = 0xEE091016;
    private static final int PANEL_BG = StationScreenStyle.PANEL_BG;
    private static final int PANEL_ALT = StationScreenStyle.PANEL_ALT;
    private static final int PANEL_EDGE = StationScreenStyle.PANEL_EDGE;
    private static final int PANEL_DEEP = StationScreenStyle.PANEL_DEEP;
    private static final int TEXT_PRIMARY = StationScreenStyle.TEXT_PRIMARY;
    private static final int TEXT_SECONDARY = StationScreenStyle.TEXT_SECONDARY;
    private static final int ACCENT = StationScreenStyle.ACCENT;
    private static final int CONTENT_PADDING = 12;
    private static final int PANEL_TEXT_PADDING_X = StationScreenStyle.PANEL_TEXT_PADDING_X;
    private static final int PANEL_TEXT_PADDING_Y = StationScreenStyle.PANEL_TEXT_PADDING_Y;
    private static final int PANEL_GAP = 8;
    private static final int HEADER_HEIGHT = 54;
    private static final int STATUS_HEIGHT = 52;
    private static final int CHOICES_HEIGHT = 146;
    private static final int CHOICE_BUTTON_HEIGHT = 18;
    private static final int CHOICE_BUTTON_GAP = 4;
    private static final int TRANSCRIPT_TITLE_GAP = 8;
    private static final int TRANSCRIPT_BUBBLE_GAP = 8;
    private static final int TRUST_BAR_HEIGHT = 8;
    private static final int IDENTITY_LABEL_Y_OFFSET = 28;
    private static final int IDENTITY_FIELD_Y_OFFSET = 40;
    private static final int IDENTITY_GALLERY_Y_OFFSET = 68;
    private static final int IDENTITY_FIELD_WIDTH = 196;
    private static final int IDENTITY_COLOR_BUTTON_SIZE = 18;
    private static final int IDENTITY_COLOR_BUTTON_GAP = 4;
    private static final int IDENTITY_TILE_HEIGHT = 82;
    private static final int IDENTITY_TILE_GAP = 8;
    private static final int IDENTITY_VISIBLE_SKINS = 5;
    private static final int IDENTITY_SCROLLBAR_HEIGHT = 8;
    private static final int IMAGE_W = 420;
    private static final int IMAGE_H = 484;

    private final List<ChoiceButton> choiceButtons = new ArrayList<>();
    private final List<ColorPresetButton> colorButtons = new ArrayList<>();
    private static final NameColorPreset[] NAME_COLOR_PRESETS = new NameColorPreset[] {
            new NameColorPreset("aqua", 0x4FE9E2),
            new NameColorPreset("gold", 0xF0C04D),
            new NameColorPreset("green", 0x57C26E),
            new NameColorPreset("red", 0xD65D5D),
            new NameColorPreset("light_purple", 0xD88FF7),
            new NameColorPreset("white", 0xE8EDF2)
    };
    private EditBox nicknameField;
    private ConversationMode mode = ConversationMode.DIALOGUE;
    private String lastPlayerLineKey;
    private String localNpcLineKey;
    private String selectedAppearanceId = "";
    private String selectedNameColorId = "aqua";
    private int skinPage;
    private AbstractCityNpcEntity previewNpc;

    public FixerDialogScreen(FixerDialogMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = IMAGE_W;
        this.imageHeight = IMAGE_H;
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
        this.titleLabelX = 10000;
        this.titleLabelY = 10000;
        this.lastPlayerLineKey = playerKey("open");
        this.localNpcLineKey = greetingKey();
    }

    @Override
    protected void init() {
        super.init();
        choiceButtons.clear();
        nicknameField = new EditBox(font, identityFieldX(), identityFieldY(), IDENTITY_FIELD_WIDTH, 18, Component.translatable("screen.cyberneticenhancements.npc.identity.nickname"));
        nicknameField.setMaxLength(32);
        nicknameField.setVisible(false);
        addRenderableWidget(nicknameField);
        colorButtons.clear();
        for (int index = 0; index < NAME_COLOR_PRESETS.length; index++) {
            NameColorPreset preset = NAME_COLOR_PRESETS[index];
            ColorPresetButton button = new ColorPresetButton(0, 0, IDENTITY_COLOR_BUTTON_SIZE, IDENTITY_COLOR_BUTTON_SIZE, preset);
            colorButtons.add(button);
            addRenderableWidget(button);
        }
        int buttonX = leftPos + CONTENT_PADDING + 10;
        int buttonWidth = imageWidth - CONTENT_PADDING * 2 - 20;
        int startY = choicesY() + PANEL_TEXT_PADDING_Y + font.lineHeight + 8;
        for (int index = 0; index < 5; index++) {
            ChoiceButton button = new ChoiceButton(buttonX, startY + index * (CHOICE_BUTTON_HEIGHT + CHOICE_BUTTON_GAP), buttonWidth, CHOICE_BUTTON_HEIGHT);
            choiceButtons.add(button);
            addRenderableWidget(button);
        }
        syncIdentityEditorFromNpc();
        refreshChoices();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (nicknameField != null) {
            nicknameField.setX(identityFieldX());
            nicknameField.setY(identityFieldY());
            nicknameField.setWidth(Math.min(IDENTITY_FIELD_WIDTH, transcriptWidth() - PANEL_TEXT_PADDING_X * 2));
            nicknameField.setVisible(mode == ConversationMode.IDENTITY);
            nicknameField.setEditable(mode == ConversationMode.IDENTITY && menu.identityUnlocked());
        }
        for (int index = 0; index < colorButtons.size(); index++) {
            ColorPresetButton button = colorButtons.get(index);
            button.setX(identityColorButtonsX() + index * (IDENTITY_COLOR_BUTTON_SIZE + IDENTITY_COLOR_BUTTON_GAP));
            button.setY(identityFieldY());
            button.visible = mode == ConversationMode.IDENTITY;
            button.active = menu.identityUnlocked();
            button.selected = button.preset.colorId().equals(selectedNameColorId);
        }
        refreshChoices();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x0 = leftPos;
        int y0 = topPos;
        guiGraphics.fill(x0, y0, x0 + imageWidth, y0 + imageHeight, OUTER_BG);
        drawPanel(guiGraphics, x0, y0, imageWidth, imageHeight, PANEL_BG);
        drawPanel(guiGraphics, headerX(), headerY(), headerWidth(), headerHeight(), PANEL_ALT);
        drawPanel(guiGraphics, statusX(), statusY(), statusWidth(), statusHeight(), PANEL_ALT);
        drawPanel(guiGraphics, transcriptX(), transcriptY(), transcriptWidth(), transcriptHeight(), PANEL_ALT);
        drawPanel(guiGraphics, choicesX(), choicesY(), choicesWidth(), choicesHeight(), PANEL_ALT);

        renderHeader(guiGraphics);
        renderStatus(guiGraphics);
        renderTranscript(guiGraphics);
        renderChoiceHeader(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (mode != ConversationMode.IDENTITY || button != 0 || !menu.identityUnlocked()) {
            return false;
        }
        int skinIndex = clickedIdentitySkin(mouseX, mouseY);
        if (skinIndex >= 0) {
            List<ResourceLocation> skins = availableSkins();
            if (skinIndex < skins.size()) {
                selectedAppearanceId = skins.get(skinIndex).toString();
                PacketDistributor.sendToServer(new NpcIdentityAppearancePayload(selectedAppearanceId));
                alignSkinScrollToSelection();
            }
            return true;
        }
        return clickedIdentityScrollbar(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (mode == ConversationMode.IDENTITY
                && nicknameField != null
                && nicknameField.isVisible()
                && nicknameField.isFocused()
                && nicknameField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (mode == ConversationMode.IDENTITY
                && nicknameField != null
                && nicknameField.isVisible()
                && nicknameField.isFocused()
                && nicknameField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mode == ConversationMode.IDENTITY && menu.identityUnlocked() && scrollY != 0.0D) {
            scrollSkinGallery(scrollY < 0.0D ? 1 : -1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void refreshChoices() {
        List<ChoiceSpec> specs = currentChoices();
        for (int index = 0; index < choiceButtons.size(); index++) {
            ChoiceButton button = choiceButtons.get(index);
            if (index >= specs.size()) {
                button.visible = false;
                button.active = false;
                button.spec = null;
                continue;
            }
            button.spec = specs.get(index);
            button.visible = true;
            button.active = button.spec.enabled();
            button.setMessage(button.spec.message());
        }
    }

    private List<ChoiceSpec> currentChoices() {
        return switch (mode) {
            case DIALOGUE -> dialogueChoices();
            case SERVICES -> serviceChoices();
            case SHOP -> shopChoices();
            case CONTRACTS -> contractChoices();
            case IDENTITY -> identityChoices();
        };
    }

    private List<ChoiceSpec> dialogueChoices() {
        NpcDialogueNode node = currentDialogueNode();
        List<ChoiceSpec> choices = new ArrayList<>();
        for (NpcDialogueChoice choice : node.choices()) {
            choices.add(customChoice(
                    Component.translatable(choice.translationKey()),
                    true,
                    () -> runDialogueChoice(choice)
            ));
        }
        if (menu.identityUnlocked()) {
            choices.add(customChoice(
                    Component.translatable("screen.cyberneticenhancements.npc.choice.identity"),
                    true,
                    this::openIdentityMode
            ));
        }
        return choices;
    }

    private List<ChoiceSpec> serviceChoices() {
        List<ChoiceSpec> choices = new ArrayList<>();
        choices.add(serverChoice(choiceKey("service_stabilize"), playerKey("service_stabilize"), FixerDialogMenu.OPTION_STABILIZE, menu.priceForOption(FixerDialogMenu.OPTION_STABILIZE), true, menu.isOptionAvailable(FixerDialogMenu.OPTION_STABILIZE)));
        choices.add(serverChoice(choiceKey("service_purge"), playerKey("service_purge"), FixerDialogMenu.OPTION_PURGE, menu.priceForOption(FixerDialogMenu.OPTION_PURGE), true, menu.isOptionAvailable(FixerDialogMenu.OPTION_PURGE)));
        choices.add(localChoice("screen.cyberneticenhancements.fixer.choice.back", playerKey("back"), npcLineKey("back"), () -> switchMode(ConversationMode.DIALOGUE, playerKey("back"), npcLineKey("back"))));
        return choices;
    }

    private List<ChoiceSpec> shopChoices() {
        List<ChoiceSpec> choices = new ArrayList<>();
        choices.add(shopChoice(FixerDialogMenu.OPTION_BUY_MAXDOC_MK2, 0));
        choices.add(shopChoice(FixerDialogMenu.OPTION_BUY_RAM_JOLT, 1));
        choices.add(shopChoice(FixerDialogMenu.OPTION_BUY_CHROME_SUPPRESSANT, 2));
        choices.add(shopChoice(FixerDialogMenu.OPTION_BUY_IMMUNOBLOCKERS, 3));
        choices.add(localChoice("screen.cyberneticenhancements.fixer.choice.back", playerKey("back"), npcLineKey("back"), () -> switchMode(ConversationMode.DIALOGUE, playerKey("back"), npcLineKey("back"))));
        return choices;
    }

    private List<ChoiceSpec> contractChoices() {
        List<ChoiceSpec> choices = new ArrayList<>();
        if (menu.hasActiveContract()) {
            choices.add(customChoice(
                    Component.translatable("screen.cyberneticenhancements.fixer.choice.contract.turn_in", menu.activeContractTitle()),
                    menu.activeContractReady(),
                    () -> sendOption(FixerDialogMenu.OPTION_TURN_IN_CONTRACT, playerKey("turn_in_contract"))
            ));
            choices.add(customChoice(
                    Component.translatable("screen.cyberneticenhancements.fixer.choice.contract.abandon", menu.activeContractTitle()),
                    true,
                    () -> sendOption(FixerDialogMenu.OPTION_ABANDON_CONTRACT, playerKey("abandon_contract"))
            ));
        } else {
            for (int slot = 0; slot < 3; slot++) {
                if (!menu.offerPresent(slot)) {
                    continue;
                }
                final int offerSlot = slot;
                choices.add(customChoice(
                        Component.translatable("screen.cyberneticenhancements.fixer.choice.contract.accept", menu.offerTitle(slot), menu.offerRewardMoney(slot)),
                        true,
                        () -> sendOption(FixerDialogMenu.OPTION_ACCEPT_CONTRACT_1 + offerSlot, playerKey("accept_contract"))
                ));
            }
        }
        choices.add(localChoice("screen.cyberneticenhancements.fixer.choice.back", playerKey("back"), npcLineKey("back"), () -> switchMode(ConversationMode.DIALOGUE, playerKey("back"), npcLineKey("back"))));
        return choices;
    }

    private List<ChoiceSpec> identityChoices() {
        List<ChoiceSpec> choices = new ArrayList<>();
        choices.add(customChoice(
                Component.translatable("screen.cyberneticenhancements.npc.identity.save_name"),
                menu.identityUnlocked(),
                this::sendNicknameUpdate
        ));
        choices.add(customChoice(
                Component.translatable("screen.cyberneticenhancements.npc.identity.reset_name"),
                menu.identityUnlocked(),
                () -> {
                    if (nicknameField != null) {
                        nicknameField.setValue("");
                    }
                    sendNicknameUpdate();
                }
        ));
        choices.add(customChoice(
                Component.translatable("screen.cyberneticenhancements.npc.identity.previous_page"),
                skinPage > 0,
                () -> scrollSkinGallery(-1)
        ));
        choices.add(customChoice(
                Component.translatable("screen.cyberneticenhancements.npc.identity.next_page"),
                hasNextSkinScroll(),
                () -> scrollSkinGallery(1)
        ));
        choices.add(localChoice("screen.cyberneticenhancements.fixer.choice.back", playerKey("back"), npcLineKey("back"), () -> switchMode(ConversationMode.DIALOGUE, playerKey("back"), npcLineKey("back"))));
        return choices;
    }

    private ChoiceSpec shopChoice(int optionId, int slot) {
        return customChoice(
                Component.translatable("screen.cyberneticenhancements.npc.choice.shop.entry", shopChoiceLabel(slot), menu.priceForOption(optionId)),
                menu.isOptionAvailable(optionId),
                () -> {
                    lastPlayerLineKey = playerKey("shop");
                    PacketDistributor.sendToServer(new FixerDialogueChoicePayload(optionId));
                }
        );
    }

    private Component shopChoiceLabel(int slot) {
        String key = switch (menu.npcType().id()) {
            case "ripperdoc" -> switch (slot) {
                case 0 -> "screen.cyberneticenhancements.npc.shop.ripperdoc.maxdoc";
                case 1 -> "screen.cyberneticenhancements.npc.shop.ripperdoc.bounce_back";
                case 2 -> "screen.cyberneticenhancements.npc.shop.ripperdoc.chrome";
                default -> "screen.cyberneticenhancements.npc.shop.ripperdoc.immuno";
            };
            case "techie" -> switch (slot) {
                case 0 -> "screen.cyberneticenhancements.npc.shop.techie.components";
                case 1 -> "screen.cyberneticenhancements.npc.shop.techie.sensors";
                case 2 -> "screen.cyberneticenhancements.npc.shop.techie.servos";
                default -> "screen.cyberneticenhancements.npc.shop.techie.joints";
            };
            case "netrunner" -> switch (slot) {
                case 0 -> "screen.cyberneticenhancements.npc.shop.netrunner.ram_jolt";
                case 1 -> "screen.cyberneticenhancements.npc.shop.netrunner.relic_scanner";
                case 2 -> "screen.cyberneticenhancements.npc.shop.netrunner.circuit";
                default -> "screen.cyberneticenhancements.npc.shop.netrunner.battery";
            };
            case "merc" -> switch (slot) {
                case 0 -> "screen.cyberneticenhancements.npc.shop.merc.maxdoc";
                case 1 -> "screen.cyberneticenhancements.npc.shop.merc.asskick";
                case 2 -> "screen.cyberneticenhancements.npc.shop.merc.black_lace";
                default -> "screen.cyberneticenhancements.npc.shop.merc.bounce_back";
            };
            default -> switch (slot) {
                case 0 -> "screen.cyberneticenhancements.npc.shop.fixer.maxdoc";
                case 1 -> "screen.cyberneticenhancements.npc.shop.fixer.ram_jolt";
                case 2 -> "screen.cyberneticenhancements.npc.shop.fixer.chrome";
                default -> "screen.cyberneticenhancements.npc.shop.fixer.immuno";
            };
        };
        return Component.translatable(key);
    }

    private String greetingKey() {
        return "screen.cyberneticenhancements.npc.line." + menu.npcTypeId() + ".greeting";
    }

    private String playerKey(String suffix) {
        return "screen.cyberneticenhancements.npc.player." + menu.npcTypeId() + "." + suffix;
    }

    private String npcLineKey(String suffix) {
        return "screen.cyberneticenhancements.npc.line." + menu.npcTypeId() + "." + suffix;
    }

    private String responseKey(String suffix) {
        return "screen.cyberneticenhancements.npc.response." + menu.npcTypeId() + "." + suffix;
    }

    private String choiceKey(String suffix) {
        return "screen.cyberneticenhancements.npc.choice." + menu.npcTypeId() + "." + suffix;
    }

    private Component choiceLabel(String suffix) {
        return Component.translatable(choiceKey(suffix));
    }

    private ChoiceSpec localChoice(String labelKey, String playerLineKey, String npcLineKey, Runnable action) {
        return new ChoiceSpec(Component.translatable(labelKey), true, () -> {
            lastPlayerLineKey = playerLineKey;
            localNpcLineKey = npcLineKey;
            action.run();
        });
    }

    private ChoiceSpec serverChoice(String labelKey, String playerLineKey, int optionId, int price, boolean showPrice, boolean enabled) {
        Object[] args = showPrice ? new Object[]{price} : new Object[0];
        Component message = args.length == 0 ? Component.translatable(labelKey) : Component.translatable(labelKey, args);
        return new ChoiceSpec(message, enabled, () -> {
            lastPlayerLineKey = playerLineKey;
            PacketDistributor.sendToServer(new FixerDialogueChoicePayload(optionId));
        });
    }

    private ChoiceSpec customChoice(Component message, boolean enabled, Runnable action) {
        return new ChoiceSpec(message, enabled, action);
    }

    private void switchMode(ConversationMode newMode, String playerLineKey, String npcLineKey) {
        mode = newMode;
        lastPlayerLineKey = playerLineKey;
        localNpcLineKey = npcLineKey;
        refreshChoices();
    }

    private void sendOption(int optionId, String playerLineKey) {
        lastPlayerLineKey = playerLineKey;
        PacketDistributor.sendToServer(new FixerDialogueChoicePayload(optionId));
    }

    private void openIdentityMode() {
        mode = ConversationMode.IDENTITY;
        lastPlayerLineKey = playerKey("identity");
        localNpcLineKey = responseKey("identity");
        syncIdentityEditorFromNpc();
        refreshChoices();
    }

    private void sendNicknameUpdate() {
        String nickname = nicknameField == null ? "" : nicknameField.getValue();
        PacketDistributor.sendToServer(new NpcIdentityNicknamePayload(nickname));
    }

    private void syncIdentityEditorFromNpc() {
        AbstractCityNpcEntity npc = currentNpcEntity();
        if (nicknameField != null && npc != null) {
            nicknameField.setValue(npc.getName().getString());
        }
        selectedAppearanceId = currentAppearanceId();
        selectedNameColorId = currentNameColorId();
        alignSkinScrollToSelection();
    }

    private void runDialogueChoice(NpcDialogueChoice choice) {
        lastPlayerLineKey = choice.playerLineKey();
        localNpcLineKey = currentNpcLineKey();
        switch (choice.view()) {
            case SERVICES -> {
                mode = ConversationMode.SERVICES;
                localNpcLineKey = npcLineKey("services");
            }
            case SHOP -> {
                mode = ConversationMode.SHOP;
                localNpcLineKey = npcLineKey("shop");
            }
            case CONTRACTS -> {
                mode = ConversationMode.CONTRACTS;
                localNpcLineKey = npcLineKey("contracts");
            }
            case DIALOGUE, CLOSE -> mode = ConversationMode.DIALOGUE;
        }
        PacketDistributor.sendToServer(new FixerDialogueChoicePayload(choice.id()));
        refreshChoices();
    }

    private NpcDialogueTree dialogueTree() {
        return menu.dialogueTree();
    }

    private NpcDialogueNode currentDialogueNode() {
        return dialogueTree().node(menu.currentDialogueNodeId());
    }

    private String currentNpcLineKey() {
        return currentDialogueNode().npcLineKey();
    }

    private AbstractCityNpcEntity currentNpcEntity() {
        if (minecraft == null || minecraft.level == null || menu.entityId() < 0) {
            return null;
        }
        return minecraft.level.getEntity(menu.entityId()) instanceof AbstractCityNpcEntity npc ? npc : null;
    }

    private String currentNpcDisplayName() {
        AbstractCityNpcEntity npc = currentNpcEntity();
        return npc == null ? title.getString() : npc.getName().getString();
    }

    private String currentAppearanceId() {
        AbstractCityNpcEntity npc = currentNpcEntity();
        if (npc == null) {
            return selectedAppearanceId;
        }
        if (!npc.appearanceOverride().isBlank()) {
            return npc.appearanceOverride();
        }
        return FixerEntityRenderer.resolveTexture(npc.getUUID(), npc.npcCategory()).toString();
    }

    private String currentNameColorId() {
        AbstractCityNpcEntity npc = currentNpcEntity();
        if (npc == null || npc.nameColorId().isBlank()) {
            return selectedNameColorId;
        }
        return npc.nameColorId();
    }

    private List<ResourceLocation> availableSkins() {
        return FixerEntityRenderer.availableTextures();
    }

    private int selectedAppearanceIndex() {
        List<ResourceLocation> skins = availableSkins();
        if (skins.isEmpty()) {
            return 0;
        }
        String current = selectedAppearanceId == null || selectedAppearanceId.isBlank() ? currentAppearanceId() : selectedAppearanceId;
        for (int index = 0; index < skins.size(); index++) {
            if (skins.get(index).toString().equals(current)) {
                return index;
            }
        }
        return 0;
    }

    private void scrollSkinGallery(int delta) {
        int maxOffset = maxSkinScrollOffset();
        skinPage = Math.max(0, Math.min(maxOffset, skinPage + delta));
    }

    private boolean hasNextSkinScroll() {
        return skinPage < maxSkinScrollOffset();
    }

    private int maxSkinScrollOffset() {
        return Math.max(0, availableSkins().size() - IDENTITY_VISIBLE_SKINS);
    }

    private void alignSkinScrollToSelection() {
        int selectedIndex = selectedAppearanceIndex();
        if (selectedIndex < skinPage) {
            skinPage = selectedIndex;
            return;
        }
        int lastVisible = skinPage + IDENTITY_VISIBLE_SKINS - 1;
        if (selectedIndex > lastVisible) {
            skinPage = Math.max(0, selectedIndex - IDENTITY_VISIBLE_SKINS + 1);
        }
    }

    private int clickedIdentitySkin(double mouseX, double mouseY) {
        int startX = identityGalleryX();
        int startY = identityGalleryY();
        int tileWidth = identityTileWidth();
        List<ResourceLocation> skins = availableSkins();
        int visible = Math.min(IDENTITY_VISIBLE_SKINS, Math.max(0, skins.size() - skinPage));
        for (int slot = 0; slot < visible; slot++) {
            int tileX = startX + slot * (tileWidth + IDENTITY_TILE_GAP);
            if (mouseX >= tileX && mouseX < tileX + tileWidth
                    && mouseY >= startY && mouseY < startY + IDENTITY_TILE_HEIGHT) {
                return skinPage + slot;
            }
        }
        return -1;
    }

    private boolean clickedIdentityScrollbar(double mouseX, double mouseY) {
        int x = identityScrollbarX();
        int y = identityScrollbarY();
        int width = identityScrollbarWidth();
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + IDENTITY_SCROLLBAR_HEIGHT) {
            return false;
        }
        int maxOffset = maxSkinScrollOffset();
        if (maxOffset <= 0) {
            return true;
        }
        float ratio = (float) ((mouseX - x) / Math.max(1.0D, width - 1.0D));
        skinPage = Math.max(0, Math.min(maxOffset, Math.round(ratio * maxOffset)));
        return true;
    }

    private ResourceLocation selectedAppearanceResource() {
        String current = selectedAppearanceId == null || selectedAppearanceId.isBlank() ? currentAppearanceId() : selectedAppearanceId;
        if (!current.isBlank()) {
            ResourceLocation parsed = ResourceLocation.tryParse(current);
            if (parsed != null) {
                return parsed;
            }
        }
        List<ResourceLocation> skins = availableSkins();
        return skins.isEmpty() ? null : skins.getFirst();
    }

    private void renderHeader(GuiGraphics guiGraphics) {
        int x = headerX() + PANEL_TEXT_PADDING_X;
        int y = headerY() + PANEL_TEXT_PADDING_Y;
        guiGraphics.drawString(font, currentNpcDisplayName(), x, y, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, menu.npcTypeLabel(), x, y + 12, ACCENT, false);
        Component trust = Component.translatable("screen.cyberneticenhancements.fixer.trust", trustLabel());
        Component nextTrust = nextTrustLabel();
        int rightX = headerX() + headerWidth() - PANEL_TEXT_PADDING_X;
        guiGraphics.drawString(font, trust, rightX - font.width(trust), y, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, nextTrust, rightX - font.width(nextTrust), y + 12, TEXT_SECONDARY, false);
        drawTrustBar(guiGraphics, x, headerY() + 32, headerWidth() - PANEL_TEXT_PADDING_X * 2, TRUST_BAR_HEIGHT);
    }

    private void renderStatus(GuiGraphics guiGraphics) {
        int x = statusX() + PANEL_TEXT_PADDING_X;
        int rowY = statusY() + 8;
        if (mode == ConversationMode.IDENTITY) {
            int totalSkins = Math.max(1, availableSkins().size());
            Component unlockState = menu.identityUnlocked()
                    ? Component.translatable("screen.cyberneticenhancements.npc.identity.unlocked")
                    : Component.translatable("screen.cyberneticenhancements.npc.identity.locked");
            Component appearanceState = Component.translatable("screen.cyberneticenhancements.npc.identity.appearance_state", selectedAppearanceIndex() + 1, totalSkins);
            guiGraphics.drawString(font, trimStyled(unlockState, statusWidth() - PANEL_TEXT_PADDING_X * 2), x, rowY, menu.identityUnlocked() ? ACCENT : TEXT_SECONDARY, false);
            guiGraphics.drawString(font, trimStyled(appearanceState, statusWidth() - PANEL_TEXT_PADDING_X * 2), x, rowY + 14, TEXT_SECONDARY, false);
            return;
        }
        if (mode == ConversationMode.CONTRACTS) {
            if (menu.hasActiveContract()) {
                Component titleLine = Component.translatable("screen.cyberneticenhancements.fixer.contract.active", menu.activeContractTitle());
                Component objectiveLine = menu.activeContractObjective();
                Component rewardLine = Component.translatable("screen.cyberneticenhancements.fixer.contract.reward", menu.activeContractRewardSummary());
                Component locationLine = menu.activeContractLocationSummary();
                guiGraphics.drawString(font, trimStyled(titleLine, statusWidth() - PANEL_TEXT_PADDING_X * 2), x, rowY, TEXT_SECONDARY, false);
                guiGraphics.drawString(font, trimStyled(objectiveLine, statusWidth() - PANEL_TEXT_PADDING_X * 2), x, rowY + 12, TEXT_SECONDARY, false);
                Component thirdLine = locationLine.getString().isBlank() ? rewardLine : locationLine;
                int thirdColor = locationLine.getString().isBlank() ? ACCENT : StationScreenStyle.TEXT_PRIMARY;
                guiGraphics.drawString(font, trimStyled(thirdLine, statusWidth() - PANEL_TEXT_PADDING_X * 2), x, rowY + 24, thirdColor, false);
            } else {
                Component titleLine = Component.translatable("screen.cyberneticenhancements.fixer.contract.board");
                Component rewardLine = Component.translatable("screen.cyberneticenhancements.fixer.contract.board_note");
                guiGraphics.drawString(font, titleLine, x, rowY, TEXT_SECONDARY, false);
                guiGraphics.drawString(font, trimStyled(rewardLine, statusWidth() - PANEL_TEXT_PADDING_X * 2), x, rowY + 12, ACCENT, false);
            }
            return;
        }
        Component wallet = Component.translatable("screen.cyberneticenhancements.fixer.summary.wallet", menu.balance());
        Component discount = Component.translatable("screen.cyberneticenhancements.fixer.summary.discount", discountLabel());
        guiGraphics.drawString(font, wallet, x, rowY + 4, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, discount, statusX() + statusWidth() - PANEL_TEXT_PADDING_X - font.width(discount), rowY + 4, ACCENT, false);
    }

    private void renderTranscript(GuiGraphics guiGraphics) {
        if (mode == ConversationMode.IDENTITY) {
            renderIdentityEditor(guiGraphics);
            return;
        }
        int x = transcriptX() + PANEL_TEXT_PADDING_X;
        int y = transcriptY() + PANEL_TEXT_PADDING_Y;
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.fixer.section.transcript"), x, y, TEXT_PRIMARY, false);

        int bubbleWidth = transcriptWidth() - PANEL_TEXT_PADDING_X * 2;
        int contentTop = y + font.lineHeight + TRANSCRIPT_TITLE_GAP;
        int contentBottom = transcriptY() + transcriptHeight() - PANEL_TEXT_PADDING_Y;
        int availableBubbleHeight = Math.max(80, contentBottom - contentTop - TRANSCRIPT_BUBBLE_GAP);
        int bubbleHeight = availableBubbleHeight / 2;
        int secondBubbleY = contentTop + bubbleHeight + TRANSCRIPT_BUBBLE_GAP;

        drawSpeakerBlock(guiGraphics, Component.translatable("screen.cyberneticenhancements.fixer.speaker.player"), Component.translatable(lastPlayerLineKey), x, contentTop, bubbleWidth, bubbleHeight, 0xFF15252D, TEXT_PRIMARY);
        drawSpeakerBlock(guiGraphics, Component.literal(currentNpcDisplayName()), npcText(), x, secondBubbleY, bubbleWidth, bubbleHeight, 0xFF162E2A, ACCENT);
    }

    private void renderIdentityEditor(GuiGraphics guiGraphics) {
        int x = transcriptX() + PANEL_TEXT_PADDING_X;
        int y = transcriptY() + PANEL_TEXT_PADDING_Y;
        int maxWidth = transcriptWidth() - PANEL_TEXT_PADDING_X * 2;
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.npc.identity.title"), x, y, TEXT_PRIMARY, false);
        drawWrappedText(guiGraphics, Component.translatable("screen.cyberneticenhancements.npc.identity.subtitle"), x, y + 12, maxWidth, TEXT_SECONDARY, 1);

        int labelY = y + IDENTITY_LABEL_Y_OFFSET;
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.npc.identity.nickname"), x, labelY, ACCENT, false);

        int galleryX = identityGalleryX();
        int galleryY = identityGalleryY();
        int tileWidth = identityTileWidth();
        List<ResourceLocation> skins = availableSkins();
        String current = selectedAppearanceId == null || selectedAppearanceId.isBlank() ? currentAppearanceId() : selectedAppearanceId;
        int visible = Math.min(IDENTITY_VISIBLE_SKINS, Math.max(0, skins.size() - skinPage));
        for (int slot = 0; slot < visible; slot++) {
            int index = skinPage + slot;
            int tileX = galleryX + slot * (tileWidth + IDENTITY_TILE_GAP);
            boolean selected = skins.get(index).toString().equals(current);
            int border = selected ? ACCENT : PANEL_EDGE;
            guiGraphics.fill(tileX, galleryY, tileX + tileWidth, galleryY + IDENTITY_TILE_HEIGHT, PANEL_BG);
            guiGraphics.fill(tileX, galleryY, tileX + tileWidth, galleryY + 1, border);
            guiGraphics.fill(tileX, galleryY + IDENTITY_TILE_HEIGHT - 1, tileX + tileWidth, galleryY + IDENTITY_TILE_HEIGHT, PANEL_DEEP);
            guiGraphics.fill(tileX, galleryY, tileX + 1, galleryY + IDENTITY_TILE_HEIGHT, border);
            guiGraphics.fill(tileX + tileWidth - 1, galleryY, tileX + tileWidth, galleryY + IDENTITY_TILE_HEIGHT, PANEL_DEEP);
            renderNpcModel(guiGraphics, skins.get(index), tileX + 3, galleryY + 3, tileWidth - 6, IDENTITY_TILE_HEIGHT - 6, false);
        }
        renderIdentityScrollbar(guiGraphics);
    }

    private void renderIdentityScrollbar(GuiGraphics guiGraphics) {
        int x = identityScrollbarX();
        int y = identityScrollbarY();
        int width = identityScrollbarWidth();
        drawPanel(guiGraphics, x, y, width, IDENTITY_SCROLLBAR_HEIGHT, PANEL_BG);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + IDENTITY_SCROLLBAR_HEIGHT - 1, PANEL_DEEP);
        int total = Math.max(1, availableSkins().size());
        int visible = Math.min(IDENTITY_VISIBLE_SKINS, total);
        int trackWidth = width - 2;
        int thumbWidth = Math.max(18, Math.round(trackWidth * (visible / (float) total)));
        int maxOffset = maxSkinScrollOffset();
        int thumbX = x + 1;
        if (maxOffset > 0) {
            thumbX += Math.round((trackWidth - thumbWidth) * (skinPage / (float) maxOffset));
        }
        guiGraphics.fill(thumbX, y + 1, thumbX + thumbWidth, y + IDENTITY_SCROLLBAR_HEIGHT - 1, ACCENT);
    }

    private void renderNpcModel(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int renderWidth, int renderHeight, boolean preview) {
        if (minecraft == null || minecraft.level == null) {
            return;
        }
        AbstractCityNpcEntity entity = previewNpc(texture);
        float centerX = x + renderWidth * 0.5F;
        float bottomY = y + renderHeight - (preview ? 13.0F : 10.0F);
        float scale = preview
                ? Math.max(42.0F, Math.min(renderWidth * 0.57F, renderHeight * 0.50F))
                : Math.max(21.0F, Math.min(renderWidth * 0.50F, renderHeight * 0.38F));
        Vector3f translation = new Vector3f(0.0F, preview ? entity.getBbHeight() * 0.28F : entity.getBbHeight() * 0.20F, 0.0F);
        Quaternionf bodyRotation = Axis.ZP.rotationDegrees(180.0F);
        bodyRotation.mul(Axis.YP.rotationDegrees(180.0F));
        bodyRotation.mul(Axis.XP.rotationDegrees(preview ? 6.0F : 4.0F));
        Quaternionf cameraRotation = new Quaternionf(bodyRotation).conjugate();
        InventoryScreen.renderEntityInInventory(guiGraphics, centerX, bottomY, scale, translation, cameraRotation, bodyRotation, entity);
    }

    private AbstractCityNpcEntity previewNpc(ResourceLocation texture) {
        if (previewNpc == null || previewNpc.level() != minecraft.level) {
            previewNpc = createPreviewNpc();
        }
        NpcCategory category = categoryForTexture(texture);
        previewNpc.applyClientPreviewIdentity(texture.toString(), category, currentNpcDisplayName(), selectedNameColorId);
        previewNpc.setYRot(0.0F);
        previewNpc.setYBodyRot(0.0F);
        previewNpc.yBodyRotO = 0.0F;
        previewNpc.setYHeadRot(0.0F);
        previewNpc.yHeadRotO = 0.0F;
        previewNpc.setXRot(0.0F);
        previewNpc.xRotO = 0.0F;
        previewNpc.walkAnimation.setSpeed(0.0F);
        return previewNpc;
    }

    private AbstractCityNpcEntity createPreviewNpc() {
        return switch (menu.npcTypeId()) {
            case "ripperdoc" -> new RipperdocEntity(ModEntityTypes.RIPPERDOC.get(), minecraft.level);
            case "techie" -> new TechieEntity(ModEntityTypes.TECHIE.get(), minecraft.level);
            case "netrunner" -> new NetrunnerEntity(ModEntityTypes.NETRUNNER.get(), minecraft.level);
            case "merc" -> new MercEntity(ModEntityTypes.MERC.get(), minecraft.level);
            default -> new FixerEntity(ModEntityTypes.FIXER.get(), minecraft.level);
        };
    }

    private NpcCategory categoryForTexture(ResourceLocation texture) {
        String path = texture.getPath();
        if (path.contains("/female/")) {
            return NpcCategory.FEMALE;
        }
        if (path.contains("/male/")) {
            return NpcCategory.MALE;
        }
        return NpcCategory.GENERIC;
    }

    private void drawSpeakerBlock(GuiGraphics guiGraphics, Component speaker, Component line, int x, int y, int width, int height, int fill, int speakerColor) {
        guiGraphics.fill(x, y, x + width, y + height, fill);
        guiGraphics.fill(x, y, x + width, y + 1, PANEL_EDGE);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, PANEL_DEEP);
        guiGraphics.drawString(font, speaker, x + 8, y + 6, speakerColor, false);
        int availableTextHeight = Math.max(0, height - 22);
        int maxLines = Math.max(1, Math.min(3, availableTextHeight / (font.lineHeight + 2)));
        drawWrappedText(guiGraphics, line, x + 8, y + 18, width - 16, TEXT_SECONDARY, maxLines);
    }

    private void renderChoiceHeader(GuiGraphics guiGraphics) {
        int x = choicesX() + PANEL_TEXT_PADDING_X;
        int y = choicesY() + PANEL_TEXT_PADDING_Y;
        Component header = mode == ConversationMode.DIALOGUE
                ? Component.translatable("screen.cyberneticenhancements.npc.section.choices." + menu.npcTypeId())
                : Component.translatable(mode.labelKey);
        guiGraphics.drawString(font, header, x, y, TEXT_PRIMARY, false);
    }

    private Component npcText() {
        if (mode == ConversationMode.DIALOGUE) {
            return Component.translatable(currentNpcLineKey());
        }
        return switch (menu.responseId()) {
            case FixerDialogMenu.RESPONSE_INTRO -> Component.translatable(responseKey("intro"));
            case FixerDialogMenu.RESPONSE_CACHE_INTEL -> Component.translatable(responseKey("cache_intel"));
            case FixerDialogMenu.RESPONSE_STABILIZE_OK -> Component.translatable(responseKey("stabilize_ok"));
            case FixerDialogMenu.RESPONSE_NEED_FUNDS -> Component.translatable(responseKey("need_funds"));
            case FixerDialogMenu.RESPONSE_PURGE_LOCKED -> Component.translatable(responseKey("purge_locked"));
            case FixerDialogMenu.RESPONSE_PURGE_COOLDOWN -> Component.translatable(responseKey("purge_cooldown"));
            case FixerDialogMenu.RESPONSE_PURGE_OK -> Component.translatable(responseKey("purge_ok"));
            case FixerDialogMenu.RESPONSE_MAXDOC_OK -> Component.translatable(responseKey("stock_1_ok"));
            case FixerDialogMenu.RESPONSE_RAM_JOLT_OK -> Component.translatable(responseKey("stock_2_ok"));
            case FixerDialogMenu.RESPONSE_CHROME_SUPPRESSANT_OK -> Component.translatable(responseKey("stock_3_ok"));
            case FixerDialogMenu.RESPONSE_IMMUNOBLOCKERS_LOCKED -> Component.translatable(responseKey("stock_4_locked"));
            case FixerDialogMenu.RESPONSE_IMMUNOBLOCKERS_OK -> Component.translatable(responseKey("stock_4_ok"));
            case FixerDialogMenu.RESPONSE_COOL_OFF -> Component.translatable(responseKey("cool_off"));
            case FixerDialogMenu.RESPONSE_BUSY_STATE -> Component.translatable(responseKey("busy_state"));
            case FixerDialogMenu.RESPONSE_CONTRACTS -> Component.translatable(responseKey("contracts"));
            case FixerDialogMenu.RESPONSE_CONTRACT_ACCEPTED -> Component.translatable(responseKey("contract_accepted"));
            case FixerDialogMenu.RESPONSE_CONTRACT_TURNED_IN -> Component.translatable(responseKey("contract_turned_in"));
            case FixerDialogMenu.RESPONSE_CONTRACT_NOT_READY -> Component.translatable(responseKey("contract_not_ready"));
            case FixerDialogMenu.RESPONSE_CONTRACT_MISSING_ITEMS -> Component.translatable(responseKey("contract_missing_items"));
            case FixerDialogMenu.RESPONSE_CONTRACT_ABANDONED -> Component.translatable(responseKey("contract_abandoned"));
            case FixerDialogMenu.RESPONSE_CONTRACT_MAX_ACTIVE -> Component.translatable(responseKey("contract_max_active"));
            case FixerDialogMenu.RESPONSE_CONTRACT_ALREADY_ACTIVE -> Component.translatable(responseKey("contract_already_active"));
            case FixerDialogMenu.RESPONSE_IDENTITY_LOCKED -> Component.translatable(responseKey("identity_locked"));
            case FixerDialogMenu.RESPONSE_IDENTITY_NICKNAME_OK -> Component.translatable(responseKey("identity_name_ok"));
            case FixerDialogMenu.RESPONSE_IDENTITY_APPEARANCE_OK -> Component.translatable(responseKey("identity_appearance_ok"));
            case FixerDialogMenu.RESPONSE_IDENTITY_COLOR_OK -> Component.translatable(responseKey("identity_color_ok"));
            case FixerDialogMenu.RESPONSE_IDENTITY_INVALID -> Component.translatable(responseKey("identity_invalid"));
            default -> Component.translatable(localNpcLineKey);
        };
    }

    private void drawWrappedText(GuiGraphics guiGraphics, Component text, int x, int y, int width, int color, int maxLines) {
        List<FormattedCharSequence> lines = font.split(text, width);
        int lineCount = Math.min(maxLines, lines.size());
        for (int index = 0; index < lineCount; index++) {
            guiGraphics.drawString(font, lines.get(index), x, y + index * (font.lineHeight + 2), color, false);
        }
    }

    private FormattedCharSequence trimStyled(Component text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return Language.getInstance().getVisualOrder(text);
        }
        int ellipsisWidth = font.width("...");
        FormattedText base = font.substrByWidth(text, Math.max(0, maxWidth - ellipsisWidth));
        FormattedText combined = FormattedText.composite(base, Component.literal("..."));
        return Language.getInstance().getVisualOrder(combined);
    }

    private void drawPanel(GuiGraphics guiGraphics, int x, int y, int width, int height, int fill) {
        guiGraphics.fill(x, y, x + width, y + height, fill);
        guiGraphics.fill(x, y, x + width, y + 1, PANEL_EDGE);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, PANEL_DEEP);
        guiGraphics.fill(x, y, x + 1, y + height, PANEL_EDGE);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, PANEL_DEEP);
    }

    private Component trustLabel() {
        return Component.translatable("screen.cyberneticenhancements.fixer.trust." + Math.max(0, Math.min(4, menu.trustLevel())));
    }

    private void drawTrustBar(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int progress = menu.trustProgressPercent();
        int fillWidth = Math.round((width - 2) * (progress / 100.0F));
        guiGraphics.fill(x, y, x + width, y + height, PANEL_DEEP);
        guiGraphics.fill(x, y, x + width, y + 1, PANEL_EDGE);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, PANEL_EDGE);
        guiGraphics.fill(x, y, x + 1, y + height, PANEL_EDGE);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, PANEL_EDGE);
        if (fillWidth > 0) {
            guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + height - 1, ACCENT);
        }
        for (int threshold : new int[]{20, 45, 70, 90}) {
            int markerX = x + 1 + Math.round((width - 2) * (threshold / 100.0F));
            int markerColor = progress >= threshold ? TEXT_PRIMARY : PANEL_EDGE;
            guiGraphics.fill(markerX, y + 1, markerX + 1, y + height - 1, markerColor);
        }
    }

    private Component nextTrustLabel() {
        return switch (menu.trustLevel()) {
            case 0 -> Component.translatable("screen.cyberneticenhancements.fixer.trust.next", Component.translatable("screen.cyberneticenhancements.fixer.trust.1"), 20);
            case 1 -> Component.translatable("screen.cyberneticenhancements.fixer.trust.next", Component.translatable("screen.cyberneticenhancements.fixer.trust.2"), 45);
            case 2 -> Component.translatable("screen.cyberneticenhancements.fixer.trust.next", Component.translatable("screen.cyberneticenhancements.fixer.trust.3"), 70);
            case 3 -> Component.translatable("screen.cyberneticenhancements.fixer.trust.next", Component.translatable("screen.cyberneticenhancements.fixer.trust.4"), 90);
            default -> Component.translatable("screen.cyberneticenhancements.fixer.trust.next_max");
        };
    }

    private Component discountLabel() {
        int discount = menu.trustDiscountPercent();
        return discount <= 0
                ? Component.translatable("screen.cyberneticenhancements.fixer.summary.discount.none")
                : Component.translatable("screen.cyberneticenhancements.fixer.summary.discount.value", discount);
    }

    private int headerX() {
        return leftPos + CONTENT_PADDING;
    }

    private int headerY() {
        return topPos + CONTENT_PADDING;
    }

    private int headerWidth() {
        return imageWidth - CONTENT_PADDING * 2;
    }

    private int headerHeight() {
        return HEADER_HEIGHT;
    }

    private int statusX() {
        return headerX();
    }

    private int statusY() {
        return headerY() + headerHeight() + PANEL_GAP;
    }

    private int statusWidth() {
        return headerWidth();
    }

    private int statusHeight() {
        return STATUS_HEIGHT;
    }

    private int transcriptX() {
        return headerX();
    }

    private int transcriptY() {
        return statusY() + statusHeight() + PANEL_GAP;
    }

    private int transcriptWidth() {
        return headerWidth();
    }

    private int transcriptHeight() {
        return imageHeight - CONTENT_PADDING * 2 - HEADER_HEIGHT - STATUS_HEIGHT - CHOICES_HEIGHT - PANEL_GAP * 3;
    }

    private int identityFieldX() {
        return transcriptX() + PANEL_TEXT_PADDING_X;
    }

    private int identityFieldY() {
        return transcriptY() + PANEL_TEXT_PADDING_Y + IDENTITY_FIELD_Y_OFFSET;
    }

    private int identityColorButtonsX() {
        return identityFieldX() + Math.min(IDENTITY_FIELD_WIDTH, transcriptWidth() - PANEL_TEXT_PADDING_X * 2) + 8;
    }

    private int identityGalleryX() {
        return transcriptX() + PANEL_TEXT_PADDING_X;
    }

    private int identityGalleryY() {
        return transcriptY() + PANEL_TEXT_PADDING_Y + IDENTITY_GALLERY_Y_OFFSET;
    }

    private int identityScrollbarX() {
        return identityGalleryX();
    }

    private int identityScrollbarY() {
        return identityGalleryY() + IDENTITY_TILE_HEIGHT + 10;
    }

    private int identityScrollbarWidth() {
        return transcriptWidth() - PANEL_TEXT_PADDING_X * 2;
    }

    private int identityTileWidth() {
        int availableWidth = transcriptWidth() - PANEL_TEXT_PADDING_X * 2;
        int totalGapWidth = (IDENTITY_VISIBLE_SKINS - 1) * IDENTITY_TILE_GAP;
        return Math.max(44, (availableWidth - totalGapWidth) / IDENTITY_VISIBLE_SKINS);
    }

    private int choicesX() {
        return headerX();
    }

    private int choicesY() {
        return transcriptY() + transcriptHeight() + PANEL_GAP;
    }

    private int choicesWidth() {
        return headerWidth();
    }

    private int choicesHeight() {
        return CHOICES_HEIGHT;
    }

    private enum ConversationMode {
        DIALOGUE("screen.cyberneticenhancements.fixer.mode.dialogue"),
        SERVICES("screen.cyberneticenhancements.fixer.mode.services"),
        SHOP("screen.cyberneticenhancements.fixer.mode.shop"),
        CONTRACTS("screen.cyberneticenhancements.fixer.mode.contracts"),
        IDENTITY("screen.cyberneticenhancements.npc.mode.identity");

        private final String labelKey;

        ConversationMode(String labelKey) {
            this.labelKey = labelKey;
        }
    }

    private record ChoiceSpec(Component message, boolean enabled, Runnable action) {
    }

    private record NameColorPreset(String colorId, int rgb) {
    }

    private final class ChoiceButton extends Button {
        private ChoiceSpec spec;

        private ChoiceButton(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), button -> {
                if (((ChoiceButton) button).spec != null) {
                    ((ChoiceButton) button).spec.action().run();
                }
            }, DEFAULT_NARRATION);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int fill = !active ? 0xFF172028 : isHoveredOrFocused() ? 0xFF173B39 : PANEL_BG;
            int top = !active ? PANEL_EDGE : isHoveredOrFocused() ? ACCENT : PANEL_EDGE;
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, fill);
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + 1, top);
            guiGraphics.fill(getX(), getY() + height - 1, getX() + width, getY() + height, PANEL_DEEP);
            guiGraphics.fill(getX(), getY(), getX() + 1, getY() + height, PANEL_EDGE);
            guiGraphics.fill(getX() + width - 1, getY(), getX() + width, getY() + height, PANEL_DEEP);
            Component message = getMessage();
            int textX = getX() + 8;
            int textY = getY() + (height - 8) / 2;
            if (font.width(message) > width - 16) {
                String trimmed = font.plainSubstrByWidth(message.getString(), Math.max(0, width - 20 - font.width("..."))) + "...";
                guiGraphics.drawString(font, trimmed, textX, textY, active ? TEXT_PRIMARY : TEXT_SECONDARY, false);
                return;
            }
            guiGraphics.drawString(font, message, textX, textY, active ? TEXT_PRIMARY : TEXT_SECONDARY, false);
        }
    }

    private final class ColorPresetButton extends Button {
        private final NameColorPreset preset;
        private boolean selected;

        private ColorPresetButton(int x, int y, int width, int height, NameColorPreset preset) {
            super(x, y, width, height, Component.empty(), button -> {
                selectedNameColorId = ((ColorPresetButton) button).preset.colorId();
                PacketDistributor.sendToServer(new NpcIdentityNameColorPayload(((ColorPresetButton) button).preset.colorId()));
            }, DEFAULT_NARRATION);
            this.preset = preset;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int fill = active ? preset.rgb() : 0xFF2B2F36;
            int top = selected ? ACCENT : isHoveredOrFocused() ? TEXT_PRIMARY : PANEL_EDGE;
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, fill);
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + 1, top);
            guiGraphics.fill(getX(), getY() + height - 1, getX() + width, getY() + height, PANEL_DEEP);
            guiGraphics.fill(getX(), getY(), getX() + 1, getY() + height, top);
            guiGraphics.fill(getX() + width - 1, getY(), getX() + width, getY() + height, PANEL_DEEP);
        }
    }
}
