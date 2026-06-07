package de.artemis.cyberneticenhancements.client;

import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

final class HudVisibilityController {
    private static final String FILE_NAME = "cyberneticenhancements-client.properties";
    private static final String HUD_ENABLED_KEY = "hud.enabled";

    private static boolean loaded;
    private static boolean hudEnabled = true;

    private HudVisibilityController() {
    }

    static boolean isHudEnabled() {
        ensureLoaded();
        return hudEnabled;
    }

    static boolean toggleHud() {
        ensureLoaded();
        hudEnabled = !hudEnabled;
        save();
        return hudEnabled;
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }

        loaded = true;
        Path configPath = getConfigPath();
        if (configPath == null || !Files.exists(configPath)) {
            return;
        }

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(configPath)) {
            properties.load(inputStream);
            hudEnabled = Boolean.parseBoolean(properties.getProperty(HUD_ENABLED_KEY, Boolean.TRUE.toString()));
        } catch (IOException ignored) {
            hudEnabled = true;
        }
    }

    private static void save() {
        Path configPath = getConfigPath();
        if (configPath == null) {
            return;
        }

        try {
            Files.createDirectories(configPath.getParent());
            Properties properties = new Properties();
            properties.setProperty(HUD_ENABLED_KEY, Boolean.toString(hudEnabled));
            try (OutputStream outputStream = Files.newOutputStream(configPath)) {
                properties.store(outputStream, "Cybernetic Enhancements client settings");
            }
        } catch (IOException ignored) {
        }
    }

    private static Path getConfigPath() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.gameDirectory == null) {
            return null;
        }
        return minecraft.gameDirectory.toPath().resolve("config").resolve(FILE_NAME);
    }
}
