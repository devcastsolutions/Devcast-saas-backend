package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum PlanType {
    BASIC("Basic", "Basic plan with limited features"),
    PROFESSIONAL("Professional", "Professional plan with more features"),
    ENTERPRISE("Enterprise", "Enterprise plan with all features");

    private final String displayName;
    private final String description;

    PlanType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
