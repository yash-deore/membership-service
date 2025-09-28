# FirstClub Membership Service

A comprehensive membership management service built with Spring Boot that demonstrates enterprise-level design patterns, concurrency handling, and extensible architecture.

## 🚀 Features

### Core Functionality

-   **Membership Plans**: Monthly, Quarterly, and Yearly subscription options
-   **Tiered Memberships**: Silver, Gold, and Platinum tiers with configurable benefits
-   **Dynamic Tier Eligibility**: Rule-based tier qualification system
-   **Subscription Management**: Subscribe, upgrade, downgrade, and cancel operations
-   **Order Tracking**: Track member orders for tier eligibility calculations
-   **Benefit System**: Configurable benefits per tier (discounts, free delivery, etc.)


## 🔐 Tier Eligibility Rules

### Silver Tier

-   No requirements (default tier)
-   Benefits: 5% discount, free delivery on orders > $50

### Gold Tier

-   Minimum 5 total orders
-   Minimum $500 total order value
-   Benefits: 10% discount, free delivery on all orders, early access

### Platinum Tier

-   Minimum 10 orders in last 30 days
-   Minimum $1000 order value in last 30 days
-   Benefits: 15% discount, priority free delivery, exclusive deals, priority support


## 📋 Prerequisites

-   Java 17 or higher
-   Maven 3.6+
-   IDE with Lombok support (IntelliJ IDEA recommended)

## 🛠️ Setup & Installation

1. **Clone the repository**

    ```bash
    cd FirstClub
    ```

2. **Build the project**

    ```bash
    mvn clean install
    ```

3. **Run the application**
    ```bash
    mvn spring-boot:run
    ```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

-   Swagger UI: http://localhost:8080/swagger-ui.html
-   OpenAPI JSON: http://localhost:8080/api-docs

## 🔥 Quick Start Guide

### 1. Create a Member

```bash
curl -X POST http://localhost:8080/api/membership/members \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "cohort": "premium"
  }'
```

### 2. View Available Plans

```bash
curl http://localhost:8080/api/membership/plans
```

### 3. View Available Tiers

```bash
curl http://localhost:8080/api/membership/tiers
```

### 4. Subscribe to a Membership

```bash
curl -X POST http://localhost:8080/api/membership/subscribe \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "planType": "MONTHLY",
    "tierType": "SILVER",
    "autoRenew": true
  }'
```

### 5. Record Orders (for tier eligibility)

```bash
curl -X POST "http://localhost:8080/api/membership/orders/user123?orderValue=150.00"
```

### 6. Check Tier Eligibility

```bash
curl http://localhost:8080/api/membership/eligibility/user123
```

### 7. Upgrade Tier

```bash
curl -X PUT http://localhost:8080/api/membership/upgrade \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "targetTierType": "GOLD"
  }'
```

### 8. Get Member Status

```bash
curl http://localhost:8080/api/membership/status/user123
```

## 🔧 Configuration

Key configuration properties in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database (H2 In-Memory)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Thread Pool
spring.task.execution.pool.core-size=10
spring.task.execution.pool.max-size=20
```

## 📊 Database Schema

Access H2 console at http://localhost:8080/h2-console

-   JDBC URL: `jdbc:h2:mem:membershipdb`
-   Username: `sa`
-   Password: (empty)
