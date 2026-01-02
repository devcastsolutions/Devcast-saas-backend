package com.devcast.saas.model.enums;

import lombok.Getter;

@Getter
public enum InvoiceStatus {
    PENDING("Pending", "Invoice pending payment"),
    PAID("Paid", "Invoice has been paid"),
    OVERDUE("Overdue", "Invoice payment is overdue"),
    CANCELLED("Cancelled", "Invoice is cancelled"),
    REFUNDED("Refunded", "Invoice has been refunded");

    private final String displayName;
    private final String description;

    InvoiceStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
