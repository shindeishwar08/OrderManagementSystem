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

## ⚡️ Features (Phase 1 Complete)

### ✅ Authentication & Security
* **JWT Auth:** Stateless authentication using `jjwt`.
* **RBAC:** Role-Based Access Control (`CUSTOMER`, `PARTNER`, `ADMIN`).
* **Secure Passwords:** BCrypt hashing implementation.
* **Validation:** Strict input validation using Java Bean Validation (`@Valid`).

### 🔌 API Endpoints (Current)

| Method | Endpoint          | Description                          | Access      |
| :----- | :---------------- | :----------------------------------- | :---------- |
| `POST` | `/auth/register`  | Register a new user (Customer/Admin) | Public      |
| `POST` | `/auth/login`     | Login and receive JWT Token          | Public      |
| `GET`  | `/user/me`        | Get current user profile             | Authenticated |

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
    * Import the collection (optional).
    * Hit `POST /auth/register` to create a user.
    * Hit `POST /auth/login` to get a Bearer Token.

---

## 📈 Roadmap

* [x] **Phase 1:** Foundation, Auth, Security Configuration.
* [ ] **Phase 2:** Order Lifecycle (Create, Cancel, List).
* [ ] **Phase 3:** Delivery Partner Flow (Accept, Pick, Deliver).
* [ ] **Phase 4:** Admin Dashboard & Analytics.