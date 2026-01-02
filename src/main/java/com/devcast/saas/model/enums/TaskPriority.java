package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum TaskPriority {
    CRITICAL("Critical", "Must be done immediately"),
    HIGH("High", "Should be done soon"),
    MEDIUM("Medium", "Can be done in regular course"),
    LOW("Low", "Can be done later");

    private final String displayName;
    private final String description;

    TaskPriority(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
