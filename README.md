# Pennywise Backend API

> **Personal Expense Management & Financial Insights Engine**  
> RESTful API built with Java 21, Spring Boot 3.3.4, Spring Security, and MySQL 8.0.

---

## 📌 Architecture & Responsibilities

The Pennywise Backend provides a robust, stateless REST API responsible for:
- **Authentication & Security:** User registration, credential authentication, BCrypt password hashing, and stateless JWT token issuance and validation.
- **Transaction Ledger:** Full CRUD for expenses and income entries with user-specific data isolation.
- **Budget Monitoring:** Category budget allocation, monthly progress tracking, and overspending threshold computation.
- **Analytics & Forecasting:** Daily burn rate calculation, month-end expenditure estimates, statistical anomaly detection, and category distribution summaries.
- **Explainable Rule-Based AI Categorization:** Deterministic keyword-matching engine classifying transaction descriptions into standard expense categories with explainable rule tags.
- **Conversational Financial Query Engine:** Natural-language financial query processor analyzing live transaction metrics to answer user spending questions.

---

## 🛠️ Technology Stack

- **Java Version:** Java 21 LTS
- **Framework:** [Spring Boot 3.3.4](https://spring.io/projects/spring-boot)
  - `spring-boot-starter-web`: REST controllers and HTTP request handling
  - `spring-boot-starter-data-jpa`: Object-relational mapping with Hibernate 6.5
  - `spring-boot-starter-security`: Authentication filter chain and authorization rules
  - `spring-boot-starter-validation`: Jakarta bean validation for API payloads
- **JWT Library:** [JJWT 0.12.6](https://github.com/jwtk/jjwt) (Java JWT)
- **Database:** MySQL 8.0+ with HikariCP connection pooling
- **Build Tool:** Apache Maven with Maven Wrapper (`./mvnw`)
- **Testing:** JUnit 5, Spring Security Test, Mockito

---

## 🔐 API Endpoints & Authentication

All endpoints under `/api/auth/**` and `/api/health` are publicly accessible.  
All other endpoints require a valid JWT passed in the HTTP Authorization header:  
`Authorization: Bearer <your_jwt_token>`

### Authentication Endpoints (`/api/auth`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user account | No |
| `POST` | `/api/auth/login` | Authenticate with username/email and password | No |
| `POST` | `/api/auth/demo` | Fast 1-click evaluation demo login (`demo` account) | No |
| `GET` | `/api/auth/me` | Fetch authenticated user profile | Yes (Bearer) |

### Transaction Endpoints (`/api/transactions`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/transactions` | List authenticated user transactions (supports `?type=&category=&search=`) | Yes (Bearer) |
| `GET` | `/api/transactions/{id}` | Retrieve specific transaction by ID (enforces ownership) | Yes (Bearer) |
| `POST` | `/api/transactions` | Record a new expense or income entry | Yes (Bearer) |
| `PUT` | `/api/transactions/{id}` | Update existing transaction details | Yes (Bearer) |
| `DELETE` | `/api/transactions/{id}` | Delete transaction record | Yes (Bearer) |

### Budget Endpoints (`/api/budgets`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/budgets/status` | Get monthly category budget progress (supports `?month=YYYY-MM`) | Yes (Bearer) |
| `POST` | `/api/budgets` | Create or update category spending limit | Yes (Bearer) |
| `DELETE` | `/api/budgets/{id}` | Remove budget limit | Yes (Bearer) |

### Analytics & AI Assistant Endpoints (`/api/analytics`, `/api/ai`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/analytics/dashboard` | Dashboard KPIs (Net Balance, Income, Expenses, Savings Rate) | Yes (Bearer) |
| `GET` | `/api/analytics/insights` | Spending estimate, burn rate, statistical anomalies, breakdown | Yes (Bearer) |
| `POST` | `/api/analytics/ai-query` | Natural language spending query processor | Yes (Bearer) |
| `POST` | `/api/ai/categorize` | Rule-based explainable auto-categorization | Yes (Bearer) |

---

## ⚙️ Environment Variables & Configuration

Pennywise uses Spring Profiles (`dev` and `prod`) to separate safe local development from strict production security.

| Variable | Description | Development Default | Production Requirement |
| :--- | :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` | Must be set to `prod` |
| `PORT` | HTTP server port | `8080` | Assigned by hosting platform (e.g. Render) |
| `DB_URL` | JDBC MySQL connection URL | `jdbc:mysql://localhost:3306/pennywise_db...` | Required in production (managed MySQL host) |
| `DB_USERNAME` | Database username | `root` | Required in production |
| `DB_PASSWORD` | Database password | `root` | Required in production |
| `JWT_SECRET` | 256-bit signing key (HMAC-SHA256) | Insecure dev fallback | **Mandatory** (minimum 32 chars/64 hex chars) |
| `CORS_ORIGINS` | Allowed frontend origins | `http://localhost:5173,http://localhost:3000` | Deployed frontend domain (e.g. `https://pennywise.onrender.com`) |

> **Security Note:** In `prod` profile, `JwtUtils` and Spring Boot validation will strictly abort startup if `JWT_SECRET` or `DB_PASSWORD` are missing or set to insecure development defaults.

---

## 💻 Local Setup & Development

### Prerequisites
- **Java Development Kit (JDK):** Version 21 LTS
- **MySQL Server:** Version 8.0 or higher
- **Git**

### 1. Database Setup
Ensure MySQL is running locally and create the database:
```sql
CREATE DATABASE IF NOT EXISTS pennywise_db CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
```
*(Hibernate automatically validates and initializes the schema tables on startup).*

### 2. Build and Run
Using the provided Maven Wrapper:

**On Linux/macOS:**
```bash
./mvnw clean spring-boot:run
```

**On Windows:**
```powershell
.\mvnw.cmd clean spring-boot:run
```

The server will start at `http://localhost:8080`.

---

## 🧪 Automated Testing

Execute the unit and integration test suite:
```bash
./mvnw test
```
The test suite covers:
- Rule-based keyword categorization across multiple categories.
- Fallback categorization for unknown descriptions.
- JWT token generation, username extraction, and signature validation.
- Rejection of tampered, empty, or malformed JWT tokens.
- Production profile security enforcement (verifies startup aborts if default dev keys are detected in `prod`).

---

## 📦 Production Packaging

To compile and package the standalone executable JAR:
```bash
./mvnw clean package
```
The resulting executable JAR will be generated at:
`target/pennywise-backend-1.0.0.jar`

Run the packaged JAR:
```bash
java -jar target/pennywise-backend-1.0.0.jar
```

---

## 🐳 Docker Deployment

A multi-stage `Dockerfile` is included for containerized environments:
```bash
docker build -t pennywise-backend .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev pennywise-backend
```

---

## 🌐 Deployment (Render Web Service)

1. Connect this repository (`pennywise-backend`) to **Render**.
2. Select **Web Service** with **Docker** environment (or Native Java 21 runtime).
3. Set the following Environment Variables in the Render dashboard:
   - `SPRING_PROFILES_ACTIVE`: `prod`
   - `PORT`: `8080` (or leave default `$PORT`)
   - `DB_URL`: `jdbc:mysql://<external-mysql-host>:<port>/<dbname>?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `DB_USERNAME`: `<your_db_username>`
   - `DB_PASSWORD`: `<your_db_password>`
   - `JWT_SECRET`: *(Generate with `openssl rand -hex 32`)*
   - `CORS_ORIGINS`: `https://<your-frontend-service-name>.onrender.com`
4. Set Health Check Path to: `/api/health`
