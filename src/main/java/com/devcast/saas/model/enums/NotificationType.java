package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    INFO("Information", "Informational notification"),
    WARNING("Warning", "Warning notification"),
    ERROR("Error", "Error notification"),
    SUCCESS("Success", "Success notification"),
    TASK_ASSIGNED("Task Assigned", "Task assignment notification"),
    TEAM_INVITE("Team Invite", "Team invitation notification"),
    SUBSCRIPTION_ALERT("Subscription Alert", "Subscription related alert");

    private final String displayName;
    private final String description;

    NotificationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
