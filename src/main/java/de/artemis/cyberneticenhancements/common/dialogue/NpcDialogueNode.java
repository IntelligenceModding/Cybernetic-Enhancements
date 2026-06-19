package de.artemis.cyberneticenhancements.common.dialogue;

import java.util.List;

public record NpcDialogueNode(
        int id,
        String npcLineKey,
        List<NpcDialogueChoice> choices
) {
}
