package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public enum CyberwareEffectType {
    MAX_HEALTH("max_health", Attributes.MAX_HEALTH, AttributeModifier.Operation.ADD_VALUE),
    ARMOR("armor", Attributes.ARMOR, AttributeModifier.Operation.ADD_VALUE),
    ATTACK_DAMAGE("attack_damage", Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_VALUE),
    ATTACK_SPEED("attack_speed", Attributes.ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE),
    MOVEMENT_SPEED("movement_speed", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
    BLOCK_BREAK_SPEED("block_break_speed", Attributes.BLOCK_BREAK_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
    BLOCK_REACH("block_reach", Attributes.BLOCK_INTERACTION_RANGE, AttributeModifier.Operation.ADD_VALUE),
    ENTITY_REACH("entity_reach", Attributes.ENTITY_INTERACTION_RANGE, AttributeModifier.Operation.ADD_VALUE),
    STEP_HEIGHT("step_height", Attributes.STEP_HEIGHT, AttributeModifier.Operation.ADD_VALUE),
    SAFE_FALL_DISTANCE("safe_fall_distance", Attributes.SAFE_FALL_DISTANCE, AttributeModifier.Operation.ADD_VALUE),
    FALL_DAMAGE_REDUCTION("fall_damage_reduction", Attributes.FALL_DAMAGE_MULTIPLIER, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
    KNOCKBACK_RESISTANCE("knockback_resistance", Attributes.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_VALUE),
    CHROME_CAPACITY("chrome_capacity"),
    HEALTH_REGEN("health_regen"),
    DAMAGE_REDUCTION("damage_reduction"),
    BONUS_ABSORPTION("bonus_absorption"),
    JUMP_POWER("jump_power"),
    NIGHT_VISION("night_vision", MobEffects.NIGHT_VISION),
    FIRE_RESISTANCE("fire_resistance", MobEffects.FIRE_RESISTANCE),
    WATER_BREATHING("water_breathing", MobEffects.WATER_BREATHING),
    DOLPHINS_GRACE("dolphins_grace", MobEffects.DOLPHINS_GRACE);

    private final String id;
    private final Holder<Attribute> attribute;
    private final AttributeModifier.Operation operation;
    private final Holder<MobEffect> mobEffect;

    CyberwareEffectType(String id, Holder<Attribute> attribute, AttributeModifier.Operation operation) {
        this.id = id;
        this.attribute = attribute;
        this.operation = operation;
        this.mobEffect = null;
    }

    CyberwareEffectType(String id) {
        this.id = id;
        this.attribute = null;
        this.operation = null;
        this.mobEffect = null;
    }

    CyberwareEffectType(String id, Holder<MobEffect> mobEffect) {
        this.id = id;
        this.attribute = null;
        this.operation = null;
        this.mobEffect = mobEffect;
    }

    public String translationKey() {
        return "tooltip.cyberneticenhancements.effect." + id;
    }

    public String id() {
        return id;
    }

    public ResourceLocation modifierId() {
        return ResourceLocation.fromNamespaceAndPath("cyberneticenhancements", "cyberware/" + id);
    }

    public Holder<Attribute> getAttribute() {
        return attribute;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public Holder<MobEffect> getMobEffect() {
        return mobEffect;
    }

    public boolean isAttributeEffect() {
        return attribute != null;
    }

    public boolean isMobEffect() {
        return mobEffect != null;
    }
}
