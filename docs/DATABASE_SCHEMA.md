# DevCast SaaS Backend - Database Schema Documentation

## Overview

This document provides a comprehensive description of the DevCast SaaS Backend Platform database schema. The database is designed to support a multi-tenant, enterprise-grade SaaS application with secure user management, role-based access control (RBAC), and scalable architecture.

**Database Type:** MySQL  
**ORM Framework:** Hibernate (JPA)  
**Auto-Migration:** Enabled (`spring.jpa.hibernate.ddl-auto=update`)

---

## Core Tables

### 1. **users**

**Purpose:** Stores user account information and authentication credentials.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `user_id` | BIGINT | PK, AUTO_INCREMENT | Unique user identifier |
| `first_name` | VARCHAR(50) | NOT NULL | User's first name (max 50 chars) |
| `last_name` | VARCHAR(50) | NOT NULL | User's last name (max 50 chars) |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | User's email address (unique across system) |
| `username` | VARCHAR(30) | UNIQUE, NOT NULL | User's login username (3-30 chars) |
| `password` | VARCHAR(255) | NOT NULL | Encrypted password (min 8 chars) |
| `role` | ENUM | NOT NULL | User role: `ADMIN` |
| `status` | ENUM | NOT NULL | Account status: `ACTIVE`, `INACTIVE`, `BANNED` |
| `profile_image_url` | VARCHAR(255) | NULL | URL to user's profile image |

**Relationships:**
- **Many-to-Many with roles** (via `user_roles` junction table)

**Key Constraints:**
- Email must be unique across the entire system
- Username must be unique and between 3-30 characters
- Password is encrypted and never serialized in API responses
- Status defaults to `ACTIVE`

**Indexes:**
- PK: `user_id`
- UNIQUE: `email`, `username`

---

### 2. **roles**

**Purpose:** Defines system roles that can be assigned to users to control access and permissions.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Unique role identifier |
| `name` | VARCHAR(255) | UNIQUE, NOT NULL | Role name (e.g., "Admin", "Manager", "User") |
| `description` | TEXT | NULL | Role description and purpose |

**Relationships:**
- **Many-to-Many with permissions** (via `role_permissions` junction table)
- **Many-to-Many with users** (via `user_roles` junction table)

**Key Constraints:**
- Role name must be unique
- Role name is required

**Indexes:**
- PK: `id`
- UNIQUE: `name`

**Example Roles:**
- Admin (Full system access)
- Manager (Team and project management)
- User (Basic access)

---

### 3. **permissions**

**Purpose:** Defines granular permissions that control specific actions users can perform on resources.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Unique permission identifier |
| `name` | VARCHAR(255) | UNIQUE, NOT NULL | Permission name (e.g., "view_users", "create_project") |
| `description` | TEXT | NULL | Permission description |
| `resource` | VARCHAR(255) | NULL | Resource type (e.g., "users", "projects", "reports") |
| `action` | VARCHAR(255) | NULL | Action type (e.g., "create", "read", "update", "delete") |

**Relationships:**
- **Many-to-Many with roles** (via `role_permissions` junction table)

**Key Constraints:**
- Permission name must be unique

**Indexes:**
- PK: `id`
- UNIQUE: `name`

**Permission Naming Convention:**
- Format: `{action}_{resource}` (e.g., `create_users`, `read_projects`)
- Common actions: `create`, `read`, `update`, `delete`, `view`, `manage`

**Example Permissions:**
- `view_users` - View user list
- `create_projects` - Create new projects
- `delete_reports` - Delete reports
- `manage_roles` - Manage system roles

---

## Junction Tables

### 4. **user_roles**

**Purpose:** Maps users to roles (Many-to-Many relationship).

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `user_id` | BIGINT | PK, FK → users.user_id | Reference to user |
| `role_id` | BIGINT | PK, FK → roles.id | Reference to role |

**Key Constraints:**
- Composite Primary Key: (`user_id`, `role_id`)
- Foreign Key Constraint: `user_id` → users.user_id (CASCADE DELETE)
- Foreign Key Constraint: `role_id` → roles.id (CASCADE DELETE)

**Indexes:**
- PK: (`user_id`, `role_id`)
- FK: `role_id`

**Purpose:** Allows flexible role assignment where a single user can have multiple roles.

---

### 5. **role_permissions**

**Purpose:** Maps roles to permissions (Many-to-Many relationship).

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `role_id` | BIGINT | PK, FK → roles.id | Reference to role |
| `permission_id` | BIGINT | PK, FK → permissions.id | Reference to permission |

**Key Constraints:**
- Composite Primary Key: (`role_id`, `permission_id`)
- Foreign Key Constraint: `role_id` → roles.id (CASCADE DELETE)
- Foreign Key Constraint: `permission_id` → permissions.id (CASCADE DELETE)

**Indexes:**
- PK: (`role_id`, `permission_id`)
- FK: `permission_id`

**Purpose:** Enables granular permission assignment to roles for RBAC implementation.

---

## Data Flow & Relationships

```
┌─────────────────────────────────────────────────────────────┐
│                         USERS                               │
│  (user_id, first_name, last_name, email, password, etc.)    │
└──────────────────────┬──────────────────────────────────────┘
                       │ Many-to-Many
                       │ (via user_roles)
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                         ROLES                               │
│         (id, name, description)                             │
└──────────────────────┬──────────────────────────────────────┘
                       │ Many-to-Many
                       │ (via role_permissions)
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                      PERMISSIONS                            │
│  (id, name, description, resource, action)                  │
└─────────────────────────────────────────────────────────────┘
```

---

## Access Control Model

### Role-Based Access Control (RBAC)

1. **User → Role Assignment**
   - Users are assigned one or more roles via the `user_roles` junction table
   - A user inherits all permissions from their assigned roles

2. **Role → Permission Assignment**
   - Roles are granted specific permissions via the `role_permissions` junction table
   - Permissions define what actions a user can perform

3. **Permission Evaluation**
   - When a user attempts an action, the system checks if any of their roles have the required permission
   - Permissions are typically checked at the API endpoint level using security annotations

### Example Scenario

```
User: john_doe (user_id: 1)
├── Role: Admin (role_id: 1)
│   ├── Permission: create_users
│   ├── Permission: delete_users
│   └── Permission: manage_roles
└── Role: Manager (role_id: 2)
    ├── Permission: create_projects
    └── Permission: view_reports
```

Result: john_doe can perform all 5 permissions above.

---

## Enumerations

### UserRole Enum

Currently defined roles at the application level:
- `ADMIN` - Administrator with elevated privileges

**Note:** This enum can be extended to include additional roles like `MANAGER`, `USER`, `GUEST`, etc.

### UserStatus Enum

User account status values:
- `ACTIVE` - Account is active and usable (default)
- `INACTIVE` - Account is inactive but not deleted
- `BANNED` - Account is suspended and cannot be used

**Default Value:** `ACTIVE`

---

## Security Considerations

### Password Protection
- Passwords are encrypted before storage (never stored in plain text)
- `@JsonIgnore` annotation prevents password from being serialized in API responses
- Minimum password length: 8 characters
- Field is marked as `updatable = true` to allow password changes

### Email & Username Uniqueness
- Both email and username have UNIQUE constraints to prevent duplicates
- Email must be valid format (validated at application level)

### Data Isolation
- Multi-tenant support through organization-level isolation (extendable)
- User data is scoped to their assigned roles and permissions

### Lazy Loading
- Relationships use `FetchType.LAZY` to prevent N+1 query problems
- Circular references prevented using `@JsonIgnore`

---

## Configuration

### Database Connection
```properties
# Database Configuration
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
