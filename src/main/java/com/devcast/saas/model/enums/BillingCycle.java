package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum BillingCycle {
    MONTHLY("Monthly", "Billed every month"),
    QUARTERLY("Quarterly", "Billed every 3 months"),
    ANNUAL("Annual", "Billed every year");

    private final String displayName;
    private final String description;

    BillingCycle(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
