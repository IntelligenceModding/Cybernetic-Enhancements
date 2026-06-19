package de.artemis.cyberneticenhancements.common.entity;

import net.minecraft.network.chat.Component;

public enum NpcType {
    FIXER("fixer", true, true, true, false, true, true),
    RIPPERDOC("ripperdoc", false, true, true, false, false, true),
    TECHIE("techie", false, false, true, true, true, false),
    NETRUNNER("netrunner", true, false, true, true, true, true),
    MERC("merc", false, false, true, true, true, false);

    private final String id;
    private final boolean supportsIntel;
    private final boolean supportsMedicalServices;
    private final boolean supportsShop;
    private final boolean supportsContracts;
    private final boolean supportsTrustDiscount;
    private final boolean usesPatientFocus;

    NpcType(
            String id,
            boolean supportsIntel,
            boolean supportsMedicalServices,
            boolean supportsShop,
            boolean supportsContracts,
            boolean supportsTrustDiscount,
            boolean usesPatientFocus
    ) {
        this.id = id;
        this.supportsIntel = supportsIntel;
        this.supportsMedicalServices = supportsMedicalServices;
        this.supportsShop = supportsShop;
        this.supportsContracts = supportsContracts;
        this.supportsTrustDiscount = supportsTrustDiscount;
        this.usesPatientFocus = usesPatientFocus;
    }

    public String id() {
        return id;
    }

    public boolean supportsIntel() {
        return supportsIntel;
    }

    public boolean supportsMedicalServices() {
        return supportsMedicalServices;
    }

    public boolean supportsShop() {
        return supportsShop;
    }

    public boolean supportsContracts() {
        return supportsContracts;
    }

    public boolean supportsTrustDiscount() {
        return supportsTrustDiscount;
    }

    public boolean usesPatientFocus() {
        return usesPatientFocus;
    }

    public Component displayName() {
        return Component.translatable("screen.cyberneticenhancements.npc.type." + id);
    }

    public static NpcType fromId(String id) {
        for (NpcType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return FIXER;
    }
}
