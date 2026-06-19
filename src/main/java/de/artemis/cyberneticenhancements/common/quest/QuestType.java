package de.artemis.cyberneticenhancements.common.quest;

public enum QuestType {
    DELIVERY("delivery"),
    EXTRACTION("extraction"),
    INVESTIGATION("investigation"),
    BREACH("breach"),
    ELIMINATION("elimination"),
    RECOVERY("recovery");

    private final String id;

    QuestType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static QuestType fromId(String id) {
        for (QuestType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return DELIVERY;
    }
}
