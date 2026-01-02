package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum NotificationStatus {
    UNREAD("Unread", "Notification not yet read"),
    READ("Read", "Notification has been read"),
    ARCHIVED("Archived", "Notification is archived");

    private final String displayName;
    private final String description;

    NotificationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
