# Hyperlocal Delivery - Order Management System (OMS)

A robust, backend-focused Order Management System built with **Spring Boot 3**.
This project demonstrates production-grade architecture, including stateless JWT authentication, Role-Based Access Control (RBAC), Geospatial Tracking (Redis), and complex state management for delivery orders.

-----

## 🏗️ Tech Stack

  * **Language:** Java 17
  * **Framework:** Spring Boot 3.5.7
  * **Database:** PostgreSQL (Primary), Redis (Geospatial & Caching)
  * **Security:** Spring Security 6 + JWT (Stateless), Bucket4j (Rate Limiting)
  * **Infrastructure:** Docker & Docker Compose
  * **Build Tool:** Maven

-----

## ⚡️ Features

### ✅ Authentication & Security

  * **JWT Auth:** Stateless authentication using `jjwt`.
  * **RBAC:** Role-Based Access Control (`CUSTOMER`, `PARTNER`, `ADMIN`).
  * **Secure Passwords:** BCrypt hashing implementation.
  * **Validation:** Strict input validation using Java Bean Validation (`@Valid`).

### 🛡️ System Resilience & Protection

  * **Rate Limiting (Bucket4j):** Implemented "Token Bucket" algorithm to protect against abuse.
      * **Login Protection:** Limits login attempts based on IP address (5 attempts/min).
      * **Order Protection:** Limits order creation per User ID (1 order/10sec).
      * **Handling:** Returns `429 Too Many Requests` when limits are exceeded.
  * **Concurrency Control:**
      * **Pessimistic Locking:** Uses `PESSIMISTIC_WRITE` locks on the Order repository to prevent race conditions (e.g., stopping multiple partners from accepting the same order simultaneously).
      * **Transactional Integrity:** Full `@Transactional` coverage across critical services (`assignOrder`, `createOrder`) ensures ACID compliance and prevents "Zombie" data during failures.

### 📦 Order Management

  * **Order Lifecycle:** Full state machine (`CREATED` → `ASSIGNED` → `ACCEPTED` → `PICKED` → `DELIVERED`).
  * **Customer Flow:** Create orders, view history, and cancel active orders.
  * **Error Handling:** Global Exception Handling with standardized JSON error responses.

### 🚚 Smart Logistics Engine

  * **Geospatial Assignment:** Uses **Redis GEOSEARCH** to find partners within a 3km radius of the pickup point.
  * **Smart Filtering:** "Funnel" logic filters for nearby drivers -> checks database availability -> selects the least busy driver.
  * **Resilience:** Circuit-breaker logic falls back to Database Search if the Redis tracking engine fails.
  * **Ghost Partner Handling:** Safely ignores stale tracking data to prevent system crashes.

### 👁️ Visibility & Tracking

  * **Real-Time Tracking:** Customers can track their active orders (`ACCEPTED` -> `PICKED`) with strict privacy controls.
  * **Admin "God Mode":** Operations teams can verify the live location of any partner (Online or Offline).
  * **Security:** Ownership validation ensures customers can only track their own orders.

### 👑 Admin Dashboard

  * **Search Engine:** Dynamic filtering by Status, Partner, and Date with Pagination.
  * **God Mode:** Manual override to force-assign stuck orders to specific partners.
  * **Live Roster:** View all partners, their online status, and current workload.
  * **Analytics:** Real-time stats on total orders, active deliveries, and revenue.

-----

## 🔌 API Endpoints

### 🔐 Authentication

| Method | Endpoint          | Description                          | Access      |
| :----- | :---------------- | :----------------------------------- | :---------- |
| `POST` | `/auth/register`  | Register a new user (Customer/Admin) | Public      |
| `POST` | `/auth/login`     | Login and receive JWT Token          | Public      |
| `GET`  | `/user/me`        | Get current user profile             | Authenticated |

### 🛒 Customer

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/orders` | Create a new delivery order | `CUSTOMER` |
| `GET` | `/orders/my-orders` | List all my past orders | `CUSTOMER` |
| `PUT` | `/orders/cancel/{id}` | Cancel an order (if not yet picked) | `CUSTOMER` |
| `GET` | `/orders/{id}/track` | Track driver location | `CUSTOMER` |

### 🚚 Delivery Partner

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `PUT` | `/partner/status` | Toggle availability (true/false) | `PARTNER` |
| `GET` | `/partner/orders` | View assigned/active orders | `PARTNER` |
| `PUT` | `/partner/orders/accept/{id}` | Accept an assigned order | `PARTNER` |
| `PUT` | `/partner/orders/decline/{id}` | Decline an order (Re-triggers assignment) | `PARTNER` |
| `PUT` | `/partner/orders/update/{id}` | Update status (PICKED, DELIVERED) | `PARTNER` |

### 👮‍♂️ Admin

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/admin/orders` | Search all orders (Filter + Pagination) | `ADMIN` |
| `GET` | `/admin/partner/{id}` | Track any partner (God Mode) | `ADMIN` |
| `PUT` | `/admin/orders/assign/{id}` | Force-assign order to a partner | `ADMIN` |
| `GET` | `/admin/partners` | View Partner Roster & Load | `ADMIN` |
| `GET` | `/admin/stats` | View Analytics (Revenue, Counts) | `ADMIN` |

-----

## 🛠️ Setup & Running

1.  **Clone the repo**

    ```bash
    git clone https://github.com/shindeishwar08/OrderManagementSystem.git
    ```

2.  **Start Infrastructure (Docker)**

    ```bash
    docker-compose up -d
    # Starts PostgreSQL (5432) and Redis (6379)
    ```

3.  **Run the App**

    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Test with Postman**

      * Use the provided collection to simulate the full flow.
      * Use `PUT /partner/location` (via Simulation Script) to update coordinates in Redis.