package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableCategory;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableManager;
import de.artemis.cyberneticenhancements.common.cyberware.CyberstrainManager;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareAbilities;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffectType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlotType;
import de.artemis.cyberneticenhancements.common.cyberware.OperatingSystemFamily;
import de.artemis.cyberneticenhancements.common.cyberware.PlayerCyberwareInventory;
import de.artemis.cyberneticenhancements.common.cyberware.TemporaryCyberwareEffectManager;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record CyberwareHudPayload(
        int installedChrome,
        int chromeCapacity,
        int cyberstrain,
        int effectiveCyberstrain,
        int suppressionAmount,
        int suppressionSeconds,
        int psychosisSeconds,
        String psychosisStateKey,
        String abilityKey,
        int abilityActiveSeconds,
        int abilityActiveTotalSeconds,
        int abilityCooldownSeconds,
        int abilityCooldownTotalSeconds,
        List<StatusEntry> statuses,
        List<CooldownEntry> cooldowns
) implements CustomPacketPayload {
    public static final Type<CyberwareHudPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "cyberware_hud"));
    private static final StreamCodec<RegistryFriendlyByteBuf, CapacityState> CAPACITY_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            CapacityState::installedChrome,
            ByteBufCodecs.INT,
            CapacityState::chromeCapacity,
            CapacityState::new
    );
    private static final StreamCodec<RegistryFriendlyByteBuf, StrainState> STRAIN_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            StrainState::cyberstrain,
            ByteBufCodecs.INT,
            StrainState::effectiveCyberstrain,
            ByteBufCodecs.INT,
            StrainState::suppressionAmount,
            ByteBufCodecs.INT,
            StrainState::suppressionSeconds,
            ByteBufCodecs.INT,
            StrainState::psychosisSeconds,
            ByteBufCodecs.STRING_UTF8,
            StrainState::psychosisStateKey,
            StrainState::new
    );
    private static final StreamCodec<RegistryFriendlyByteBuf, AbilityState> ABILITY_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            AbilityState::abilityKey,
            ByteBufCodecs.INT,
            AbilityState::abilityActiveSeconds,
            ByteBufCodecs.INT,
            AbilityState::abilityCooldownSeconds,
            ByteBufCodecs.INT,
            AbilityState::abilityActiveTotalSeconds,
            ByteBufCodecs.INT,
            AbilityState::abilityCooldownTotalSeconds,
            AbilityState::new
    );
    private static final StreamCodec<RegistryFriendlyByteBuf, StatusEntry> STATUS_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            StatusEntry::translationKey,
            ByteBufCodecs.DOUBLE,
            StatusEntry::amount,
            ByteBufCodecs.INT,
            StatusEntry::remainingSeconds,
            ByteBufCodecs.INT,
            StatusEntry::totalSeconds,
            StatusEntry::new
    );
    private static final StreamCodec<RegistryFriendlyByteBuf, CooldownEntry> COOLDOWN_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            CooldownEntry::translationKey,
            ByteBufCodecs.INT,
            CooldownEntry::remainingSeconds,
            ByteBufCodecs.INT,
            CooldownEntry::totalSeconds,
            CooldownEntry::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, CyberwareHudPayload> STREAM_CODEC = StreamCodec.composite(
            CAPACITY_STREAM_CODEC,
            payload -> new CapacityState(payload.installedChrome(), payload.chromeCapacity()),
            STRAIN_STREAM_CODEC,
            payload -> new StrainState(
                    payload.cyberstrain(),
                    payload.effectiveCyberstrain(),
                    payload.suppressionAmount(),
                    payload.suppressionSeconds(),
                    payload.psychosisSeconds(),
                    payload.psychosisStateKey()
            ),
            ABILITY_STREAM_CODEC,
            payload -> new AbilityState(
                    payload.abilityKey(),
                    payload.abilityActiveSeconds(),
                    payload.abilityCooldownSeconds(),
                    payload.abilityActiveTotalSeconds(),
                    payload.abilityCooldownTotalSeconds()
            ),
            ByteBufCodecs.collection(ArrayList::new, STATUS_STREAM_CODEC),
            CyberwareHudPayload::statuses,
            ByteBufCodecs.collection(ArrayList::new, COOLDOWN_STREAM_CODEC),
            CyberwareHudPayload::cooldowns,
            CyberwareHudPayload::fromCodecParts
    );
    private record CapacityState(int installedChrome, int chromeCapacity) {
    }

    private record StrainState(
            int cyberstrain,
            int effectiveCyberstrain,
            int suppressionAmount,
            int suppressionSeconds,
            int psychosisSeconds,
            String psychosisStateKey
    ) {
    }

    private record AbilityState(
            String abilityKey,
            int abilityActiveSeconds,
            int abilityCooldownSeconds,
            int abilityActiveTotalSeconds,
            int abilityCooldownTotalSeconds
    ) {
    }

    public record StatusEntry(String translationKey, double amount, int remainingSeconds, int totalSeconds) {
    }

    public record CooldownEntry(String translationKey, int remainingSeconds, int totalSeconds) {
    }

    @Override
    public Type<CyberwareHudPayload> type() {
        return TYPE;
    }

    private static CyberwareHudPayload fromCodecParts(
            CapacityState capacity,
            StrainState strain,
            AbilityState ability,
            List<StatusEntry> statuses,
            List<CooldownEntry> cooldowns
    ) {
        return new CyberwareHudPayload(
                capacity.installedChrome(),
                capacity.chromeCapacity(),
                strain.cyberstrain(),
                strain.effectiveCyberstrain(),
                strain.suppressionAmount(),
                strain.suppressionSeconds(),
                strain.psychosisSeconds(),
                strain.psychosisStateKey(),
                ability.abilityKey(),
                ability.abilityActiveSeconds(),
                ability.abilityActiveTotalSeconds(),
                ability.abilityCooldownSeconds(),
                ability.abilityCooldownTotalSeconds(),
                statuses,
                cooldowns
        );
    }

    public static CyberwareHudPayload capture(ServerPlayer player) {
        PlayerCyberwareInventory inventory = new PlayerCyberwareInventory(player);
        long gameTime = player.level().getGameTime();
        int suppressionSeconds = CyberstrainManager.getSuppressionSecondsRemaining(player);
        int abilityCooldownSeconds = CyberwareAbilities.getAbilityCooldownSecondsRemaining(player);
        String abilityKey = resolveAbilityKey(player, inventory);
        int abilityCooldownTotalSeconds = resolveAbilityCooldownTotalSeconds(player, inventory, abilityCooldownSeconds);

        return new CyberwareHudPayload(
                inventory.getInstalledChromeCost(),
                inventory.getChromeCapacity(),
                inventory.getCyberstrain(),
                CyberstrainManager.getEffectiveCyberstrain(player, inventory, gameTime),
                CyberstrainManager.getSuppressionAmount(player),
                suppressionSeconds,
                (int) CyberstrainManager.getPsychosisSecondsRemaining(player),
                psychosisStateKey(player, inventory),
                abilityKey,
                CyberwareAbilities.getActiveSecondsRemaining(player),
                CyberwareAbilities.getActiveTotalSeconds(player),
                abilityCooldownSeconds,
                abilityCooldownTotalSeconds,
                collectStatuses(player, gameTime, suppressionSeconds),
                collectCooldowns(player, inventory)
        );
    }

    private static List<StatusEntry> collectStatuses(ServerPlayer player, long gameTime, int suppressionSeconds) {
        List<StatusEntry> entries = new ArrayList<>();
        if (suppressionSeconds > 0) {
            entries.add(new StatusEntry(
                    "hud.cyberneticenhancements.suppression",
                    CyberstrainManager.getSuppressionAmount(player),
                    suppressionSeconds,
                    CyberstrainManager.getSuppressionTotalSeconds(player)
            ));
        }

        int ramJoltSeconds = CyberstrainManager.getRamJoltSecondsRemaining(player);
        if (ramJoltSeconds > 0) {
            entries.add(new StatusEntry(
                    "hud.cyberneticenhancements.ram_jolt",
                    CyberstrainManager.getRamJoltCooldownMultiplier(player),
                    ramJoltSeconds,
                    CyberstrainManager.getRamJoltTotalSeconds(player)
            ));
        }

        List<TemporaryCyberwareEffectManager.ActiveEffectStatus> activeEffects = new ArrayList<>(TemporaryCyberwareEffectManager.collectActiveStatuses(player, gameTime));
        activeEffects.removeIf(status -> !isHudTrackedEffect(status.type()));
        activeEffects.sort(Comparator
                .comparingInt((TemporaryCyberwareEffectManager.ActiveEffectStatus status) -> effectPriority(status.type()))
                .thenComparing(TemporaryCyberwareEffectManager.ActiveEffectStatus::remainingSeconds, Comparator.reverseOrder()));

        for (TemporaryCyberwareEffectManager.ActiveEffectStatus status : activeEffects) {
            entries.add(new StatusEntry(status.type().translationKey(), status.amount(), status.remainingSeconds(), status.totalSeconds()));
        }
        return entries;
    }

    private static List<CooldownEntry> collectCooldowns(ServerPlayer player, PlayerCyberwareInventory inventory) {
        List<CooldownEntry> entries = new ArrayList<>();
        for (CyberConsumableCategory category : CyberConsumableCategory.values()) {
            int seconds = CyberConsumableManager.getRemainingCategoryCooldownSeconds(player, category);
            entries.add(new CooldownEntry(category.translationKey(), seconds, resolveConsumableCooldownTotalSeconds(player, category)));
        }

        if (inventory.hasInstalledCyberware("biomonitor")) {
            addTriggeredCooldown(entries, "item.cyberneticenhancements.biomonitor", CyberwareAbilities.getBiomonitorCooldownSecondsRemaining(player), 45);
        }
        if (inventory.hasInstalledCyberware("blood_pump")) {
            addTriggeredCooldown(entries, "item.cyberneticenhancements.blood_pump", CyberwareAbilities.getBloodPumpCooldownSecondsRemaining(player), 60);
        }
        if (inventory.hasInstalledCyberware("second_heart")) {
            addTriggeredCooldown(entries, "item.cyberneticenhancements.second_heart", CyberwareAbilities.getSecondHeartCooldownSecondsRemaining(player), 180);
        }
        if (inventory.hasInstalledCyberware("reflex_tuner")) {
            addTriggeredCooldown(entries, "item.cyberneticenhancements.reflex_tuner", CyberwareAbilities.getReflexTunerCooldownSecondsRemaining(player), 40);
        }

        return entries;
    }

    private static void addTriggeredCooldown(List<CooldownEntry> entries, String translationKey, int seconds, int totalSeconds) {
        if (seconds > 0) {
            entries.add(new CooldownEntry(translationKey, seconds, totalSeconds));
        }
    }

    private static boolean isHudTrackedEffect(CyberwareEffectType type) {
        return switch (type) {
            case MAX_HEALTH,
                 ATTACK_DAMAGE,
                 ATTACK_SPEED,
                 MOVEMENT_SPEED,
                 BLOCK_BREAK_SPEED,
                 CHROME_CAPACITY,
                 HEALTH_REGEN,
                 DAMAGE_REDUCTION,
                 BONUS_ABSORPTION,
                 WATER_BREATHING -> true;
            default -> false;
        };
    }

    private static int effectPriority(CyberwareEffectType type) {
        return switch (type) {
            case HEALTH_REGEN -> 0;
            case BONUS_ABSORPTION -> 1;
            case MAX_HEALTH -> 2;
            case CHROME_CAPACITY -> 3;
            case DAMAGE_REDUCTION -> 4;
            case MOVEMENT_SPEED -> 5;
            case ATTACK_DAMAGE -> 6;
            case ATTACK_SPEED -> 7;
            case BLOCK_BREAK_SPEED -> 8;
            case WATER_BREATHING -> 9;
            default -> 20;
        };
    }

    private static String resolveAbilityKey(ServerPlayer player, PlayerCyberwareInventory inventory) {
        OperatingSystemFamily family = CyberwareAbilities.getActiveFamily(player);
        if (family == OperatingSystemFamily.NONE) {
            CyberwareItem operatingSystem = inventory.getInstalledCyberwareBySlotType(CyberwareSlotType.OPERATING_SYSTEM);
            if (operatingSystem != null) {
                family = CyberwareAbilities.getOperatingSystemFamily(operatingSystem);
            }
        }

        return switch (family) {
            case SANDEVISTAN -> "hud.cyberneticenhancements.ability.sandevistan";
            case BERSERK -> "hud.cyberneticenhancements.ability.berserk";
            case NONE -> "";
        };
    }

    private static int resolveAbilityCooldownTotalSeconds(ServerPlayer player, PlayerCyberwareInventory inventory, int abilityCooldownSeconds) {
        int trackedCooldown = CyberwareAbilities.getAbilityCooldownTotalSeconds(player);
        if (trackedCooldown > 0) {
            return trackedCooldown;
        }

        CyberwareItem operatingSystem = inventory.getInstalledCyberwareBySlotType(CyberwareSlotType.OPERATING_SYSTEM);
        if (operatingSystem == null) {
            return 0;
        }

        OperatingSystemFamily family = CyberwareAbilities.getOperatingSystemFamily(operatingSystem);
        int baseSeconds = switch (family) {
            case SANDEVISTAN -> 30;
            case BERSERK -> 35;
            case NONE -> 0;
        };
        if (baseSeconds == 0) {
            return Math.max(abilityCooldownSeconds, 0);
        }
        return (int) Math.max(1L, Math.round(baseSeconds * CyberstrainManager.getAbilityCooldownMultiplier(player, inventory)));
    }

    private static int resolveConsumableCooldownTotalSeconds(ServerPlayer player, CyberConsumableCategory category) {
        int tracked = CyberConsumableManager.getCategoryCooldownTotalSeconds(player, category);
        if (tracked > 0) {
            return tracked;
        }

        return switch (category) {
            case MEDICAL -> 100;
            case BOOSTER -> 200;
            case NEURAL -> 140;
            case SUPPRESSANT -> 240;
            case STREET -> 220;
        };
    }

    private static String psychosisStateKey(ServerPlayer player, PlayerCyberwareInventory inventory) {
        return "hud.cyberneticenhancements.state." + CyberstrainManager.getPsychosisStateLabel(player, inventory).toLowerCase(java.util.Locale.ROOT);
    }
}
