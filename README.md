# 📚 API Sistema Biblioteca

A robust, production-ready RESTful API developed with **Java 21** and **Spring Boot 4.1.0**, serving as the backend core for the Library Management System.

The application implements JWT-based authentication, database persistence with PostgreSQL and Spring Data JPA, comprehensive input validation with custom exception handlers, and pagination for high-performance resource querying.

---

## 🛠️ Tech Stack & Architecture

* **Language & Framework:** Java 21, Spring Boot 4.1.0
* **Database & Persistence:** PostgreSQL, Spring Data JPA / Hibernate
* **Security:** Spring Security & JWT (JSON Web Tokens)
* **Validation & Error Handling:** Jakarta Bean Validation & Centralized `@RestControllerAdvice`
* **Build Tool:** Gradle
* **Testing:** JUnit 5, Mockito

### 📂 Project Structure

The project adheres to a clean layered architecture separating API controllers, business logic, data persistence, and security configuration:

```text
src/main/java/com/biblioteca/api_sistema_biblioteca/
├── config/                  # Global configurations & exception handlers
│   └── GlobalExceptionHandler.java
│   └── SecurityConfig.java
├── controller/              # REST Endpoints (Controllers)
│   ├── AuthController.java
│   ├── BookController.java
│   └── UserController.java
├── model/ / entity/         # JPA Entities & Database Mappings
│   ├── Book.java
│   └── User.java
├── dto/                     # Request & Response Data Transfer Objects
├── repository/              # Spring Data JPA Repositories
├── service/                 # Business Logic & Service Interfaces
└── security/                # JWT Utilities & Filters
```

---

## 🔥 Key Backend Engineering Highlights

### 🛡️ Centralized Exception & Validation Handling
* Implemented a custom `GlobalExceptionHandler` using `@RestControllerAdvice` to process Jakarta Validation failures (`MethodArgumentNotValidException` and `HandlerMethodValidationException`).
* Structured responses to prevent message overriding by mapping error parameters using `Map<String, List<String>>` for multi-rule validation reporting.

### 🔐 JWT Authentication & Authorization
* Stateless security workflow powered by Spring Security filters.
* Secured REST endpoints requiring JWT Bearer token validation for book management and user profile modifications.

### ⚡ Optimized Querying & Pagination
* Spring Data JPA pagination support (`Pageable`, `Page<T>`) for book listings to reduce server memory overhead during large queries.
* Real-time search filtering capability integrated into paginated repository methods.

---

## 🚀 Getting Started Locally

### Prerequisites
* **Java Development Kit (JDK)**: `21` or higher
* **Gradle**: Gradle Wrapper included (`./gradlew`)
* **PostgreSQL Database**: Running instance on port `5432`

### Database Configuration

Ensure PostgreSQL is running, then create the database:

```sql
CREATE DATABASE bibliotecadb;
```

Configure your environment variables or update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sistema_bibliotecadb
spring.datasource.username=YOUR_POSTGRES_USER
spring.datasource.password=YOUR_POSTGRES_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Secret Key Configuration
jwt.secret=YOUR_SUPER_SECRET_KEY_AT_LEAST_256_BITS_LONG
```

### Installation & Execution

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/fernandogcabal/api-sistema-biblioteca.git](https://github.com/fernandogcabal/api-sistema-biblioteca.git)
   cd api-sistema-biblioteca
   ```

2. **Build the project:**
   ```bash
   ./gradlew build
   ```

3. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

The API will start at `http://localhost:8080`.

---

## 🧪 Testing

To execute unit and integration tests:

```bash
./gradlew test
```

---

## 🔗 Related Frontend Project

This REST API serves as the backend service for the Angular single-page application:
* 🐙 **Frontend Repository:** [UI-SISTEMA-BIBLIOTECA](https://github.com/fernandogcabal/UI-SISTEMA-BIBLIOTECA)