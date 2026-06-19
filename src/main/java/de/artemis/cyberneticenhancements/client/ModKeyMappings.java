package de.artemis.cyberneticenhancements.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class ModKeyMappings {
    public static final String CATEGORY = "key.categories.cyberneticenhancements";
    public static final String ABILITY_OS = "activate_cyberware";
    public static final String ABILITY_AUX = "activate_auxiliary_cyberware";
    public static final String ABILITY_ARMS = "activate_arm_cyberware";
    public static final String ABILITY_FACE = "activate_face_cyberware";
    public static final KeyMapping ACTIVATE_CYBERWARE = new KeyMapping(
            "key.cyberneticenhancements.activate_cyberware",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );
    public static final KeyMapping ACTIVATE_AUXILIARY_CYBERWARE = new KeyMapping(
            "key.cyberneticenhancements.activate_auxiliary_cyberware",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
    );
    public static final KeyMapping ACTIVATE_ARM_CYBERWARE = new KeyMapping(
            "key.cyberneticenhancements.activate_arm_cyberware",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
    );
    public static final KeyMapping ACTIVATE_FACE_CYBERWARE = new KeyMapping(
            "key.cyberneticenhancements.activate_face_cyberware",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            CATEGORY
    );
    public static final KeyMapping TOGGLE_HUD = new KeyMapping(
            "key.cyberneticenhancements.toggle_hud",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
    );
    public static final KeyMapping OPEN_ARCHIVE = new KeyMapping(
            "key.cyberneticenhancements.open_archive",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            CATEGORY
    );

    private ModKeyMappings() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE_CYBERWARE);
        event.register(ACTIVATE_AUXILIARY_CYBERWARE);
        event.register(ACTIVATE_ARM_CYBERWARE);
        event.register(ACTIVATE_FACE_CYBERWARE);
        event.register(TOGGLE_HUD);
        event.register(OPEN_ARCHIVE);
    }

    public static Component getAbilityBindingLabel(String bindingId) {
        return switch (bindingId) {
            case ABILITY_OS -> ACTIVATE_CYBERWARE.getTranslatedKeyMessage();
            case ABILITY_AUX -> ACTIVATE_AUXILIARY_CYBERWARE.getTranslatedKeyMessage();
            case ABILITY_ARMS -> ACTIVATE_ARM_CYBERWARE.getTranslatedKeyMessage();
            case ABILITY_FACE -> ACTIVATE_FACE_CYBERWARE.getTranslatedKeyMessage();
            default -> Component.empty();
        };
    }
}
