package de.artemis.cyberneticenhancements.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.artemis.cyberneticenhancements.common.blockentity.RelicCacheBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class RelicCacheBlockEntityRenderer implements BlockEntityRenderer<RelicCacheBlockEntity> {
    private static final float FULL_TURN_DEGREES = 360.0F;

    public RelicCacheBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RelicCacheBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.hasActiveHackSession()) {
            renderHackCountdown(blockEntity, partialTick, poseStack, bufferSource, packedLight);
        }
        if (!blockEntity.isAnimating()) {
            return;
        }

        ItemStack preview = blockEntity.currentPreviewItem();
        if (preview.isEmpty()) {
            return;
        }

        float time = (blockEntity.getLevel() == null ? 0.0F : blockEntity.getLevel().getGameTime()) + partialTick;
        float progress = blockEntity.animationProgress(partialTick);
        float rise = easeOutCubic(Mth.clamp(progress / 0.22F, 0.0F, 1.0F));
        float lockIn = easeInCubic(Mth.clamp((progress - 0.7F) / 0.3F, 0.0F, 1.0F));
        float pulse = 1.0F + 0.03F * Mth.sin(time * 0.22F) + 0.05F * lockIn;
        float hover = Mth.sin(time * 0.09F) * 0.018F + Mth.cos(time * 0.05F) * 0.012F;
        float coreY = 0.98F + rise * 0.30F + hover;
        float coreYaw = (time * 3.0F + progress * 90.0F) % FULL_TURN_DEGREES;
        float corePitch = 8.0F + Mth.sin(time * 0.07F) * 4.0F + lockIn * 6.0F;
        float coreScale = 0.62F + rise * 0.08F;

        renderItem(
                blockEntity,
                preview,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                0.5D,
                coreY,
                0.5D,
                coreYaw,
                corePitch,
                coreScale * pulse
        );

        int orbitCount = Math.min(3, Math.max(0, blockEntity.previewItemCount() - 1));
        for (int orbitIndex = 0; orbitIndex < orbitCount; orbitIndex++) {
            ItemStack orbitStack = blockEntity.previewItem(orbitIndex + 1);
            if (orbitStack.isEmpty()) {
                continue;
            }

            float orbitDirection = orbitIndex % 2 == 0 ? 1.0F : -1.0F;
            float orbitAngle = time * 0.045F * orbitDirection
                    + progress * 2.2F * orbitDirection
                    + orbitIndex * ((float) Math.PI * 2.0F / 3.0F);
            float orbitRadius = 0.34F - lockIn * 0.10F;
            float orbitX = Mth.cos(orbitAngle) * orbitRadius;
            float orbitZ = Mth.sin(orbitAngle) * orbitRadius;
            float orbitY = 0.92F + rise * 0.24F + Mth.sin(time * 0.06F + orbitIndex * 0.8F) * 0.025F;
            float orbitYaw = (-orbitAngle * Mth.RAD_TO_DEG) + time * 1.4F * orbitDirection;
            float orbitScale = 0.29F + 0.02F * Mth.sin(time * 0.08F + orbitIndex);

            renderItem(
                    blockEntity,
                    orbitStack,
                    poseStack,
                    bufferSource,
                    packedLight,
                    packedOverlay,
                    0.5D + orbitX,
                    orbitY,
                    0.5D + orbitZ,
                    orbitYaw,
                    6.0F,
                    orbitScale
            );
        }

        ItemStack lockedReward = blockEntity.rewardItem(0);
        if (!lockedReward.isEmpty() && lockIn > 0.0F) {
            float rewardYaw = (-time * 4.0F) % FULL_TURN_DEGREES;
            float rewardScale = (0.22F + lockIn * 0.16F) * (1.0F + 0.04F * Mth.sin(time * 0.22F));
            float rewardY = 0.72F + rise * 0.20F - lockIn * 0.03F;
            renderItem(
                    blockEntity,
                    lockedReward,
                    poseStack,
                    bufferSource,
                    packedLight,
                    packedOverlay,
                    0.5D,
                    rewardY,
                    0.5D,
                    rewardYaw,
                    4.0F,
                    rewardScale
            );
        }
    }

    private static void renderHackCountdown(
            RelicCacheBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int secondsLeft = Math.max(0, Mth.ceil((blockEntity.getHackTimeRemainingTicks() - partialTick) / 20.0F));
        String timerLabel = secondsLeft + "s";
        int textWidth = font.width(timerLabel);
        int textColor = blockEntity.getHackTimeRemainingTicks() <= 10 * 20 ? 0xFFFF9A76 : 0xFF9FF7E2;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.05D, 0.5D);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(0.0225F, -0.0225F, 0.0225F);
        poseStack.translate(0.0F, -4.0F, 0.0F);

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, -0.01F);
        var matrix = poseStack.last().pose();
        font.drawInBatch(timerLabel, -textWidth / 2.0F, 0.0F, 0xCC081015, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();

        font.drawInBatch(timerLabel, -textWidth / 2.0F, 0.0F, textColor, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }

    private static void renderItem(
            RelicCacheBlockEntity blockEntity,
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            double x,
            double y,
            double z,
            float yaw,
            float pitch,
            float scale
    ) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.scale(scale, scale, scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                0
        );
        poseStack.popPose();
    }

    private static float easeOutCubic(float value) {
        float inverse = 1.0F - value;
        return 1.0F - inverse * inverse * inverse;
    }

    private static float easeInCubic(float value) {
        return value * value * value;
    }
}
