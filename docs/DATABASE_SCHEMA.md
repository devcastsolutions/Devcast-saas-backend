# DevCast SaaS Backend - Database Schema Documentation

## Overview

This document provides a comprehensive description of the DevCast SaaS Backend Platform database schema. The database is designed to support a multi-tenant, enterprise-grade SaaS application with secure user management, role-based access control (RBAC), billing, project management, and scalable architecture.

**Database Type:** MySQL  
**ORM Framework:** Hibernate (JPA)  
**Auto-Migration:** Enabled (`spring.jpa.hibernate.ddl-auto=update`)

---

## Core Tables (16 Total)

### 1. **users**

**Purpose:** Stores user account information and authentication credentials.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `user_id` | BIGINT | PK, AUTO_INCREMENT | Unique user identifier |
| `first_name` | VARCHAR(50) | NOT NULL | User's first name |
| `last_name` | VARCHAR(50) | NOT NULL | User's last name |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | User's email address |
| `username` | VARCHAR(30) | UNIQUE, NOT NULL | User's login username |
| `password` | VARCHAR(255) | NOT NULL | Encrypted password (BCrypt) |
| `role` | ENUM | NOT NULL | Legacy role field (USER, ADMIN, MANAGER) |
| `status` | ENUM | NOT NULL | Account status: ACTIVE, INACTIVE, PENDING, SUSPENDED, BANNED, DELETED |
| `profile_image_url` | VARCHAR(255) | NULL | URL to user's profile image |
| `email_verified` | BOOLEAN | DEFAULT: false | Email verification status |
| `phone_number` | VARCHAR(15) | NULL | User's phone number |
| `created_at` | TIMESTAMP | NOT NULL | Account creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |
| `last_login` | TIMESTAMP | NULL | Last login timestamp |

**Relationships:**
- One-to-Many with TeamMember
- One-to-Many with Organization (as owner)
- One-to-Many with Project (as owner)
- One-to-Many with Task (as assignee or creator)
- Many-to-Many with Role
- Many-to-Many with Project

---

### 2. **organizations**

**Purpose:** Manages multi-tenant organization data and workspace information.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `organization_id` | BIGINT | PK, AUTO_INCREMENT | Unique organization identifier |
| `name` | VARCHAR(100) | NOT NULL | Organization name |
| `description` | TEXT | NULL | Organization description |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Organization email |
| `phone_number` | VARCHAR(15) | NULL | Organization phone |
| `website_url` | VARCHAR(255) | NULL | Organization website URL |
| `logo_url` | VARCHAR(255) | NULL | Organization logo URL |
| `address` | VARCHAR(255) | NULL | Street address |
| `city` | VARCHAR(50) | NULL | City name |
| `state` | VARCHAR(50) | NULL | State/Province |
| `zip_code` | VARCHAR(20) | NULL | Postal code |
| `country` | VARCHAR(100) | NULL | Country name |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT: true | Active status |
| `owner_id` | BIGINT | FK → users.user_id | Organization owner |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |

**Relationships:**
- Many-to-One with Users (owner)
- One-to-Many with TeamMember
- One-to-Many with Team
- One-to-Many with Project
- One-to-Many with Subscription
- One-to-Many with AuditLog
- One-to-Many with Invoice

---

### 3. **teams**

**Purpose:** Manages teams within organizations for collaborative work.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `team_id` | BIGINT | PK, AUTO_INCREMENT | Unique team identifier |
| `name` | VARCHAR(100) | NOT NULL | Team name |
| `description` | TEXT | NULL | Team description |
| `organization_id` | BIGINT | FK → organizations.organization_id | Parent organization |
| `lead_id` | BIGINT | FK → users.user_id | Team lead user |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT: true | Active status |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |

**Constraints:**
- UNIQUE: (organization_id, name) - Team names must be unique within organization

**Relationships:**
- Many-to-One with Organization
- Many-to-One with Users (lead)
- One-to-Many with TeamMember

---

### 4. **team_members**

**Purpose:** Maps users to teams with their assigned roles.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `team_member_id` | BIGINT | PK, AUTO_INCREMENT | Unique member identifier |
| `organization_id` | BIGINT | FK → organizations.organization_id | Organization reference |
| `team_id` | BIGINT | FK → teams.team_id | Team reference |
| `user_id` | BIGINT | FK → users.user_id | User reference |
| `role` | ENUM | NOT NULL, DEFAULT: MEMBER | Role: ADMIN, MANAGER, MEMBER, VIEWER, GUEST |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT: true | Active status |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |
| `joined_at` | TIMESTAMP | NOT NULL | Team join timestamp |

**Constraints:**
- UNIQUE: (team_id, user_id) - User can only join team once
- UNIQUE: (organization_id, user_id) - User can only be org member once

---

### 5. **projects**

**Purpose:** Stores project information for task and team management.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `project_id` | BIGINT | PK, AUTO_INCREMENT | Unique project identifier |
| `name` | VARCHAR(100) | NOT NULL | Project name |
| `description` | TEXT | NULL | Project description |
| `organization_id` | BIGINT | FK → organizations.organization_id | Parent organization |
| `owner_id` | BIGINT | FK → users.user_id | Project owner |
| `status` | ENUM | NOT NULL, DEFAULT: ACTIVE | Status: ACTIVE, INACTIVE, ON_HOLD, COMPLETED, ARCHIVED |
| `repository_url` | VARCHAR(255) | NULL | Git repository URL |
| `start_date` | TIMESTAMP | NULL | Project start date |
| `end_date` | TIMESTAMP | NULL | Project end date |
| `is_public` | BOOLEAN | NOT NULL, DEFAULT: false | Public visibility |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |

**Constraints:**
- UNIQUE: (organization_id, name) - Project names unique within organization

**Relationships:**
- Many-to-One with Organization
- Many-to-One with Users (owner)
- One-to-Many with Task
- Many-to-Many with Users (project_members)

---

### 6. **tasks**

**Purpose:** Manages project tasks with status, priority, and assignment tracking.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `task_id` | BIGINT | PK, AUTO_INCREMENT | Unique task identifier |
| `title` | VARCHAR(200) | NOT NULL | Task title |
| `description` | TEXT | NULL | Task description |
| `project_id` | BIGINT | FK → projects.project_id | Parent project |
| `assigned_to_id` | BIGINT | FK → users.user_id | Assigned user |
| `created_by_id` | BIGINT | FK → users.user_id | Creator user |
| `status` | ENUM | NOT NULL, DEFAULT: TODO | Status: TODO, IN_PROGRESS, IN_REVIEW, COMPLETED, BLOCKED, CANCELLED |
| `priority` | ENUM | NOT NULL, DEFAULT: MEDIUM | Priority: CRITICAL, HIGH, MEDIUM, LOW |
| `due_date` | TIMESTAMP | NULL | Task due date |
| `progress_percentage` | INT | DEFAULT: 0, RANGE: 0-100 | Completion percentage |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |
| `completed_at` | TIMESTAMP | NULL | Completion timestamp |

**Relationships:**
- Many-to-One with Project
- Many-to-One with Users (assignee)
- Many-to-One with Users (creator)

---

### 7. **roles**

**Purpose:** Defines system roles for RBAC implementation.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `role_id` | BIGINT | PK, AUTO_INCREMENT | Unique role identifier |
| `name` | VARCHAR(50) | UNIQUE, NOT NULL | Role name (e.g., Admin, Manager, User) |
| `description` | TEXT | NULL | Role description |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |

**Relationships:**
- Many-to-Many with Users (via user_roles)
- Many-to-Many with Permission (via role_permissions)

---

### 8. **permissions**

**Purpose:** Defines granular permissions for resource access control.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `permission_id` | BIGINT | PK, AUTO_INCREMENT | Unique permission identifier |
| `name` | VARCHAR(100) | UNIQUE, NOT NULL | Permission name (e.g., view_users, create_project) |
| `description` | TEXT | NULL | Permission description |
| `resource` | VARCHAR(50) | NOT NULL | Resource type (users, projects, tasks) |
| `action` | VARCHAR(50) | NOT NULL | Action type (create, read, update, delete) |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |

**Constraints:**
- UNIQUE: (resource, action) - Prevents duplicate resource-action pairs

**Relationships:**
- Many-to-Many with Role (via role_permissions)

---

### 9. **plans**

**Purpose:** Manages subscription plans with pricing and feature limits.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `plan_id` | BIGINT | PK, AUTO_INCREMENT | Unique plan identifier |
| `name` | VARCHAR(100) | UNIQUE, NOT NULL | Plan name (Basic, Professional, Enterprise) |
| `description` | TEXT | NULL | Plan description |
| `plan_type` | ENUM | NOT NULL, DEFAULT: BASIC | Type: BASIC, PROFESSIONAL, ENTERPRISE |
| `monthly_price` | DECIMAL(10,2) | NOT NULL | Monthly subscription price |
| `annual_price` | DECIMAL(10,2) | NOT NULL | Annual subscription price |
| `max_users` | INT | NULL | Maximum team members allowed |
| `max_projects` | INT | NULL | Maximum projects allowed |
| `max_storage_gb` | INT | NULL | Maximum storage in GB |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT: true | Active status |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |

**Relationships:**
- One-to-Many with Subscription

---

### 10. **subscriptions**

**Purpose:** Manages organization subscriptions to pricing plans with billing details.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `subscription_id` | BIGINT | PK, AUTO_INCREMENT | Unique subscription identifier |
| `organization_id` | BIGINT | FK → organizations.organization_id | Organization reference |
| `plan_id` | BIGINT | FK → plans.plan_id | Subscribed plan |
| `status` | ENUM | NOT NULL, DEFAULT: ACTIVE | Status: ACTIVE, PAUSED, CANCELLED, EXPIRED, TRIAL |
| `billing_cycle` | ENUM | NOT NULL, DEFAULT: MONTHLY | Cycle: MONTHLY, QUARTERLY, ANNUAL |
| `start_date` | TIMESTAMP | NOT NULL | Subscription start date |
| `end_date` | TIMESTAMP | NULL | Subscription end date |
| `renewal_date` | TIMESTAMP | NULL | Next renewal date |
| `price` | DECIMAL(10,2) | NOT NULL | Current subscription price |
| `discount_amount` | DECIMAL(10,2) | DEFAULT: 0.00 | Applied discount |
| `stripe_subscription_id` | VARCHAR(255) | NULL | Stripe subscription reference |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |

**Constraints:**
- UNIQUE: (organization_id, plan_id) - One subscription per plan per org

**Relationships:**
- Many-to-One with Organization
- Many-to-One with Plan
- One-to-Many with Invoice

---

### 11. **invoices**

**Purpose:** Tracks billing invoices for subscription payments.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `invoice_id` | BIGINT | PK, AUTO_INCREMENT | Unique invoice identifier |
| `invoice_number` | VARCHAR(50) | UNIQUE, NOT NULL | Invoice reference number |
| `organization_id` | BIGINT | FK → organizations.organization_id | Organization reference |
| `subscription_id` | BIGINT | FK → subscriptions.subscription_id | Related subscription |
| `status` | ENUM | NOT NULL, DEFAULT: PENDING | Status: PENDING, PAID, OVERDUE, CANCELLED, REFUNDED |
| `amount` | DECIMAL(10,2) | NOT NULL | Invoice amount (pre-tax) |
| `tax_amount` | DECIMAL(10,2) | DEFAULT: 0.00 | Tax amount |
| `total_amount` | DECIMAL(10,2) | NOT NULL | Total invoice amount |
| `issued_date` | TIMESTAMP | NOT NULL | Issue date |
| `due_date` | TIMESTAMP | NOT NULL | Payment due date |
| `paid_date` | TIMESTAMP | NULL | Payment received date |
| `notes` | TEXT | NULL | Invoice notes |
| `stripe_invoice_id` | VARCHAR(255) | NULL | Stripe invoice reference |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |

**Relationships:**
- Many-to-One with Organization
- Many-to-One with Subscription

---

### 12. **audit_logs**

**Purpose:** Maintains comprehensive audit trail for compliance and security.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `log_id` | BIGINT | PK, AUTO_INCREMENT | Unique log identifier |
| `user_id` | BIGINT | FK → users.user_id | User performing action |
| `organization_id` | BIGINT | FK → organizations.organization_id | Related organization |
| `action` | ENUM | NOT NULL | Action: CREATE, READ, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT, IMPORT |
| `entity_type` | VARCHAR(100) | NOT NULL | Entity type (users, projects, tasks) |
| `entity_id` | BIGINT | NULL | ID of affected entity |
| `details` | TEXT | NOT NULL | Action details |
| `old_value` | TEXT | NULL | Previous value before change |
| `new_value` | TEXT | NULL | New value after change |
| `ip_address` | VARCHAR(45) | NULL | Client IP address |
| `created_at` | TIMESTAMP | NOT NULL | Action timestamp |

**Indexes:**
- INDEX: user_id, organization_id, created_at

**Relationships:**
- Many-to-One with Users
- Many-to-One with Organization

---

### 13. **notifications**

**Purpose:** Manages user notifications for tasks, invites, and system alerts.

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `notification_id` | BIGINT | PK, AUTO_INCREMENT | Unique notification identifier |
| `user_id` | BIGINT | FK → users.user_id | Recipient user |
| `type` | ENUM | NOT NULL | Type: INFO, WARNING, ERROR, SUCCESS, TASK_ASSIGNED, TEAM_INVITE, SUBSCRIPTION_ALERT |
| `title` | VARCHAR(200) | NOT NULL | Notification title |
| `message` | TEXT | NOT NULL | Notification message |
| `status` | ENUM | NOT NULL, DEFAULT: UNREAD | Status: UNREAD, READ, ARCHIVED |
| `related_entity_type` | VARCHAR(100) | NULL | Related entity type |
| `related_entity_id` | BIGINT | NULL | Related entity ID |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NULL | Last update timestamp |
| `read_at` | TIMESTAMP | NULL | Read timestamp |

**Indexes:**
- INDEX: user_id, status

**Relationships:**
- Many-to-One with Users

---

### 14. **user_roles** (Junction)

**Purpose:** Maps users to roles (Many-to-Many relationship).

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `user_id` | BIGINT | PK, FK → users.user_id | User reference |
| `role_id` | BIGINT | PK, FK → roles.role_id | Role reference |

**Constraints:**
- Composite PK: (user_id, role_id)
- CASCADE DELETE

---

### 15. **role_permissions** (Junction)

**Purpose:** Maps roles to permissions (Many-to-Many relationship).

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `role_id` | BIGINT | PK, FK → roles.role_id | Role reference |
| `permission_id` | BIGINT | PK, FK → permissions.permission_id | Permission reference |

**Constraints:**
- Composite PK: (role_id, permission_id)
- CASCADE DELETE

---

### 16. **project_members** (Junction)

**Purpose:** Maps users to projects (Many-to-Many relationship).

**Columns:**

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `project_id` | BIGINT | PK, FK → projects.project_id | Project reference |
| `user_id` | BIGINT | PK, FK → users.user_id | User reference |

**Constraints:**
- Composite PK: (project_id, user_id)
- CASCADE DELETE

---

## Entity Relationships Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         USERS                                   │
│  (user_id, first_name, last_name, email, password, status)      │
└─┬──────────────┬──────────────┬──────────────┬──────────────────┘
  │              │              │              │
  │ 1-M          │ M-M          │ 1-M          │ 1-M
  │              │ user_roles   │              │
  ▼              ▼              ▼              ▼
┌──────────────────┐  ┌──────────────┐  ┌─────────────────┐
│ TEAM_MEMBERS     │  │ ROLES        │  │ ORGANIZATIONS   │
│ (team_member_id) │  │ (role_id)    │  │ (organization_id)
└──────────────────┘  └──────┬───────┘  └────────┬────────┘
                             │                   │
                             │ M-M               │ 1-M
                             │ role_permissions  │
                             ▼                   ▼
                       ┌──────────────┐  ┌─────────────────┐
                       │ PERMISSIONS  │  │ TEAMS           │
                       │ (permission) │  │ (team_id)       │
                       └──────────────┘  └────────┬────────┘
                                                 │
                                                 │ 1-M
                                                 ▼
┌────────────────────────────────────────────────────────────┐
│                    PROJECTS                                │
│  (project_id, name, status, organization_id)              │
└───────────────────────────┬────────────────────────────────┘
                            │
                            │ 1-M
                            ▼
                       ┌──────────────┐
                       │ TASKS        │
                       │ (task_id)    │
                       └──────────────┘

┌────────────────────────────────────────────────────────────┐
│                    PLANS                                   │
│  (plan_id, name, monthly_price, annual_price)             │
└───────────────────┬───────────────────────────────────────┘
                    │
                    │ 1-M
                    ▼
┌────────────────────────────────────────────────────────────┐
│               SUBSCRIPTIONS                                │
│  (subscription_id, organization_id, plan_id, status)      │
└──────────────────────┬─────────────────────────────────────┘
                       │
                       │ 1-M
                       ▼
┌────────────────────────────────────────────────────────────┐
│                INVOICES                                    │
│  (invoice_id, invoice_number, amount, status)             │
└────────────────────────────────────────────────────────────┘

AUDIT_LOGS and NOTIFICATIONS also connect to USERS and ORGANIZATIONS
```

---

## Enumerations

### UserRole
- ADMIN - Administrator privileges
- MANAGER - Management capabilities
- USER - Basic user access
- GUEST - Limited guest access

### UserStatus
- ACTIVE - Account is active (default)
- INACTIVE - Account inactive
- PENDING - Awaiting verification
- SUSPENDED - Temporarily suspended
- BANNED - Permanently banned
- DELETED - Marked for deletion

### TeamMemberRole
- ADMIN - Full team access
- MANAGER - Team management
- MEMBER - Basic member
- VIEWER - Read-only access
- GUEST - Limited guest access

### ProjectStatus
- ACTIVE - Actively in progress
- INACTIVE - Not in progress
- ON_HOLD - Temporarily paused
- COMPLETED - Project finished
- ARCHIVED - Archived project

### TaskStatus
- TODO - Not started
- IN_PROGRESS - Currently being worked
- IN_REVIEW - Under review
- COMPLETED - Task finished
- BLOCKED - Blocked by issue
- CANCELLED - Cancelled

### TaskPriority
- CRITICAL - Urgent/immediate
- HIGH - Should do soon
- MEDIUM - Regular priority
- LOW - Can do later

### SubscriptionStatus
- ACTIVE - Currently active
- PAUSED - Temporarily paused
- CANCELLED - User cancelled
- EXPIRED - Subscription expired
- TRIAL - Free trial period

### BillingCycle
- MONTHLY - Monthly billing
- QUARTERLY - Quarterly billing
- ANNUAL - Annual billing

### PlanType
- BASIC - Basic tier
- PROFESSIONAL - Professional tier
- ENTERPRISE - Enterprise tier

### AuditAction
- CREATE - Record created
- READ - Record accessed
- UPDATE - Record updated
- DELETE - Record deleted
- LOGIN - User login
- LOGOUT - User logout
- EXPORT - Data exported
- IMPORT - Data imported

### NotificationType
- INFO - Information
- WARNING - Warning
- ERROR - Error
- SUCCESS - Success
- TASK_ASSIGNED - Task assignment
- TEAM_INVITE - Team invitation
- SUBSCRIPTION_ALERT - Subscription alert

### NotificationStatus
- UNREAD - Not yet read
- READ - Has been read
- ARCHIVED - Archived

### InvoiceStatus
- PENDING - Awaiting payment
- PAID - Payment received
- OVERDUE - Past due date
- CANCELLED - Cancelled invoice
- REFUNDED - Refunded invoice

---

## Validation Rules Summary

### Users Table
- First/Last Name: 2-50 chars, letters only
- Email: Valid email format, unique
- Username: 3-30 chars, alphanumeric + underscore/dot/hyphen
- Password: 8+ chars, must contain digit, lowercase, uppercase, special char
- Phone Number: 10-15 digits
- Status: Must be valid enum value

### Organizations Table
- Name: 2-100 chars
- Email: Valid, unique
- Phone: 10-15 digits (optional)
- Website/Logo: Valid URLs (optional)
- Address fields: Max 255 chars
- Owner: Must reference valid user

### Teams Table
- Name: 2-100 chars
- Must be unique within organization
- Lead: Optional but must reference valid user
- Organization: Required reference

### Projects Table
- Name: 2-100 chars, unique within organization
- Status: Valid enum value
- Repository URL: Valid URL (optional)
- Visibility: Boolean value

### Tasks Table
- Title: 3-200 chars
- Description: Max 2000 chars
- Status/Priority: Valid enum values
- Progress: 0-100 percentage
- Project: Required reference

### Plans Table
- Name: 2-100 chars, unique
- Prices: Positive decimal values with 2 decimals
- Limits: Non-negative integers

### Subscriptions Table
- Status/Billing Cycle: Valid enum values
- Dates: Start required, others optional
- Price: Positive decimal
- Discount: Non-negative decimal

### Invoices Table
- Invoice Number: Unique, 1-50 chars
- Amounts: Positive decimals
- Status: Valid enum value
- Dates: Issue and due dates required

### Audit Logs Table
- Action: Valid enum value
- Entity Type: Required, 2-100 chars
- Details: Required, 1-2000 chars
- IP Address: Max 45 chars

### Notifications Table
- Type: Valid enum value
- Title: 3-200 chars
- Message: 3-1000 chars
- Status: Valid enum value

---

## Future Extensions

The schema is designed to support:

1. **Advanced Features**
   - File management and storage tracking
   - Comments and discussions on tasks
   - Time tracking and activity logs
   - Integration webhooks

2. **Security Enhancements**
   - Two-factor authentication settings
   - API keys and tokens table
   - Device and session management
   - IP whitelist configurations

3. **Analytics & Reporting**
   - Project metrics and statistics
   - Team performance tracking
   - User activity analytics
   - Financial reporting

4. **Advanced Workflows**
   - Custom fields and metadata
   - Workflow automation
   - Integration connectors
   - Template management

---

## Performance Optimization

### Indexes
- Primary keys (auto-indexed)
- Foreign keys (auto-indexed)
- audit_logs: user_id, organization_id, created_at
- notifications: user_id, status
- Composite indexes on UNIQUE constraints

### Query Optimization
- Lazy loading for relationships (prevent N+1)
- Query pagination for list endpoints
- Connection pooling configuration
- Database-level indexing strategy

### Caching Strategy
- Cache frequently accessed roles/permissions
- Cache plan information
- Short-lived cache for user data
- Invalidate cache on updates

---

## Configuration

### Database Connection
```properties
# Database Configuration
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/devcast_saas}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
spring.jpa.properties.hibernate.jdbc.batch_size=10
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

---

**Last Updated:** January 2, 2026  
**Version:** 2.0  
**Total Tables:** 16 (Core: 10, RBAC: 2, Billing: 4)  
**Framework:** Spring Boot + Hibernate JPA
