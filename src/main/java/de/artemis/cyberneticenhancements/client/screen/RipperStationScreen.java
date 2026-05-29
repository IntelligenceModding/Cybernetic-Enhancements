package de.artemis.cyberneticenhancements.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.client.tooltip.ModTooltipStyle;
import de.artemis.cyberneticenhancements.client.tooltip.UpgradeProgressClientTooltip;
import de.artemis.cyberneticenhancements.client.tooltip.UpgradeProgressTooltip;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlot;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlotType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import de.artemis.cyberneticenhancements.common.menu.RipperStationLayout;
import de.artemis.cyberneticenhancements.common.menu.RipperStationMenu;
import de.artemis.cyberneticenhancements.common.network.UpgradeCyberwareSlotPayload;
import de.artemis.cyberneticenhancements.common.network.UpgradeRipperSubSlotPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RipperStationScreen extends AbstractContainerScreen<RipperStationMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            CyberneticEnhancements.MOD_ID,
            "textures/gui/container/ripper_station.png");
    private static final ResourceLocation HIGHLIGHT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            CyberneticEnhancements.MOD_ID,
            "textures/gui/selection_overlay.png");
    private static final int PANEL_ACCENT = 0xFF1EE2B5;
    private static final int SLOT_BACKGROUND = 0xFF202A34;
    private static final int SLOT_ACTIVE = 0xFF2D4A4F;
    private static final int TEXT_PRIMARY = 0xFFE5F2FF;
    private static final int TEXT_SECONDARY = 0xFF9FB5C7;
    private static final int WARNING = 0xFFFF6B6B;
    private static final int HOVER_HIGHLIGHT_TINT = 0x3856B8FF;
    private static final float HOVER_HIGHLIGHT_SCALE = 1.0035F;
    private static final int LEFT_PANEL_X = 12;
    private static final int CENTER_PANEL_X = 200;
    private static final int RIGHT_PANEL_X = 610;
    private static final int TOP_PANEL_Y = 12;
    private static final int LOWER_PANEL_Y = 258;
    private static final int PANEL_TEXT_PADDING_X = 6;
    private static final int PANEL_TEXT_PADDING_Y = 8;
    private static final int CHROME_TANK_X1 = 12;
    private static final int CHROME_TANK_Y1 = 338;
    private static final int CHROME_TANK_X2 = 189;
    private static final int CHROME_TANK_Y2 = 443;
    private static final int INTEGRITY_TANK_X1 = 610;
    private static final int INTEGRITY_TANK_Y1 = 338;
    private static final int INTEGRITY_TANK_X2 = 787;
    private static final int INTEGRITY_TANK_Y2 = 443;
    private static final int CHROME_TANK_INSET = 4;
    private static final int TANK_FRAME = 0xFF304250;
    private static final int TANK_FRAME_HIGHLIGHT = 0xFF4C667A;
    private static final int TANK_INTERIOR = 0xAA0C131A;
    private static final int TANK_SEGMENT = 0x443A4A58;
    private static final long SLOT_UPGRADE_HOLD_MS = 5000L;
    private static final int MATRIX_MODEL_CENTER_X = 400;
    private static final int MATRIX_MODEL_CENTER_Y = 130;
    private static final int MATRIX_MODEL_HALF_WIDTH = 84;
    private static final int MATRIX_MODEL_HALF_HEIGHT = 97;
    private static final int MATRIX_MODEL_X1 = MATRIX_MODEL_CENTER_X - MATRIX_MODEL_HALF_WIDTH;
    private static final int MATRIX_MODEL_Y1 = MATRIX_MODEL_CENTER_Y - MATRIX_MODEL_HALF_HEIGHT;
    private static final int MATRIX_MODEL_X2 = MATRIX_MODEL_CENTER_X + MATRIX_MODEL_HALF_WIDTH;
    private static final int MATRIX_MODEL_Y2 = MATRIX_MODEL_CENTER_Y + MATRIX_MODEL_HALF_HEIGHT;
    private static final int MATRIX_MODEL_SCALE = 58;
    private static final float MATRIX_MODEL_Y_OFFSET = 0.0625F;
    private UpgradeTarget heldUpgradeTarget;
    private CyberwareTier heldUpgradeStartTier;
    private long heldUpgradeStartMs;
    private boolean upgradeTriggeredThisHold;

    private enum UpgradeTargetKind {
        CYBERWARE,
        CHIPWARE,
        ARM_MODULE,
        LEG_MODULE
    }

    private record UpgradeTarget(UpgradeTargetKind kind, int primaryIndex, int slotIndex) {
    }

    private record SlotTooltip(List<Component> lines, Optional<TooltipComponent> visualComponent) {
    }

    public RipperStationScreen(RipperStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = RipperStationLayout.IMAGE_WIDTH;
        this.imageHeight = RipperStationLayout.IMAGE_HEIGHT;
        this.inventoryLabelX = RipperStationLayout.PLAYER_INVENTORY_X;
        this.inventoryLabelY = RipperStationLayout.PLAYER_INVENTORY_Y - 12;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x0 = leftPos;
        int y0 = topPos;
        guiGraphics.blit(BACKGROUND_TEXTURE, x0, y0, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        drawPlayerModel(guiGraphics, mouseX, mouseY);
        drawPlayerSlotBacks(guiGraphics);
        drawSlotFrames(guiGraphics);
        drawStatusTanks(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_SECONDARY, false);

        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.body_overview"), LEFT_PANEL_X + PANEL_TEXT_PADDING_X, TOP_PANEL_Y + PANEL_TEXT_PADDING_Y, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.system_summary"), RIGHT_PANEL_X + PANEL_TEXT_PADDING_X, TOP_PANEL_Y + PANEL_TEXT_PADDING_Y, TEXT_PRIMARY, false);

        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.stage"), getStageLabel(), 18, 46, 178);
        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.chrome"), Component.literal(menu.getInstalledChrome() + " / " + menu.getChromeCapacity()), 18, 64, 178);
        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.cyberstrain"), Component.literal(Integer.toString(menu.getCyberstrain())), 18, 82, 178);
        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.integrity"), Component.literal(menu.getIntegrity() + "%"), 18, 100, 178);
        int overviewY = 118;
        overviewY = drawOverviewSlotLine(guiGraphics,
                Component.translatable("screen.cyberneticenhancements.ripper_station.cyberware_slots"),
                menu.getInstalledCyberwareCount(),
                menu.getTotalCyberwareSlotCount(),
                overviewY);

        int armModuleSlots = menu.getTotalArmModuleSlotCount();
        if (armModuleSlots > 0) {
            overviewY = drawOverviewSlotLine(guiGraphics,
                    Component.translatable("screen.cyberneticenhancements.ripper_station.arm_modules"),
                    menu.getInstalledArmModuleCount(),
                    armModuleSlots,
                    overviewY);
        }

        int legModuleSlots = menu.getTotalLegModuleSlotCount();
        if (legModuleSlots > 0) {
            overviewY = drawOverviewSlotLine(guiGraphics,
                    Component.translatable("screen.cyberneticenhancements.ripper_station.leg_modules"),
                    menu.getInstalledLegModuleCount(),
                    legModuleSlots,
                    overviewY);
        }

        int chipwareSlots = menu.getTotalChipwareSlotCount();
        if (chipwareSlots > 0) {
            drawOverviewSlotLine(guiGraphics,
                    Component.translatable("screen.cyberneticenhancements.ripper_station.chipware"),
                    menu.getInstalledChipwareCount(),
                    chipwareSlots,
                    overviewY);
        }

        renderBodyLabels(guiGraphics);
        renderSystemSummary(guiGraphics);
        renderChipSummary(guiGraphics);
        renderModuleSummary(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        updateHeldSlotUpgrade(mouseX, mouseY);
        renderSlotTooltips(guiGraphics, mouseX, mouseY);
        renderStatusTankTooltip(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && menu.getCarried().isEmpty()) {
            UpgradeTarget hoveredTarget = getHoveredEmptyUpgradeableSlot((int) mouseX, (int) mouseY);
            if (hoveredTarget != null && canUpgrade(hoveredTarget) && hasRequiredUpgradeComponent(hoveredTarget)) {
                heldUpgradeTarget = hoveredTarget;
                heldUpgradeStartTier = getSupportedTier(hoveredTarget);
                heldUpgradeStartMs = System.currentTimeMillis();
                upgradeTriggeredThisHold = false;
                return true;
            }
        }

        clearHeldSlotUpgrade();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean consumed = button == 0 && heldUpgradeTarget != null;
        if (button == 0) {
            clearHeldSlotUpgrade();
        }
        return consumed || super.mouseReleased(mouseX, mouseY, button);
    }

    private void drawStatLine(GuiGraphics guiGraphics, Component label, Component value, int x, int y, int rightX) {
        guiGraphics.drawString(font, label, x, y, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, value, rightX - font.width(value), y, TEXT_PRIMARY, false);
    }

    private int drawOverviewSlotLine(GuiGraphics guiGraphics, Component label, int installed, int total, int y) {
        drawStatLine(guiGraphics, label, Component.literal(installed + " / " + total), 18, y, 178);
        return y + 18;
    }

    private void drawPlayerModel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (minecraft == null || minecraft.player == null) {
            return;
        }

        CyberwareSlotType hoveredType = getHoveredCyberwareType(mouseX, mouseY);

        renderPlayerModelInMatrixFollowsMouse(
                guiGraphics,
                leftPos + MATRIX_MODEL_X1,
                topPos + MATRIX_MODEL_Y1,
                leftPos + MATRIX_MODEL_X2,
                topPos + MATRIX_MODEL_Y2,
                MATRIX_MODEL_SCALE,
                MATRIX_MODEL_Y_OFFSET,
                mouseX,
                mouseY,
                (AbstractClientPlayer) minecraft.player,
                hoveredType
        );
    }

    private void renderPlayerModelInMatrixFollowsMouse(
            GuiGraphics guiGraphics,
            int x1,
            int y1,
            int x2,
            int y2,
            int scale,
            float yOffset,
            float mouseX,
            float mouseY,
            AbstractClientPlayer player,
            CyberwareSlotType hoveredType
    ) {
        float centerX = (float) (x1 + x2) / 2.0F;
        float centerY = (float) (y1 + y2) / 2.0F;
        guiGraphics.enableScissor(x1, y1, x2, y2);
        float yawOffset = (float) Math.atan((centerX - mouseX) / 40.0F);
        float pitchOffset = (float) Math.atan((centerY - mouseY) / 40.0F);
        Quaternionf poseRotation = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf cameraRotation = new Quaternionf().rotateX(pitchOffset * 20.0F * (float) (Math.PI / 180.0));
        poseRotation.mul(cameraRotation);

        float bodyRot = player.yBodyRot;
        float yRot = player.getYRot();
        float xRot = player.getXRot();
        float headRotO = player.yHeadRotO;
        float headRot = player.yHeadRot;
        player.yBodyRot = 180.0F + yawOffset * 20.0F;
        player.setYRot(180.0F + yawOffset * 40.0F);
        player.setXRot(-pitchOffset * 20.0F);
        player.yHeadRot = player.getYRot();
        player.yHeadRotO = player.getYRot();

        float playerScale = player.getScale();
        Vector3f translation = new Vector3f(0.0F, player.getBbHeight() / 2.0F + yOffset * playerScale, 0.0F);
        float renderScale = (float) scale / playerScale;
        renderPlayerModelInMatrix(guiGraphics, centerX, centerY, renderScale, translation, poseRotation, cameraRotation, player, hoveredType);

        player.yBodyRot = bodyRot;
        player.setYRot(yRot);
        player.setXRot(xRot);
        player.yHeadRotO = headRotO;
        player.yHeadRot = headRot;
        guiGraphics.disableScissor();
    }

    private void renderPlayerModelInMatrix(
            GuiGraphics guiGraphics,
            float x,
            float y,
            float scale,
            Vector3f translation,
            Quaternionf poseRotation,
            Quaternionf cameraRotation,
            AbstractClientPlayer player,
            CyberwareSlotType hoveredType
    ) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 50.0F);
        guiGraphics.pose().scale(scale, scale, -scale);
        guiGraphics.pose().translate(translation.x, translation.y, translation.z);
        guiGraphics.pose().mulPose(poseRotation);
        Lighting.setupForEntityInInventory();

        EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        renderDispatcher.overrideCameraOrientation(cameraRotation.conjugate(new Quaternionf()).rotateY((float) Math.PI));
        renderDispatcher.setRenderShadow(false);
        PlayerRenderer playerRenderer = renderDispatcher.getRenderer(player) instanceof PlayerRenderer renderer ? renderer : null;

        RenderSystem.runAsFancy(() -> {
            renderDispatcher.render(player, 0.0, 0.0, 0.0, 0.0F, 1.0F, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880);
            if (hoveredType != null && playerRenderer != null) {
                renderSelectionOverlay(guiGraphics, player, playerRenderer, hoveredType);
            }
        });

        guiGraphics.flush();
        renderDispatcher.setRenderShadow(true);
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    private void renderSelectionOverlay(
            GuiGraphics guiGraphics,
            AbstractClientPlayer player,
            PlayerRenderer playerRenderer,
            CyberwareSlotType hoveredType
    ) {
        VertexConsumer vertexConsumer = guiGraphics.bufferSource().getBuffer(RenderType.entityTranslucent(HIGHLIGHT_TEXTURE));
        int overlay = LivingEntityRenderer.getOverlayCoords(player, 0.0F);
        var model = playerRenderer.getModel();
        Vec3 renderOffset = playerRenderer.getRenderOffset(player, 1.0F);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(renderOffset.x, renderOffset.y, renderOffset.z);
        guiGraphics.pose().scale(player.getScale(), player.getScale(), player.getScale());
        guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(180.0F - player.yBodyRot));
        guiGraphics.pose().scale(-1.0F, -1.0F, 1.0F);
        guiGraphics.pose().scale(0.9375F, 0.9375F, 0.9375F);
        guiGraphics.pose().translate(0.0F, -1.501F, 0.0F);

        switch (hoveredType) {
            case FRONTAL_CORTEX -> renderHeadGroup(model, player, guiGraphics, vertexConsumer, overlay);
            case OPERATING_SYSTEM -> renderTorsoGroup(model, player, guiGraphics, vertexConsumer, overlay);
            case FACE -> renderHeadGroup(model, player, guiGraphics, vertexConsumer, overlay);
            case SKELETON -> renderWholeBaseBody(model, guiGraphics, vertexConsumer, overlay);
            case ARMS -> renderArmsGroup(model, player, guiGraphics, vertexConsumer, overlay);
            case HANDS -> renderHandsGroup(model, player, guiGraphics, vertexConsumer, overlay);
            case NERVOUS_SYSTEM -> renderWholeBaseBody(model, guiGraphics, vertexConsumer, overlay);
            case CIRCULATORY_SYSTEM -> renderCirculatoryGroup(model, player, guiGraphics, vertexConsumer, overlay);
            case INTEGUMENTARY_SYSTEM -> renderOuterBodyGroup(model, player, guiGraphics, vertexConsumer, overlay);
            case LEGS -> renderLegsGroup(model, player, guiGraphics, vertexConsumer, overlay);
        }
        guiGraphics.pose().popPose();
    }

    private void renderWholeBaseBody(
            net.minecraft.client.model.PlayerModel<AbstractClientPlayer> model,
            GuiGraphics guiGraphics,
            VertexConsumer vertexConsumer,
            int overlay
    ) {
        renderHighlightedPart(model.head, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.hat, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.body, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.jacket, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.rightArm, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.leftArm, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.rightSleeve, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.leftSleeve, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.rightLeg, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.leftLeg, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.rightPants, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.leftPants, guiGraphics, vertexConsumer, overlay);
    }

    private void renderHeadGroup(
            net.minecraft.client.model.PlayerModel<AbstractClientPlayer> model,
            AbstractClientPlayer player,
            GuiGraphics guiGraphics,
            VertexConsumer vertexConsumer,
            int overlay
    ) {
        renderHighlightedPart(model.head, guiGraphics, vertexConsumer, overlay);
        if (player.isModelPartShown(PlayerModelPart.HAT)) {
            renderHighlightedPart(model.hat, guiGraphics, vertexConsumer, overlay);
        }
    }

    private void renderTorsoGroup(
            net.minecraft.client.model.PlayerModel<AbstractClientPlayer> model,
            AbstractClientPlayer player,
            GuiGraphics guiGraphics,
            VertexConsumer vertexConsumer,
            int overlay
    ) {
        renderHighlightedPart(model.body, guiGraphics, vertexConsumer, overlay);
        if (player.isModelPartShown(PlayerModelPart.JACKET)) {
            renderHighlightedPart(model.jacket, guiGraphics, vertexConsumer, overlay);
        }
    }

    private void renderCirculatoryGroup(
            net.minecraft.client.model.PlayerModel<AbstractClientPlayer> model,
            AbstractClientPlayer player,
            GuiGraphics guiGraphics,
            VertexConsumer vertexConsumer,
            int overlay
    ) {
        renderTorsoGroup(model, player, guiGraphics, vertexConsumer, overlay);
        renderArmsGroup(model, player, guiGraphics, vertexConsumer, overlay);
        renderLegsGroup(model, player, guiGraphics, vertexConsumer, overlay);
    }

    private void renderArmsGroup(
            net.minecraft.client.model.PlayerModel<AbstractClientPlayer> model,
            AbstractClientPlayer player,
            GuiGraphics guiGraphics,
            VertexConsumer vertexConsumer,
            int overlay
    ) {
        renderHighlightedPart(model.rightArm, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.leftArm, guiGraphics, vertexConsumer, overlay);
        if (player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE)) {
            renderHighlightedPart(model.rightSleeve, guiGraphics, vertexConsumer, overlay);
        }
        if (player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE)) {
            renderHighlightedPart(model.leftSleeve, guiGraphics, vertexConsumer, overlay);
        }
    }

    private void renderHandsGroup(
            net.minecraft.client.model.PlayerModel<AbstractClientPlayer> model,
            AbstractClientPlayer player,
            GuiGraphics guiGraphics,
            VertexConsumer vertexConsumer,
            int overlay
    ) {
        boolean renderedSleeve = false;
        if (player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE)) {
            renderHighlightedPart(model.rightSleeve, guiGraphics, vertexConsumer, overlay);
            renderedSleeve = true;
        }
        if (player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE)) {
            renderHighlightedPart(model.leftSleeve, guiGraphics, vertexConsumer, overlay);
            renderedSleeve = true;
        }
        if (!renderedSleeve) {
            renderHighlightedPart(model.rightArm, guiGraphics, vertexConsumer, overlay);
            renderHighlightedPart(model.leftArm, guiGraphics, vertexConsumer, overlay);
        }
    }

    private void renderOuterBodyGroup(
            net.minecraft.client.model.PlayerModel<AbstractClientPlayer> model,
            AbstractClientPlayer player,
            GuiGraphics guiGraphics,
            VertexConsumer vertexConsumer,
            int overlay
    ) {
        if (player.isModelPartShown(PlayerModelPart.HAT)) {
            renderHighlightedPart(model.hat, guiGraphics, vertexConsumer, overlay);
        }
        if (player.isModelPartShown(PlayerModelPart.JACKET)) {
            renderHighlightedPart(model.jacket, guiGraphics, vertexConsumer, overlay);
        }
        if (player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE)) {
            renderHighlightedPart(model.rightSleeve, guiGraphics, vertexConsumer, overlay);
        }
        if (player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE)) {
            renderHighlightedPart(model.leftSleeve, guiGraphics, vertexConsumer, overlay);
        }
        if (player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG)) {
            renderHighlightedPart(model.rightPants, guiGraphics, vertexConsumer, overlay);
        }
        if (player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG)) {
            renderHighlightedPart(model.leftPants, guiGraphics, vertexConsumer, overlay);
        }
    }

    private void renderLegsGroup(
            net.minecraft.client.model.PlayerModel<AbstractClientPlayer> model,
            AbstractClientPlayer player,
            GuiGraphics guiGraphics,
            VertexConsumer vertexConsumer,
            int overlay
    ) {
        renderHighlightedPart(model.rightLeg, guiGraphics, vertexConsumer, overlay);
        renderHighlightedPart(model.leftLeg, guiGraphics, vertexConsumer, overlay);
        if (player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG)) {
            renderHighlightedPart(model.rightPants, guiGraphics, vertexConsumer, overlay);
        }
        if (player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG)) {
            renderHighlightedPart(model.leftPants, guiGraphics, vertexConsumer, overlay);
        }
    }

    private void renderHighlightedPart(
            ModelPart modelPart,
            GuiGraphics guiGraphics,
            VertexConsumer vertexConsumer,
            int overlay
    ) {
        float xScale = modelPart.xScale;
        float yScale = modelPart.yScale;
        float zScale = modelPart.zScale;
        modelPart.xScale = xScale * HOVER_HIGHLIGHT_SCALE;
        modelPart.yScale = yScale * HOVER_HIGHLIGHT_SCALE;
        modelPart.zScale = zScale * HOVER_HIGHLIGHT_SCALE;
        modelPart.render(guiGraphics.pose(), vertexConsumer, 15728880, overlay, HOVER_HIGHLIGHT_TINT);
        modelPart.xScale = xScale;
        modelPart.yScale = yScale;
        modelPart.zScale = zScale;
    }

    private CyberwareSlotType getHoveredCyberwareType(int mouseX, int mouseY) {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            Slot menuSlot = menu.slots.get(slot.ordinal());
            if (isHovering(menuSlot.x, menuSlot.y, 16, 16, mouseX, mouseY)) {
                return slot.getType();
            }
        }
        return null;
    }

    private void drawSlotFrames(GuiGraphics guiGraphics) {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            drawStationSlotBack(
                    guiGraphics,
                    RipperStationLayout.CYBERWARE_SLOT_X[slot.ordinal()],
                    RipperStationLayout.CYBERWARE_SLOT_Y[slot.ordinal()],
                    true,
                    !menu.getCyberwareStack(slot).isEmpty(),
                    menu.getSupportedTier(slot)
            );
        }

        for (int cluster = 0; cluster < RipperStationLayout.CHIP_CLUSTER_X.length; cluster++) {
            for (int slot = 0; slot < RipperStationLayout.CHIP_SLOT_OFFSET_X.length; slot++) {
                drawStationSlotBack(
                        guiGraphics,
                        RipperStationLayout.CHIP_CLUSTER_X[cluster] + RipperStationLayout.CHIP_SLOT_OFFSET_X[slot],
                        RipperStationLayout.CHIP_SLOT_Y,
                        menu.isChipSlotUnlocked(cluster, slot),
                        !menu.getChipwareStack(cluster, slot).isEmpty(),
                        menu.getChipSupportedTier(cluster, slot)
                );
            }
        }

        for (int slot = 0; slot < RipperStationLayout.ARM_MODULE_X.length; slot++) {
            drawStationSlotBack(guiGraphics, RipperStationLayout.ARM_MODULE_X[slot], RipperStationLayout.ARM_MODULE_Y, menu.isArmModuleSlotUnlocked(slot), !menu.getArmModuleStack(slot).isEmpty(), menu.getArmModuleSupportedTier(slot));
            drawStationSlotBack(guiGraphics, RipperStationLayout.LEG_MODULE_X[slot], RipperStationLayout.LEG_MODULE_Y, menu.isLegModuleSlotUnlocked(slot), !menu.getLegModuleStack(slot).isEmpty(), menu.getLegModuleSupportedTier(slot));
        }
    }

    private void drawStatusTanks(GuiGraphics guiGraphics) {
        drawTank(guiGraphics, CHROME_TANK_X1, CHROME_TANK_Y1, CHROME_TANK_X2, CHROME_TANK_Y2, menu.getChromePercent(), menu.getCyberstrain() > 0 ? WARNING : PANEL_ACCENT);
        drawTank(guiGraphics, INTEGRITY_TANK_X1, INTEGRITY_TANK_Y1, INTEGRITY_TANK_X2, INTEGRITY_TANK_Y2, menu.getIntegrity(), menu.getIntegrity() < 40 ? WARNING : PANEL_ACCENT);
    }

    private void drawTank(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int percent, int fillColor) {
        int tankX1 = leftPos + x1 + CHROME_TANK_INSET;
        int tankY1 = topPos + y1 + CHROME_TANK_INSET;
        int tankX2 = leftPos + x2 - CHROME_TANK_INSET;
        int tankY2 = topPos + y2 - CHROME_TANK_INSET;
        int tankWidth = tankX2 - tankX1;
        int tankHeight = tankY2 - tankY1;
        int fillHeight = Math.max(0, Math.min(tankHeight, percent * tankHeight / 100));
        int fillTop = tankY2 - fillHeight;

        guiGraphics.fill(tankX1, tankY1, tankX2, tankY2, TANK_FRAME);
        guiGraphics.fill(tankX1 + 1, tankY1 + 1, tankX2 - 1, tankY2 - 1, 0xCC121C25);
        guiGraphics.fill(tankX1 + 3, tankY1 + 3, tankX2 - 3, tankY2 - 3, TANK_INTERIOR);

        int cavityX1 = tankX1 + 3;
        int cavityY1 = tankY1 + 3;
        int cavityX2 = tankX2 - 3;
        int cavityY2 = tankY2 - 3;
        int cavityHeight = cavityY2 - cavityY1;
        int segmentCount = 5;
        for (int segment = 1; segment < segmentCount; segment++) {
            int segmentY = cavityY2 - segment * cavityHeight / segmentCount;
            guiGraphics.fill(cavityX1, segmentY, cavityX2, segmentY + 1, TANK_SEGMENT);
        }

        if (fillHeight > 0) {
            guiGraphics.fill(cavityX1, fillTop, cavityX2, cavityY2, fillColor);
            guiGraphics.fill(cavityX1 + 2, fillTop + 2, cavityX2 - 2, cavityY2 - 2, (fillColor & 0x00FFFFFF) | 0x66000000);
            guiGraphics.fill(cavityX1, fillTop, cavityX2, Math.min(cavityY2, fillTop + 3), 0x99E5F2FF);
            guiGraphics.fill(cavityX1 + 1, fillTop + 1, cavityX1 + 5, cavityY2 - 1, 0x335CFFFF);
            guiGraphics.fill(cavityX2 - 5, fillTop + 1, cavityX2 - 1, cavityY2 - 1, 0x220A1A28);

            int fluidHeight = cavityY2 - fillTop;
            int bandCount = Math.max(1, fluidHeight / 18);
            for (int band = 1; band <= bandCount; band++) {
                int bandY = cavityY2 - band * fluidHeight / (bandCount + 1);
                guiGraphics.fill(cavityX1 + 1, bandY, cavityX2 - 1, Math.min(cavityY2, bandY + 1), 0x2AF8FFFF);
            }
        }
        guiGraphics.fill(tankX1 + 1, tankY1 + 1, tankX2 - 1, tankY1 + 2, TANK_FRAME_HIGHLIGHT);
        guiGraphics.fill(tankX1 + 1, tankY1 + 1, tankX1 + 2, tankY2 - 1, TANK_FRAME_HIGHLIGHT);
        guiGraphics.renderOutline(tankX1, tankY1, tankWidth, tankHeight, 0xFF18242E);
    }

    private void renderStatusTankTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHoveringTank(CHROME_TANK_X1, CHROME_TANK_Y1, CHROME_TANK_X2, CHROME_TANK_Y2, mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, List.of(
                    Component.translatable("screen.cyberneticenhancements.ripper_station.chrome"),
                    Component.literal(menu.getInstalledChrome() + " / " + menu.getChromeCapacity()),
                    Component.translatable("screen.cyberneticenhancements.ripper_station.cyberstrain")
                            .append(Component.literal(": " + menu.getCyberstrain()))
            ), Optional.empty(), mouseX, mouseY);
            return;
        }

        if (isHoveringTank(INTEGRITY_TANK_X1, INTEGRITY_TANK_Y1, INTEGRITY_TANK_X2, INTEGRITY_TANK_Y2, mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, List.of(
                    Component.translatable("screen.cyberneticenhancements.ripper_station.integrity"),
                    Component.literal(menu.getIntegrity() + "%")
            ), Optional.empty(), mouseX, mouseY);
        }
    }

    private boolean isHoveringTank(int x1, int y1, int x2, int y2, int mouseX, int mouseY) {
        return isHovering(x1, y1, x2 - x1, y2 - y1, mouseX, mouseY);
    }

    private void renderBodyLabels(GuiGraphics guiGraphics) {
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.OPERATING_SYSTEM, 400, 30);
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.SKELETON, 534, 198);
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.FACE, 534, 30);
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.ARMS, 266, 92);
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.HANDS, 534, 92);
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.CIRCULATORY_SYSTEM, 266, 148);
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.NERVOUS_SYSTEM, 534, 148);
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.INTEGUMENTARY_SYSTEM, 266, 198);
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.LEGS, 266, 30);
        drawCenteredBodyLabel(guiGraphics, CyberwareSlotType.FRONTAL_CORTEX, 400, 198);
    }

    private void drawCenteredBodyLabel(GuiGraphics guiGraphics, CyberwareSlotType slotType, int centerX, int y) {
        Component label = Component.translatable(slotType.translationKey());
        guiGraphics.drawString(font, label, centerX - font.width(label) / 2, y, TEXT_SECONDARY, false);
    }

    private void drawLeftAlignedBodyLabel(GuiGraphics guiGraphics, CyberwareSlotType slotType, int x, int y) {
        guiGraphics.drawString(font, Component.translatable(slotType.translationKey()), x, y, TEXT_SECONDARY, false);
    }

    private void drawRightAlignedBodyLabel(GuiGraphics guiGraphics, CyberwareSlotType slotType, int rightX, int y) {
        Component label = Component.translatable(slotType.translationKey());
        guiGraphics.drawString(font, label, rightX - font.width(label), y, TEXT_SECONDARY, false);
    }

    private void renderSystemSummary(GuiGraphics guiGraphics) {
        int labelX = RIGHT_PANEL_X + PANEL_TEXT_PADDING_X;
        int countRightX = 780;
        int startY = 46;
        CyberwareSlotType[] types = CyberwareSlotType.values();
        for (int i = 0; i < types.length; i++) {
            int y = startY + i * 18;
            CyberwareSlotType type = types[i];
            int installed = menu.getInstalledCount(type);
            Component label = Component.translatable(type.translationKey());
            String value = installed + " / " + type.getBaseSlotCount();
            guiGraphics.drawString(font, label, labelX, y, installed > 0 ? PANEL_ACCENT : TEXT_SECONDARY, false);
            guiGraphics.drawString(font, value, countRightX - font.width(value), y, TEXT_PRIMARY, false);
        }
    }

    private void renderChipSummary(GuiGraphics guiGraphics) {
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.chipware"), CENTER_PANEL_X + PANEL_TEXT_PADDING_X, LOWER_PANEL_Y + PANEL_TEXT_PADDING_Y, TEXT_PRIMARY, false);
        for (int cluster = 0; cluster < RipperStationLayout.CHIP_CLUSTER_X.length; cluster++) {
            String hostName = menu.getChipwareHostName(cluster);
            Component host = hostName.isEmpty()
                    ? Component.translatable("screen.cyberneticenhancements.ripper_station.no_chip_socket")
                    : Component.literal(hostName);
            int clusterCenterX = getSlotBankCenterX(RipperStationLayout.CHIP_CLUSTER_X[cluster], RipperStationLayout.CHIP_SLOT_OFFSET_X);
            int hostY = RipperStationLayout.CHIP_SLOT_Y - 12;
            int hostX = clusterCenterX - font.width(host) / 2;
            guiGraphics.drawString(font, host, hostX, hostY, hostName.isEmpty() ? TEXT_SECONDARY : PANEL_ACCENT, false);
        }
    }

    private void renderModuleSummary(GuiGraphics guiGraphics) {
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.arm_modules"), LEFT_PANEL_X + PANEL_TEXT_PADDING_X, LOWER_PANEL_Y + PANEL_TEXT_PADDING_Y, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.leg_modules"), RIGHT_PANEL_X + PANEL_TEXT_PADDING_X, LOWER_PANEL_Y + PANEL_TEXT_PADDING_Y, TEXT_PRIMARY, false);

        String armHost = menu.getArmModuleHostName();
        Component armHostComponent = armHost.isEmpty()
                ? Component.translatable("screen.cyberneticenhancements.ripper_station.no_arm_cyberware")
                : Component.literal(armHost);
        int armCenterX = getExplicitSlotBankCenterX(RipperStationLayout.ARM_MODULE_X);
        guiGraphics.drawString(font, armHostComponent, armCenterX - font.width(armHostComponent) / 2, RipperStationLayout.ARM_MODULE_Y - 12, armHost.isEmpty() ? TEXT_SECONDARY : PANEL_ACCENT, false);

        String legHost = menu.getLegModuleHostName();
        Component legHostComponent = legHost.isEmpty()
                ? Component.translatable("screen.cyberneticenhancements.ripper_station.no_leg_cyberware")
                : Component.literal(legHost);
        int legCenterX = getExplicitSlotBankCenterX(RipperStationLayout.LEG_MODULE_X);
        guiGraphics.drawString(font, legHostComponent, legCenterX - font.width(legHostComponent) / 2, RipperStationLayout.LEG_MODULE_Y - 12, legHost.isEmpty() ? TEXT_SECONDARY : PANEL_ACCENT, false);
    }

    private int getSlotBankCenterX(int firstSlotX, int[] slotOffsets) {
        return firstSlotX + slotOffsets[slotOffsets.length - 1] / 2 + StationSlotRenderer.SLOT_SIZE / 2;
    }

    private int getExplicitSlotBankCenterX(int[] slotX) {
        return (slotX[0] + slotX[slotX.length - 1] + StationSlotRenderer.SLOT_SIZE) / 2;
    }

    private void renderSlotTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (CyberwareSlot cyberwareSlot : CyberwareSlot.values()) {
            Slot slot = menu.slots.get(cyberwareSlot.ordinal());
            if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && !slot.hasItem()) {
                renderSlotTooltip(guiGraphics, buildCyberwareSlotTooltip(cyberwareSlot), mouseX, mouseY);
                return;
            }
        }

        for (int cluster = 0; cluster < RipperStationLayout.CHIP_CLUSTER_X.length; cluster++) {
            for (int slot = 0; slot < RipperStationLayout.CHIP_SLOT_OFFSET_X.length; slot++) {
                int chipX = RipperStationLayout.CHIP_CLUSTER_X[cluster] + RipperStationLayout.CHIP_SLOT_OFFSET_X[slot];
                if (isHovering(chipX, RipperStationLayout.CHIP_SLOT_Y, 16, 16, mouseX, mouseY)
                        && menu.getChipwareStack(cluster, slot).isEmpty()) {
                    if (!menu.isChipSlotUnlocked(cluster, slot)) {
                        guiGraphics.renderTooltip(font, List.of(
                                Component.translatable("screen.cyberneticenhancements.ripper_station.chipware"),
                                Component.translatable("screen.cyberneticenhancements.ripper_station.chip_slot_locked")
                        ), Optional.empty(), mouseX, mouseY);
                    } else {
                        renderSlotTooltip(guiGraphics, buildSubSlotTooltip(
                                new UpgradeTarget(UpgradeTargetKind.CHIPWARE, cluster, slot),
                                Component.translatable("screen.cyberneticenhancements.ripper_station.chipware"),
                                "screen.cyberneticenhancements.ripper_station.chip_slot_hint"
                        ), mouseX, mouseY);
                    }
                    return;
                }
            }
        }

        for (int i = 0; i < RipperStationLayout.ARM_MODULE_X.length; i++) {
            if (isHovering(RipperStationLayout.ARM_MODULE_X[i], RipperStationLayout.ARM_MODULE_Y, 16, 16, mouseX, mouseY)
                    && menu.getArmModuleStack(i).isEmpty()) {
                if (!menu.isArmModuleSlotUnlocked(i)) {
                    guiGraphics.renderTooltip(font, List.of(
                            Component.translatable("screen.cyberneticenhancements.ripper_station.arm_modules"),
                            Component.translatable("screen.cyberneticenhancements.ripper_station.module_slot_locked")
                    ), Optional.empty(), mouseX, mouseY);
                } else {
                    renderSlotTooltip(guiGraphics, buildSubSlotTooltip(
                            new UpgradeTarget(UpgradeTargetKind.ARM_MODULE, 0, i),
                            Component.translatable("screen.cyberneticenhancements.ripper_station.arm_modules"),
                            "screen.cyberneticenhancements.ripper_station.module_slot_hint"
                    ), mouseX, mouseY);
                }
                return;
            }

            if (isHovering(RipperStationLayout.LEG_MODULE_X[i], RipperStationLayout.LEG_MODULE_Y, 16, 16, mouseX, mouseY)
                    && menu.getLegModuleStack(i).isEmpty()) {
                if (!menu.isLegModuleSlotUnlocked(i)) {
                    guiGraphics.renderTooltip(font, List.of(
                            Component.translatable("screen.cyberneticenhancements.ripper_station.leg_modules"),
                            Component.translatable("screen.cyberneticenhancements.ripper_station.module_slot_locked")
                    ), Optional.empty(), mouseX, mouseY);
                } else {
                    renderSlotTooltip(guiGraphics, buildSubSlotTooltip(
                            new UpgradeTarget(UpgradeTargetKind.LEG_MODULE, 0, i),
                            Component.translatable("screen.cyberneticenhancements.ripper_station.leg_modules"),
                            "screen.cyberneticenhancements.ripper_station.module_slot_hint"
                    ), mouseX, mouseY);
                }
                return;
            }
        }
    }

    private Component getStageLabel() {
        return Component.translatable("screen.cyberneticenhancements.ripper_station.stage." + switch (menu.getStageIndex()) {
            case 0 -> "organic";
            case 1 -> "augmented";
            case 2 -> "cybernetic";
            case 3 -> "synthetic";
            default -> "android";
        });
    }

    private void drawPlayerSlotBacks(GuiGraphics guiGraphics) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawVanillaSlotBack(guiGraphics,
                    RipperStationLayout.PLAYER_INVENTORY_X + column * RipperStationLayout.PLAYER_SLOT_SPACING,
                    RipperStationLayout.PLAYER_INVENTORY_Y + row * RipperStationLayout.PLAYER_SLOT_SPACING,
                        SLOT_BACKGROUND);
            }
        }
        for (int slot = 0; slot < 9; slot++) {
            drawVanillaSlotBack(guiGraphics,
                    RipperStationLayout.PLAYER_INVENTORY_X + slot * RipperStationLayout.PLAYER_SLOT_SPACING,
                    RipperStationLayout.PLAYER_HOTBAR_Y,
                    SLOT_BACKGROUND);
        }
    }

    private void drawStationSlotBack(GuiGraphics guiGraphics, int x, int y, boolean unlocked, boolean installed, CyberwareTier tier) {
        int fill = !unlocked ? 0xFF171D24 : installed ? SLOT_ACTIVE : SLOT_BACKGROUND;
        int outline = !unlocked ? 0xFF28323C : getTierFrameColor(tier);
        StationSlotRenderer.drawUpgradeableSlot(guiGraphics, leftPos, topPos, x, y, fill, outline);
    }

    private void drawVanillaSlotBack(GuiGraphics guiGraphics, int x, int y, int fill) {
        StationSlotRenderer.drawStandardSlot(guiGraphics, leftPos, topPos, x, y, fill);
    }

    private void renderSlotTooltip(GuiGraphics guiGraphics, SlotTooltip tooltip, int mouseX, int mouseY) {
        if (tooltip.visualComponent().isPresent() && tooltip.visualComponent().get() instanceof UpgradeProgressTooltip progressTooltip) {
            renderProgressSlotTooltip(guiGraphics, tooltip.lines(), progressTooltip.progress(), mouseX, mouseY);
            return;
        }
        guiGraphics.renderTooltip(font, tooltip.lines(), Optional.empty(), mouseX, mouseY);
    }

    private void renderProgressSlotTooltip(GuiGraphics guiGraphics, List<Component> lines, float progress, int mouseX, int mouseY) {
        int textWidth = 0;
        for (Component line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }

        int barWidth = UpgradeProgressClientTooltip.getBarWidth();
        int barHeight = UpgradeProgressClientTooltip.getBarHeight();
        int contentWidth = Math.max(textWidth, barWidth);
        int lineAdvance = font.lineHeight + ModTooltipStyle.LINE_SPACING;
        int textHeight = lines.size() * lineAdvance - ModTooltipStyle.LINE_SPACING;
        int tooltipWidth = contentWidth + ModTooltipStyle.CONTENT_PADDING * 2;
        int tooltipHeight = ModTooltipStyle.CONTENT_PADDING + textHeight + ModTooltipStyle.BAR_GAP + barHeight + ModTooltipStyle.BAR_BOTTOM_PADDING;

        int tooltipX = mouseX + ModTooltipStyle.TOOLTIP_CURSOR_OFFSET_X;
        if (tooltipX + tooltipWidth > width - ModTooltipStyle.SCREEN_EDGE_MARGIN) {
            tooltipX = mouseX - ModTooltipStyle.TOOLTIP_CURSOR_OFFSET_X - tooltipWidth;
        }
        tooltipX = Math.max(ModTooltipStyle.SCREEN_EDGE_MARGIN, tooltipX);

        int tooltipY = mouseY - ModTooltipStyle.TOOLTIP_CURSOR_OFFSET_Y;
        if (tooltipY + tooltipHeight > height - ModTooltipStyle.SCREEN_EDGE_MARGIN) {
            tooltipY = height - ModTooltipStyle.SCREEN_EDGE_MARGIN - tooltipHeight;
        }
        tooltipY = Math.max(ModTooltipStyle.SCREEN_EDGE_MARGIN, tooltipY);

        int tooltipX2 = tooltipX + tooltipWidth;
        int tooltipY2 = tooltipY + tooltipHeight;
        guiGraphics.flush();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
        RenderSystem.disableDepthTest();

        ModTooltipStyle.drawTooltipFrame(guiGraphics, tooltipX, tooltipY, tooltipX2, tooltipY2);

        int textX = tooltipX + ModTooltipStyle.CONTENT_PADDING;
        int textY = tooltipY + ModTooltipStyle.CONTENT_PADDING;
        for (Component line : lines) {
            guiGraphics.drawString(font, line.getVisualOrderText(), textX, textY, ModTooltipStyle.TEXT_FALLBACK, false);
            textY += lineAdvance;
        }

        int barX = tooltipX + ModTooltipStyle.CONTENT_PADDING;
        int barY = tooltipY + ModTooltipStyle.CONTENT_PADDING + textHeight + ModTooltipStyle.BAR_GAP;
        UpgradeProgressClientTooltip.renderBar(guiGraphics, barX, barY, progress);

        guiGraphics.flush();
        RenderSystem.enableDepthTest();
        guiGraphics.pose().popPose();
    }

    private SlotTooltip buildCyberwareSlotTooltip(CyberwareSlot slot) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable(slot.displayKey()));
        return appendUpgradeLines(tooltip, new UpgradeTarget(UpgradeTargetKind.CYBERWARE, slot.ordinal(), 0), "screen.cyberneticenhancements.ripper_station.slot_hint");
    }

    private SlotTooltip buildSubSlotTooltip(UpgradeTarget target, Component title, String defaultHintKey) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(title);
        return appendUpgradeLines(tooltip, target, defaultHintKey);
    }

    private SlotTooltip appendUpgradeLines(List<Component> tooltip, UpgradeTarget target, String defaultHintKey) {
        CyberwareTier supportedTier = getSupportedTier(target);
        tooltip.add(Component.translatable(
                "screen.cyberneticenhancements.ripper_station.slot_supported_tier",
                Component.translatable(supportedTier.translationKey()).withStyle(supportedTier.getColor())
        ));

        if (supportedTier == CyberwareTier.TIER_5) {
            tooltip.add(Component.translatable("screen.cyberneticenhancements.ripper_station.slot_upgrade_maxed"));
            return new SlotTooltip(tooltip, Optional.empty());
        }

        CyberwareTier nextTier = getNextSupportedTier(target);
        ItemStack requiredComponent = getRequiredUpgradeComponentStack(target);
        if (!requiredComponent.isEmpty()) {
            tooltip.add(Component.translatable(
                    "screen.cyberneticenhancements.ripper_station.slot_upgrade_cost",
                    Component.literal("1x ").append(requiredComponent.getHoverName())
            ));
        }

        if (!canUpgrade(target)) {
            tooltip.add(Component.translatable(defaultHintKey));
        } else if (hasRequiredUpgradeComponent(target)) {
            tooltip.add(Component.translatable(
                    "screen.cyberneticenhancements.ripper_station.slot_upgrade_hold",
                    Component.translatable(nextTier.translationKey()).withStyle(nextTier.getColor())
            ));
        } else {
            tooltip.add(Component.translatable(
                    "screen.cyberneticenhancements.ripper_station.slot_upgrade_missing",
                    requiredComponent.isEmpty() ? Component.empty() : requiredComponent.getHoverName()
            ));
        }

        return new SlotTooltip(tooltip, Optional.of(new UpgradeProgressTooltip(getHeldUpgradeProgress(target))));
    }

    private float getHeldUpgradeProgress(UpgradeTarget target) {
        if (!target.equals(heldUpgradeTarget) || heldUpgradeStartTier == null) {
            return 0.0F;
        }
        if (upgradeTriggeredThisHold) {
            return 1.0F;
        }
        long elapsedMs = System.currentTimeMillis() - heldUpgradeStartMs;
        return Math.max(0.0F, Math.min(1.0F, (float) elapsedMs / (float) SLOT_UPGRADE_HOLD_MS));
    }

    private void updateHeldSlotUpgrade(int mouseX, int mouseY) {
        if (heldUpgradeTarget == null || heldUpgradeStartTier == null) {
            return;
        }
        if (!isHeldUpgradeStillValid(heldUpgradeTarget, mouseX, mouseY)) {
            clearHeldSlotUpgrade();
            return;
        }
        if (getSupportedTier(heldUpgradeTarget) != heldUpgradeStartTier) {
            clearHeldSlotUpgrade();
            return;
        }
        if (!upgradeTriggeredThisHold && System.currentTimeMillis() - heldUpgradeStartMs >= SLOT_UPGRADE_HOLD_MS) {
            sendUpgradePacket(heldUpgradeTarget);
            upgradeTriggeredThisHold = true;
        }
    }

    private boolean isHeldUpgradeStillValid(UpgradeTarget target, int mouseX, int mouseY) {
        return isHoveringUpgradeTarget(target, mouseX, mouseY)
                && isTargetEmpty(target)
                && canUpgrade(target)
                && hasRequiredUpgradeComponent(target);
    }

    private UpgradeTarget getHoveredEmptyUpgradeableSlot(int mouseX, int mouseY) {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            Slot menuSlot = menu.slots.get(slot.ordinal());
            if (isHovering(menuSlot.x, menuSlot.y, 16, 16, mouseX, mouseY) && !menuSlot.hasItem()) {
                return new UpgradeTarget(UpgradeTargetKind.CYBERWARE, slot.ordinal(), 0);
            }
        }
        for (int cluster = 0; cluster < RipperStationLayout.CHIP_CLUSTER_X.length; cluster++) {
            for (int slot = 0; slot < RipperStationLayout.CHIP_SLOT_OFFSET_X.length; slot++) {
                int chipX = RipperStationLayout.CHIP_CLUSTER_X[cluster] + RipperStationLayout.CHIP_SLOT_OFFSET_X[slot];
                if (isHovering(chipX, RipperStationLayout.CHIP_SLOT_Y, 16, 16, mouseX, mouseY) && menu.getChipwareStack(cluster, slot).isEmpty()) {
                    return new UpgradeTarget(UpgradeTargetKind.CHIPWARE, cluster, slot);
                }
            }
        }
        for (int slot = 0; slot < RipperStationLayout.ARM_MODULE_X.length; slot++) {
            if (isHovering(RipperStationLayout.ARM_MODULE_X[slot], RipperStationLayout.ARM_MODULE_Y, 16, 16, mouseX, mouseY) && menu.getArmModuleStack(slot).isEmpty()) {
                return new UpgradeTarget(UpgradeTargetKind.ARM_MODULE, 0, slot);
            }
            if (isHovering(RipperStationLayout.LEG_MODULE_X[slot], RipperStationLayout.LEG_MODULE_Y, 16, 16, mouseX, mouseY) && menu.getLegModuleStack(slot).isEmpty()) {
                return new UpgradeTarget(UpgradeTargetKind.LEG_MODULE, 0, slot);
            }
        }
        return null;
    }

    private void clearHeldSlotUpgrade() {
        heldUpgradeTarget = null;
        heldUpgradeStartTier = null;
        heldUpgradeStartMs = 0L;
        upgradeTriggeredThisHold = false;
    }

    private void sendUpgradePacket(UpgradeTarget target) {
        switch (target.kind()) {
            case CYBERWARE -> PacketDistributor.sendToServer(new UpgradeCyberwareSlotPayload(target.primaryIndex()));
            case CHIPWARE -> PacketDistributor.sendToServer(new UpgradeRipperSubSlotPayload(UpgradeRipperSubSlotPayload.KIND_CHIPWARE, target.primaryIndex(), target.slotIndex()));
            case ARM_MODULE -> PacketDistributor.sendToServer(new UpgradeRipperSubSlotPayload(UpgradeRipperSubSlotPayload.KIND_ARM_MODULE, 0, target.slotIndex()));
            case LEG_MODULE -> PacketDistributor.sendToServer(new UpgradeRipperSubSlotPayload(UpgradeRipperSubSlotPayload.KIND_LEG_MODULE, 0, target.slotIndex()));
        }
    }

    private boolean isHoveringUpgradeTarget(UpgradeTarget target, int mouseX, int mouseY) {
        return switch (target.kind()) {
            case CYBERWARE -> {
                Slot menuSlot = menu.slots.get(target.primaryIndex());
                yield isHovering(menuSlot.x, menuSlot.y, 16, 16, mouseX, mouseY);
            }
            case CHIPWARE -> {
                int x = RipperStationLayout.CHIP_CLUSTER_X[target.primaryIndex()] + RipperStationLayout.CHIP_SLOT_OFFSET_X[target.slotIndex()];
                yield isHovering(x, RipperStationLayout.CHIP_SLOT_Y, 16, 16, mouseX, mouseY);
            }
            case ARM_MODULE -> isHovering(RipperStationLayout.ARM_MODULE_X[target.slotIndex()], RipperStationLayout.ARM_MODULE_Y, 16, 16, mouseX, mouseY);
            case LEG_MODULE -> isHovering(RipperStationLayout.LEG_MODULE_X[target.slotIndex()], RipperStationLayout.LEG_MODULE_Y, 16, 16, mouseX, mouseY);
        };
    }

    private boolean isTargetEmpty(UpgradeTarget target) {
        return switch (target.kind()) {
            case CYBERWARE -> menu.getCyberwareStack(CyberwareSlot.values()[target.primaryIndex()]).isEmpty();
            case CHIPWARE -> menu.getChipwareStack(target.primaryIndex(), target.slotIndex()).isEmpty();
            case ARM_MODULE -> menu.getArmModuleStack(target.slotIndex()).isEmpty();
            case LEG_MODULE -> menu.getLegModuleStack(target.slotIndex()).isEmpty();
        };
    }

    private CyberwareTier getSupportedTier(UpgradeTarget target) {
        return switch (target.kind()) {
            case CYBERWARE -> menu.getSupportedTier(CyberwareSlot.values()[target.primaryIndex()]);
            case CHIPWARE -> menu.getChipSupportedTier(target.primaryIndex(), target.slotIndex());
            case ARM_MODULE -> menu.getArmModuleSupportedTier(target.slotIndex());
            case LEG_MODULE -> menu.getLegModuleSupportedTier(target.slotIndex());
        };
    }

    private CyberwareTier getNextSupportedTier(UpgradeTarget target) {
        return switch (target.kind()) {
            case CYBERWARE -> menu.getNextSupportedTier(CyberwareSlot.values()[target.primaryIndex()]);
            case CHIPWARE -> menu.getNextChipSupportedTier(target.primaryIndex(), target.slotIndex());
            case ARM_MODULE -> menu.getNextArmModuleSupportedTier(target.slotIndex());
            case LEG_MODULE -> menu.getNextLegModuleSupportedTier(target.slotIndex());
        };
    }

    private boolean canUpgrade(UpgradeTarget target) {
        return switch (target.kind()) {
            case CYBERWARE -> menu.canUpgradeSupportedTier(CyberwareSlot.values()[target.primaryIndex()]);
            case CHIPWARE -> menu.canUpgradeChipSupportedTier(target.primaryIndex(), target.slotIndex());
            case ARM_MODULE -> menu.canUpgradeArmModuleSupportedTier(target.slotIndex());
            case LEG_MODULE -> menu.canUpgradeLegModuleSupportedTier(target.slotIndex());
        };
    }

    private boolean hasRequiredUpgradeComponent(UpgradeTarget target) {
        return switch (target.kind()) {
            case CYBERWARE -> menu.hasRequiredUpgradeComponent(CyberwareSlot.values()[target.primaryIndex()]);
            case CHIPWARE -> menu.hasRequiredChipUpgradeComponent(target.primaryIndex(), target.slotIndex());
            case ARM_MODULE -> menu.hasRequiredArmModuleUpgradeComponent(target.slotIndex());
            case LEG_MODULE -> menu.hasRequiredLegModuleUpgradeComponent(target.slotIndex());
        };
    }

    private ItemStack getRequiredUpgradeComponentStack(UpgradeTarget target) {
        return switch (target.kind()) {
            case CYBERWARE -> menu.getRequiredUpgradeComponentStack(CyberwareSlot.values()[target.primaryIndex()]);
            case CHIPWARE -> menu.getRequiredChipUpgradeComponentStack(target.primaryIndex(), target.slotIndex());
            case ARM_MODULE -> menu.getRequiredArmModuleUpgradeComponentStack(target.slotIndex());
            case LEG_MODULE -> menu.getRequiredLegModuleUpgradeComponentStack(target.slotIndex());
        };
    }

    private int getTierFrameColor(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1 -> 0xFF4A5662;
            case TIER_2 -> 0xFF3FAE6A;
            case TIER_3 -> 0xFF35A8E0;
            case TIER_4 -> 0xFFAA63E8;
            case TIER_5 -> 0xFFE2A93A;
        };
    }
}
