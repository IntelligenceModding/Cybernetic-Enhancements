package de.artemis.cyberneticenhancements.client.screen;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;

final class TextFieldFocusHelper {
    private TextFieldFocusHelper() {
    }

    static boolean unfocusOnOutsideClick(Screen screen, double mouseX, double mouseY, List<EditBox> fields) {
        boolean hadFocusedField = false;
        for (EditBox field : fields) {
            if (field == null || !field.isVisible() || !field.isFocused()) {
                continue;
            }
            hadFocusedField = true;
            if (field.isMouseOver(mouseX, mouseY)) {
                return false;
            }
        }
        if (!hadFocusedField) {
            return false;
        }
        clearFocus(screen, fields);
        return true;
    }

    static void clearFocus(Screen screen, List<EditBox> fields) {
        for (EditBox field : fields) {
            if (field != null) {
                field.setFocused(false);
            }
        }
        screen.setFocused(null);
    }
}
