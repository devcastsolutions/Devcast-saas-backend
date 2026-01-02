package com.devcast.saas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "organizations")
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long organizationId;

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Organization email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    @URL(message = "Website should be a valid URL")
    @Column(name = "website_url")
    private String websiteUrl;

    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    @URL(message = "Logo should be a valid URL")
    @Column(name = "logo_url")
    private String logoUrl;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(name = "address")
    private String address;

    @Size(max = 50, message = "City must not exceed 50 characters")
    @Column(name = "city")
    private String city;

    @Size(max = 50, message = "State must not exceed 50 characters")
    @Column(name = "state")
    private String state;

    @Size(max = 20, message = "Zip code must not exceed 20 characters")
    @Column(name = "zip_code")
    private String zipCode;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(name = "country")
    private String country;

    @NotNull(message = "Active status is required")
    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Users owner;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamMember> teamMembers = new HashSet<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Subscription> subscriptions = new HashSet<>();
}
