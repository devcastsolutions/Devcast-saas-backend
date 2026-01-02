package com.devcast.saas.model.enums;

public enum UserStatus {
    ACTIVE("Active", "User account is active"),
    INACTIVE("Inactive", "User account is inactive"),
    PENDING("Pending", "User account is pending verification"),
    SUSPENDED("Suspended", "User account is temporarily suspended"),
    BANNED("Banned", "User account is permanently banned"),
    DELETED("Deleted", "User account is deleted");

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}
