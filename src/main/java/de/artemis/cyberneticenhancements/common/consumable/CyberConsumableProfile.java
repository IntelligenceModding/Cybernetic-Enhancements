package de.artemis.cyberneticenhancements.common.consumable;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffect;
import de.artemis.cyberneticenhancements.common.cyberware.CyberstrainManager;
import de.artemis.cyberneticenhancements.common.cyberware.TemporaryCyberwareEffectManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public enum CyberConsumableProfile {
    MAXDOC_MK1,
    MAXDOC_MK2,
    MAXDOC_MK3,
    BOUNCE_BACK_MK1,
    BOUNCE_BACK_MK2,
    BOUNCE_BACK_MK3,
    HEALTH_BOOSTER,
    STAMINA_BOOSTER,
    OXY_BOOSTER,
    CAPACITY_BOOSTER,
    RAM_JOLT,
    IMMUNOBLOCKERS,
    CHROME_SUPPRESSANT,
    BLACK_LACE,
    ASSKICK,
    JELLYTRICITY;

    public void apply(Player player) {
        switch (this) {
            case MAXDOC_MK1 -> player.heal(6.0F);
            case MAXDOC_MK2 -> player.heal(10.0F);
            case MAXDOC_MK3 -> player.heal(14.0F);
            case BOUNCE_BACK_MK1 -> {
                player.heal(4.0F);
                TemporaryCyberwareEffectManager.addEffect(player, CyberwareEffect.healthRegen(0.8D), 200);
            }
            case BOUNCE_BACK_MK2 -> {
                player.heal(6.0F);
                TemporaryCyberwareEffectManager.addEffect(player, CyberwareEffect.healthRegen(1.2D), 220);
            }
            case BOUNCE_BACK_MK3 -> {
                player.heal(8.0F);
                TemporaryCyberwareEffectManager.addEffect(player, CyberwareEffect.healthRegen(1.6D), 240);
            }
            case HEALTH_BOOSTER -> TemporaryCyberwareEffectManager.addEffect(player, CyberwareEffect.hearts(8.0D), 7_200);
            case STAMINA_BOOSTER -> TemporaryCyberwareEffectManager.addEffects(player, 7_200,
                    CyberwareEffect.moveSpeed(0.20D),
                    CyberwareEffect.breakSpeed(0.20D));
            case OXY_BOOSTER -> player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 7_200, 0, true, false, false));
            case CAPACITY_BOOSTER -> TemporaryCyberwareEffectManager.addEffect(player, CyberwareEffect.chromeCapacity(12.0D), 18_000);
            case RAM_JOLT -> CyberstrainManager.applyRamJolt(player, 7_200, 0.80D);
            case IMMUNOBLOCKERS -> CyberstrainManager.applySuppressionDose(player, 18, 24_000, true);
            case CHROME_SUPPRESSANT -> CyberstrainManager.applySuppressionDose(player, 8, 12_000, false);
            case BLACK_LACE -> {
                reduceHealthFraction(player, 0.35F);
                CyberstrainManager.addInstability(player, 16.0D);
                TemporaryCyberwareEffectManager.addEffects(player, 6_000,
                        CyberwareEffect.moveSpeed(0.40D),
                        CyberwareEffect.damage(4.0D),
                        CyberwareEffect.damageReduction(0.10D),
                        CyberwareEffect.breakSpeed(0.20D));
            }
            case ASSKICK -> {
                CyberstrainManager.addInstability(player, 7.0D);
                TemporaryCyberwareEffectManager.addEffect(player, CyberwareEffect.hearts(8.0D), 7_200);
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 7_200, 0, true, false, false));
            }
            case JELLYTRICITY -> {
                reduceHealthFraction(player, 0.15F);
                CyberstrainManager.addInstability(player, 10.0D);
                TemporaryCyberwareEffectManager.addEffects(player, 7_200,
                        CyberwareEffect.moveSpeed(0.40D),
                        CyberwareEffect.breakSpeed(0.40D));
            }
        }
    }

    private static void reduceHealthFraction(Player player, float fraction) {
        float reduction = player.getMaxHealth() * fraction;
        player.setHealth(Math.max(1.0F, player.getHealth() - reduction));
    }
}
