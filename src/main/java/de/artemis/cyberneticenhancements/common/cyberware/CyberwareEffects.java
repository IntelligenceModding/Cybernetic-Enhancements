package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableManager;
import de.artemis.cyberneticenhancements.common.item.ChipwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareModuleItem;
import de.artemis.cyberneticenhancements.common.network.CyberwareHudPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.EnumMap;

public final class CyberwareEffects {
    private static final int EFFECT_DURATION = 240;
    private static final String LAST_ABSORPTION_BUFFER_KEY = "cyberneticenhancements.last_absorption_buffer";
    private static final String LAST_ON_GROUND_KEY = "cyberneticenhancements.last_on_ground";
    private static final double MAX_DAMAGE_REDUCTION = 0.80D;

    private CyberwareEffects() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        CyberwareConditionManager.onPlayerTick(player);
        CyberstrainManager.onPlayerTick(player);
        handleJumpAugmentation(player);
        if (player instanceof ServerPlayer serverPlayer && player.tickCount % 4 == 0) {
            PacketDistributor.sendToPlayer(serverPlayer, CyberwareHudPayload.capture(serverPlayer));
        }
        if (player.tickCount % 20 != 0) {
            return;
        }

        CyberConsumableManager.onPlayerTick(player);
        EnumMap<CyberwareEffectType, Double> totals = refreshPlayerCyberware(player);
        applyPassiveHealing(player, totals);
    }

    public static EnumMap<CyberwareEffectType, Double> refreshPlayerCyberware(Player player) {
        if (player.level().isClientSide()) {
            return new EnumMap<>(CyberwareEffectType.class);
        }

        PlayerCyberwareInventory inventory = new PlayerCyberwareInventory(player);
        EnumMap<CyberwareEffectType, Double> totals = collectEffectTotals(player, inventory, true);

        for (CyberwareEffectType type : CyberwareEffectType.values()) {
            if (type.isAttributeEffect()) {
                applyAttributeModifier(player, type, totals.getOrDefault(type, 0.0D));
            } else if (type.isMobEffect()) {
                applyMobEffect(player, type, totals.containsKey(type) ? (int) Math.round(totals.get(type)) : -1);
            }
        }
        applyAbsorptionBuffer(player, totals.getOrDefault(CyberwareEffectType.BONUS_ABSORPTION, 0.0D));

        double maxHealth = player.getMaxHealth();
        if (player.getHealth() > maxHealth) {
            player.setHealth((float) maxHealth);
        }
        return totals;
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        EnumMap<CyberwareEffectType, Double> totals = collectEffectTotals(player);
        double reduction = Mth.clamp(totals.getOrDefault(CyberwareEffectType.DAMAGE_REDUCTION, 0.0D), 0.0D, MAX_DAMAGE_REDUCTION);
        if (reduction > 0.0001D) {
            event.setAmount((float) (event.getAmount() * (1.0D - reduction)));
        }
    }

    public static EnumMap<CyberwareEffectType, Double> collectEffectTotals(Player player) {
        return collectEffectTotals(player, new PlayerCyberwareInventory(player), false);
    }

    public static EnumMap<CyberwareEffectType, Double> collectEffectTotals(Player player, PlayerCyberwareInventory inventory, boolean applyTriggers) {
        EnumMap<CyberwareEffectType, Double> totals = new EnumMap<>(CyberwareEffectType.class);

        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!(stack.getItem() instanceof CyberwareItem cyberwareItem)) {
                continue;
            }

            double integrityScale = CyberwareConditionHelper.getIntegrityRatio(stack, cyberwareItem.getDefinition());
            mergeEffects(totals, cyberwareItem.getEffects(stack), integrityScale);
        }
        for (CyberwareModuleItem moduleItem : inventory.getInstalledModuleItems()) {
            mergeEffects(totals, moduleItem.getDefinition().effects());
        }
        for (ChipwareItem chipwareItem : inventory.getInstalledChipwareItems()) {
            mergeEffects(totals, chipwareItem.getDefinition().effects());
        }

        if (applyTriggers) {
            CyberwareAbilities.applyTriggeredAbilities(player, inventory);
        }
        TemporaryCyberwareEffectManager.mergeActiveEffects(player, totals, player.level().getGameTime());
        CyberwareAbilities.mergeActiveEffects(player, inventory, totals);
        CyberstrainManager.applyPenalties(player, inventory, totals);
        return totals;
    }

    private static void applyAttributeModifier(Player player, CyberwareEffectType type, double amount) {
        AttributeInstance attributeInstance = player.getAttribute(type.getAttribute());
        if (attributeInstance == null) {
            return;
        }
        attributeInstance.removeModifier(type.modifierId());
        if (Math.abs(amount) > 0.0001D) {
            attributeInstance.addOrUpdateTransientModifier(new AttributeModifier(type.modifierId(), amount, type.getOperation()));
        }
    }

    private static void applyMobEffect(Player player, CyberwareEffectType type, int amplifier) {
        if (amplifier < 0) {
            return;
        }
        player.addEffect(new MobEffectInstance(type.getMobEffect(), EFFECT_DURATION, amplifier, true, false, false));
    }

    private static void mergeEffects(EnumMap<CyberwareEffectType, Double> totals, Iterable<CyberwareEffect> effects) {
        mergeEffects(totals, effects, 1.0D);
    }

    private static void mergeEffects(EnumMap<CyberwareEffectType, Double> totals, Iterable<CyberwareEffect> effects, double scale) {
        if (scale <= 0.0001D) {
            return;
        }

        for (CyberwareEffect effect : effects) {
            if (effect.type().isMobEffect()) {
                if (scale >= 0.35D) {
                    totals.merge(effect.type(), effect.amount(), Math::max);
                }
                continue;
            }

            totals.merge(effect.type(), effect.amount() * scale, Double::sum);
        }
    }

    private static void applyPassiveHealing(Player player, EnumMap<CyberwareEffectType, Double> totals) {
        double regenerationPerSecond = totals.getOrDefault(CyberwareEffectType.HEALTH_REGEN, 0.0D);
        if (regenerationPerSecond > 0.0001D && player.getHealth() < player.getMaxHealth()) {
            player.heal((float) regenerationPerSecond);
        }
    }

    private static void applyAbsorptionBuffer(Player player, double desiredAbsorption) {
        double target = Math.max(0.0D, desiredAbsorption);
        double previousTarget = player.getPersistentData().getDouble(LAST_ABSORPTION_BUFFER_KEY);
        double delta = target - previousTarget;
        if (Math.abs(delta) > 0.0001D) {
            player.setAbsorptionAmount((float) Math.max(0.0D, player.getAbsorptionAmount() + delta));
        }

        if (target > 0.0001D) {
            player.getPersistentData().putDouble(LAST_ABSORPTION_BUFFER_KEY, target);
        } else {
            player.getPersistentData().remove(LAST_ABSORPTION_BUFFER_KEY);
        }
    }

    private static void handleJumpAugmentation(Player player) {
        boolean wasOnGround = player.getPersistentData().getBoolean(LAST_ON_GROUND_KEY);
        boolean onGround = player.onGround();
        if (!onGround && wasOnGround && player.getDeltaMovement().y > 0.0D) {
            double jumpPower = collectEffectTotals(player).getOrDefault(CyberwareEffectType.JUMP_POWER, 0.0D);
            if (jumpPower > 0.0001D) {
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x, motion.y + 0.42D * jumpPower, motion.z);
                player.hasImpulse = true;
            }
        }
        player.getPersistentData().putBoolean(LAST_ON_GROUND_KEY, onGround);
    }
}
