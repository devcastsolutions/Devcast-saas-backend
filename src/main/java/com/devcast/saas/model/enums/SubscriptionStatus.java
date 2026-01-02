package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    ACTIVE("Active", "Subscription is active"),
    PAUSED("Paused", "Subscription is paused"),
    CANCELLED("Cancelled", "Subscription is cancelled"),
    EXPIRED("Expired", "Subscription has expired"),
    TRIAL("Trial", "Free trial period");

    private final String displayName;
    private final String description;

    SubscriptionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
