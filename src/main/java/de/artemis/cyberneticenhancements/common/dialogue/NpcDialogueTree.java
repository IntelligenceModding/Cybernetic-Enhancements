package de.artemis.cyberneticenhancements.common.dialogue;

import java.util.List;
import java.util.Map;

public record NpcDialogueTree(
        int rootNodeId,
        Map<Integer, NpcDialogueNode> nodes
) {
    public NpcDialogueNode root() {
        return node(rootNodeId);
    }

    public NpcDialogueNode node(int nodeId) {
        NpcDialogueNode node = nodes.get(nodeId);
        if (node == null) {
            throw new IllegalArgumentException("Unknown dialogue node id: " + nodeId);
        }
        return node;
    }

    public NpcDialogueChoice choice(int nodeId, int choiceId) {
        return node(nodeId).choices().stream()
                .filter(choice -> choice.id() == choiceId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown dialogue choice " + choiceId + " for node " + nodeId));
    }

    public List<NpcDialogueChoice> choices(int nodeId) {
        return node(nodeId).choices();
    }
}
