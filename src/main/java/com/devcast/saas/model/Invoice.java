package com.devcast.saas.model;

import com.devcast.saas.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invoices", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"invoice_number"})
})
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    @NotBlank(message = "Invoice number is required")
    @Size(min = 1, max = 50, message = "Invoice number must be between 1 and 50 characters")
    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 integer digits and 2 decimal places")
    @Column(nullable = false)
    private BigDecimal amount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Tax must have up to 10 integer digits and 2 decimal places")
    @Column(name = "tax_amount")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Total amount must have up to 10 integer digits and 2 decimal places")
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "paid_date")
    private LocalDateTime paidDate;

    @Size(max = 255, message = "Notes must not exceed 255 characters")
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Size(max = 255, message = "Stripe invoice ID must not exceed 255 characters")
    @Column(name = "stripe_invoice_id")
    private String stripeInvoiceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
