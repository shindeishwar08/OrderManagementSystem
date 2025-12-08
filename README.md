# Hyperlocal Delivery - Order Management System (OMS)

A robust, backend-focused Order Management System built with **Spring Boot 3**.
This project demonstrates production-grade architecture, including stateless JWT authentication, Role-Based Access Control (RBAC), and complex state management for delivery orders.

---

## 🏗️ Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3.5.7
* **Database:** PostgreSQL
* **Security:** Spring Security 6 + JWT (Stateless)
* **Build Tool:** Maven

---

## ⚡️ Features

### ✅ Authentication & Security (Phase 1)
* **JWT Auth:** Stateless authentication using `jjwt`.
* **RBAC:** Role-Based Access Control (`CUSTOMER`, `PARTNER`, `ADMIN`).
* **Secure Passwords:** BCrypt hashing implementation.
* **Validation:** Strict input validation using Java Bean Validation (`@Valid`).

### 📦 Order Management (Phase 2)
* **Order Lifecycle:** Full state machine (`CREATED` → `ASSIGNED` → `ACCEPTED` → `PICKED` → `DELIVERED`).
* **Customer Flow:** Create orders, view history, and cancel active orders.
* **Error Handling:** Global Exception Handling with standardized JSON error responses.

### 🚚 Smart Logistics Engine (Phase 3)
* **Auto-Assignment:** "Matchmaker" algorithm automatically assigns orders to the least busy available partner.
* **Partner Dashboard:** Partners can view assigned jobs and manage their availability.
* **Delivery Flow:** Strict validation for delivery status updates (Accept → Pick → Deliver).

### 👑 Admin Dashboard (Phase 4)
* **Search Engine:** Dynamic filtering by Status, Partner, and Date with Pagination.
* **God Mode:** Manual override to force-assign stuck orders to specific partners.
* **Live Roster:** View all partners, their online status, and current workload.
* **Analytics:** Real-time stats on total orders, active deliveries, and revenue.

---

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
| `PUT` | `/admin/orders/assign/{id}` | Force-assign order to a partner | `ADMIN` |
| `GET` | `/admin/partners` | View Partner Roster & Load | `ADMIN` |
| `GET` | `/admin/stats` | View Analytics (Revenue, Counts) | `ADMIN` |

---

## 🛠️ Setup & Running

1.  **Clone the repo**
    ```bash
    git clone https://github.com/shindeishwar08/OrderManagementSystem.git
    ```

2.  **Configure Database**
    * Ensure PostgreSQL is running on port `5432`.
    * Create database: `omsdb`.
    * Update `src/main/resources/application.properties` with your credentials.

3.  **Run the App**
    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Test with Postman**
    * Hit `POST /auth/register` to create users.
    * Hit `POST /auth/login` to get Bearer Tokens.
    * Use tokens in the `Authorization` header for protected routes.

---

## 📈 Roadmap (Completed)

* [x] **Phase 1:** Foundation, Auth, Security Configuration.
* [x] **Phase 2:** Order Lifecycle (Create, Cancel, List).
* [x] **Phase 3:** Delivery Partner Flow & Auto-Assignment Logic.
* [x] **Phase 4:** Admin Dashboard & Analytics.