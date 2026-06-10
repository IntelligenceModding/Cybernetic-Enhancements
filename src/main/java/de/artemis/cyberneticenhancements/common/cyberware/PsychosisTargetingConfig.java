package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public final class PsychosisTargetingConfig {
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve(CyberneticEnhancements.MOD_ID + "-psychosis-targeting.properties");
    private static final boolean DEFAULT_IGNORE_TAMED_ANIMALS = true;
    private static final Set<String> DEFAULT_IGNORED_ENTITY_TYPES = Set.of(
            "minecraft:bat",
            "minecraft:cod",
            "minecraft:salmon",
            "minecraft:tropical_fish",
            "minecraft:pufferfish",
            "minecraft:dolphin",
            "minecraft:squid",
            "minecraft:glow_squid",
            "minecraft:silverfish",
            "minecraft:endermite"
    );

    private static boolean ignoreTamedAnimals = DEFAULT_IGNORE_TAMED_ANIMALS;
    private static Set<String> ignoredEntityTypes = DEFAULT_IGNORED_ENTITY_TYPES;
    private static boolean initialized;

    private PsychosisTargetingConfig() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        Properties properties = loadProperties();
        ignoreTamedAnimals = readBoolean(properties, "ignore_tamed_animals", DEFAULT_IGNORE_TAMED_ANIMALS);
        ignoredEntityTypes = parseEntityTypeList(properties.getProperty("ignore_entity_types"), DEFAULT_IGNORED_ENTITY_TYPES);
        writeConfig();
        initialized = true;
    }

    public static boolean ignoreTamedAnimals() {
        ensureInitialized();
        return ignoreTamedAnimals;
    }

    public static Set<String> ignoredEntityTypes() {
        ensureInitialized();
        return ignoredEntityTypes;
    }

    private static void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        if (!Files.exists(CONFIG_PATH)) {
            return properties;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            properties.load(reader);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load psychosis targeting config: " + CONFIG_PATH, exception);
        }
    }

    private static void writeConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                writer.write("# Cyberpsychosis targeting config\n");
                writer.write("# Edit values and restart the game/server to apply them.\n");
                writer.write("# ignore_entity_types uses comma-separated entity type ids.\n\n");
                writer.write("ignore_tamed_animals=" + ignoreTamedAnimals + "\n");
                writer.write("ignore_entity_types=" + String.join(",", ignoredEntityTypes) + "\n");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write psychosis targeting config: " + CONFIG_PATH, exception);
        }
    }

    private static boolean readBoolean(Properties properties, String key, boolean defaultValue) {
        String raw = properties.getProperty(key);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(raw.trim());
    }

    private static Set<String> parseEntityTypeList(String raw, Set<String> defaults) {
        if (raw == null || raw.isBlank()) {
            return defaults;
        }

        LinkedHashSet<String> values = new LinkedHashSet<>();
        Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(String::toLowerCase)
                .forEach(values::add);
        return values.isEmpty() ? defaults : Set.copyOf(values);
    }
}
