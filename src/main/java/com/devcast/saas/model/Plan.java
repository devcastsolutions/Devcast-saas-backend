package com.devcast.saas.model;

import com.devcast.saas.model.enums.PlanType;
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
@Table(name = "plans", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})
})
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @NotBlank(message = "Plan name is required")
    @Size(min = 2, max = 100, message = "Plan name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Plan type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType = PlanType.BASIC;

    @NotNull(message = "Monthly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Monthly price must have up to 10 integer digits and 2 decimal places")
    @Column(name = "monthly_price", nullable = false)
    private BigDecimal monthlyPrice;

    @NotNull(message = "Annual price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Annual price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Annual price must have up to 10 integer digits and 2 decimal places")
    @Column(name = "annual_price", nullable = false)
    private BigDecimal annualPrice;

    @Min(value = 0, message = "Max users must be 0 or greater")
    @Column(name = "max_users")
    private Integer maxUsers;

    @Min(value = 0, message = "Max projects must be 0 or greater")
    @Column(name = "max_projects")
    private Integer maxProjects;

    @Min(value = 0, message = "Max storage must be 0 or greater")
    @Column(name = "max_storage_gb")
    private Integer maxStorageGb;

    @NotNull(message = "Active status is required")
    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
