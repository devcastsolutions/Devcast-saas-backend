# DevCast SaaS Backend - Models Documentation

## Overview

This document provides detailed documentation of all data models (entities) in the DevCast SaaS Backend Platform.

---

## Model Hierarchy

```
┌──────────────────────────┐
│        Users             │
├──────────────────────────┤
│ - User Authentication    │
│ - Profile Information    │
│ - Account Status         │
└────────────┬─────────────┘
             │
             ├────────────────────────────┐
             │                            │
        ┌────▼────┐              ┌───────▼──────┐
        │  Roles  │              │ Permissions  │
        └────┬────┘              └──────────────┘
             │                         ▲
             └────────┬────────────────┘
                      │
                 (Junction Tables)
```

---

## Detailed Model Documentation

### 1. Users Model

**Location:** `src/main/java/com/devcast/saas/model/Users.java`

**Annotations:**
- `@Entity` - JPA entity mapping to database table
- `@Data` - Lombok annotation for auto-generated getters/setters/toString/equals/hashCode
- `@Table(name = "users")` - Maps to `users` table

**Attributes:**

#### Primary Key
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "user_id")
private Long userId;
```
- Auto-incremented long integer
- Uniquely identifies each user in the system

#### User Identity Fields

**First Name**
```java
@NotBlank(message = "First name is required")
@Size(max = 50, message = "First name must not exceed 50 characters")
@Column(name = "first_name", nullable = false)
private String firstName;
```
- Required field (cannot be null or blank)
- Maximum 50 characters
- Validated at the application level

**Last Name**
```java
@NotBlank(message = "Last name is required")
@Size(max = 50, message = "Last name must not exceed 50 characters")
@Column(name = "last_name", nullable = false)
private String lastName;
```
- Required field (cannot be null or blank)
- Maximum 50 characters
- Validated at the application level

#### Contact Information

**Email**
```java
@NotBlank(message = "Email is required")
@Email(message = "Email should be valid")
@Column(unique = true, nullable = false)
private String email;
```
- Unique constraint at database level
- Must be valid email format
- Used for account recovery and communication

**Username**
```java
@NotBlank(message = "Username is required")
@Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
@Column(unique = true, nullable = false)
private String username;
```
- Unique constraint at database level
- Between 3-30 characters
- Used for login authentication

#### Authentication

**Password**
```java
@NotBlank(message = "Password is required")
@Size(min = 8, message = "Password must be at least 8 characters")
@Column(nullable = false, updatable = true)
@JsonIgnore // Never serialize password
private String password;
```
- Minimum 8 characters
- Encrypted before storage (using bcrypt or similar)
- Marked with `@JsonIgnore` to prevent exposure in API responses
- Field is updatable for password changes

#### Profile Information

**Profile Image URL**
```java
@Column(name = "profile_image_url")
private String profileImageUrl;
```
- Optional field
- Stores URL to user's profile picture
- Typically points to cloud storage (S3, CDN, etc.)

#### Status Management

**Status**
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private UserStatus status = UserStatus.ACTIVE;
```
- Enum field with default value `ACTIVE`
- Possible values: `ACTIVE`, `INACTIVE`, `BANNED`
- Used to control account accessibility

**Safe Status Getter**
```java
public UserStatus getSafeStatus() {
    return status != null ? status : UserStatus.ACTIVE;
}
```
- Utility method that returns `ACTIVE` if status is null
- Prevents null pointer exceptions

#### Role Assignment

**Roles Collection**
```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
@JsonIgnore // Ignore in JSON serialization to prevent circular references
private Set<Role> roles = new HashSet<>();
```
- Many-to-Many relationship with Role entities
- Uses `user_roles` junction table
- `FetchType.LAZY` prevents eager loading and N+1 query problems
- `@JsonIgnore` prevents circular reference serialization
- Initialized as `HashSet` for uniqueness and performance

---

### 2. Role Model

**Location:** `src/main/java/com/devcast/saas/model/Role.java`

**Annotations:**
- `@Entity` - JPA entity mapping
- `@Table(name = "roles")` - Maps to `roles` table
- `@Data` - Lombok annotation
- `@NoArgsConstructor` - Default constructor

**Attributes:**

#### Primary Key
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
- Auto-incremented long integer
- Uniquely identifies each role

#### Role Identity

**Name**
```java
@Column(unique = true, nullable = false)
private String name;
```
- Unique constraint (no duplicate role names)
- Required field
- Examples: "Admin", "Manager", "User"

**Description**
```java
@Column
private String description;
```
- Optional field
- Describes role purpose and responsibilities
- Example: "Administrator with full system access"

#### Permission Assignment

**Permissions Collection**
```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"))
private Set<Permission> permissions = new HashSet<>();
```
- Many-to-Many relationship with Permission entities
- Uses `role_permissions` junction table
- `FetchType.LAZY` for performance optimization
- Initialized as `HashSet` for uniqueness

#### Constructors

**Constructor with Parameters**
```java
public Role(String name, String description) {
    this.name = name;
    this.description = description;
}
```
- Allows creating Role instances with name and description
- Used during role initialization

---

### 3. Permission Model

**Location:** `src/main/java/com/devcast/saas/model/Permission.java`

**Annotations:**
- `@Entity` - JPA entity mapping
- `@Table(name = "permissions")` - Maps to `permissions` table
- `@Data` - Lombok annotation
- `@NoArgsConstructor` - Default constructor

**Attributes:**

#### Primary Key
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
- Auto-incremented long integer
- Uniquely identifies each permission

#### Permission Identity

**Name**
```java
@Column(unique = true, nullable = false)
private String name;
```
- Unique constraint (no duplicate permission names)
- Required field
- Naming convention: `{action}_{resource}` (e.g., "create_users", "delete_projects")

**Description**
```java
@Column
private String description;
```
- Optional field
- Describes what the permission allows
- Example: "Allows user to create new user accounts"

#### Permission Details

**Resource**
```java
@Column
private String resource;
```
- Optional field
- Specifies the resource this permission applies to
- Examples: "users", "projects", "reports"

**Action**
```java
@Column
private String action;
```
- Optional field
- Specifies the action this permission allows
- Common actions: "create", "read", "update", "delete", "view", "manage"

---

### 4. UserRole Enum

**Location:** `src/main/java/com/devcast/saas/model/enums/UserRole.java`

```java
public enum UserRole {
    ADMIN,
}
```

**Purpose:** Enumeration of user role types at the application level.

**Current Values:**
- `ADMIN` - Administrator role with elevated privileges

**Usage:**
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private UserRole role;
```

**Notes:**
- Stored as STRING in database for readability
- Can be extended with additional roles: `MANAGER`, `USER`, `GUEST`, `EMPLOYEE`, `CONTRACTOR`, etc.
- Complements the database-level Role entity for flexible permission management

---

### 5. UserStatus Enum

**Location:** `src/main/java/com/devcast/saas/model/enums/UserStatus.java`

```java
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BANNED
}
```

**Purpose:** Enumeration of user account status values.

**Values:**

| Status | Description |
|--------|---|
| `ACTIVE` | Account is active and usable (default) |
| `INACTIVE` | Account is inactive; user cannot login |
| `BANNED` | Account is suspended; user cannot access system |

**Usage:**
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private UserStatus status = UserStatus.ACTIVE;
```

**Default Value:** `ACTIVE`

---

## Model Relationships Summary

### Users ↔ Roles (Many-to-Many)

**Relationship Type:** Many-to-Many  
**Junction Table:** `user_roles`

**Cardinality:**
- One user can have multiple roles
- One role can be assigned to multiple users

**Join Columns:**
- `user_id` (Foreign Key → Users.userId)
- `role_id` (Foreign Key → Roles.id)

**Cascade Rules:**
- `CascadeType.ALL` with orphan removal
- Deleting a user removes associated role assignments
- Deleting a role removes its user assignments

### Roles ↔ Permissions (Many-to-Many)

**Relationship Type:** Many-to-Many  
**Junction Table:** `role_permissions`

**Cardinality:**
- One role can have multiple permissions
- One permission can be assigned to multiple roles

**Join Columns:**
- `role_id` (Foreign Key → Roles.id)
- `permission_id` (Foreign Key → Permissions.id)

**Cascade Rules:**
- Deleting a role removes associated permission assignments
- Deleting a permission removes its role assignments

---

## Data Validation

### Validation Annotations Used

| Annotation | Field | Message |
|-----------|-------|---------|
| `@NotBlank` | firstName, lastName, email, username, password | Field is required |
| `@Size` | firstName, lastName, username, password | Field size must meet constraints |
| `@Email` | email | Email must be valid format |
| `@NotNull` | role | Role is required |

### Validation at Multiple Levels

1. **Database Level:** NOT NULL, UNIQUE constraints
2. **JPA Level:** Annotations like `@NotBlank`, `@Email`, `@Size`
3. **Application Level:** Custom validation in services

---

## JSON Serialization Behavior

### Fields Ignored in JSON

- `password` - Never exposed via API (marked with `@JsonIgnore`)
- `roles` - Prevents circular reference (marked with `@JsonIgnore`)

### JSON Response Example

```json
{
  "userId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "username": "johndoe",
  "role": "ADMIN",
  "status": "ACTIVE",
  "profileImageUrl": "https://cdn.example.com/profiles/1.jpg"
}
```

Note: `password` and `roles` fields are not included in the response.

---

## Performance Considerations

### Lazy Loading Strategy

All relationships use `FetchType.LAZY`:
```java
@ManyToMany(fetch = FetchType.LAZY)
```

**Benefits:**
- Prevents N+1 query problems
- Reduces initial query load time
- Data is loaded only when explicitly accessed

**Trade-off:** May cause `LazyInitializationException` if accessed outside transaction context

### Preventing Circular References

```java
@JsonIgnore
private Set<Role> roles = new HashSet<>();
```

**Purpose:** Prevents infinite loops during JSON serialization
- Users without roles serialization
- Roles without users serialization
- Permissions can maintain circular reference to roles

---

## Extension Points

The model structure allows for easy extension:

1. **Additional User Fields:** Phone, address, department, manager
2. **User Preferences:** Language, timezone, notification settings
3. **Audit Fields:** created_at, updated_at, created_by, updated_by
4. **Soft Deletes:** deleted_at field for logical deletion
5. **Multi-tenancy:** organization_id for tenant isolation

---

**Last Updated:** January 2, 2026  
**Version:** 1.0  
**Author:** DevCast Solutions
