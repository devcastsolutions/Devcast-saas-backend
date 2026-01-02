package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    ACTIVE("Active", "Project is currently active"),
    INACTIVE("Inactive", "Project is inactive"),
    ON_HOLD("On Hold", "Project is temporarily on hold"),
    COMPLETED("Completed", "Project is completed"),
    ARCHIVED("Archived", "Project is archived");

    private final String displayName;
    private final String description;

    ProjectStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
