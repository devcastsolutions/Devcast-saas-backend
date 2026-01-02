package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum TeamMemberRole {
    ADMIN("Administrator", "Full access to team resources"),
    MANAGER("Manager", "Can manage team members and projects"),
    MEMBER("Member", "Can contribute to projects"),
    VIEWER("Viewer", "Read-only access to projects"),
    GUEST("Guest", "Limited guest access");

    private final String displayName;
    private final String description;

    TeamMemberRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
