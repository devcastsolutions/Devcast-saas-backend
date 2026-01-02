package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum AuditAction {
    CREATE("Create", "Record created"),
    READ("Read", "Record accessed"),
    UPDATE("Update", "Record updated"),
    DELETE("Delete", "Record deleted"),
    LOGIN("Login", "User logged in"),
    LOGOUT("Logout", "User logged out"),
    EXPORT("Export", "Data exported"),
    IMPORT("Import", "Data imported");

    private final String displayName;
    private final String description;

    AuditAction(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
