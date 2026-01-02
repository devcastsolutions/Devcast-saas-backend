package com.devcast.saas.model.enums;

public enum UserRole {
    ADMIN("Administrator", "Full system access"),
    MANAGER("Manager", "Management and user access"),
    USER("User", "Standard user access"),
    GUEST("Guest", "Limited guest access");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
