package de.artemis.cyberneticenhancements.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class ModKeyMappings {
    public static final String CATEGORY = "key.categories.cyberneticenhancements";
    public static final KeyMapping ACTIVATE_CYBERWARE = new KeyMapping(
            "key.cyberneticenhancements.activate_cyberware",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    private ModKeyMappings() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE_CYBERWARE);
    }
}
