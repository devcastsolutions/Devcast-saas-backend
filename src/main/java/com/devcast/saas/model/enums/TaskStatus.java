package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    TODO("To Do", "Task not yet started"),
    IN_PROGRESS("In Progress", "Task is currently being worked on"),
    IN_REVIEW("In Review", "Task is under review"),
    COMPLETED("Completed", "Task is completed"),
    BLOCKED("Blocked", "Task is blocked"),
    CANCELLED("Cancelled", "Task is cancelled");

    private final String displayName;
    private final String description;

    TaskStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
