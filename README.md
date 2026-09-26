<div align="center">

# 🧺 Vividela Backend

### Hexagonal Edition

**Laundry stock management API — Hexagonal Architecture · Spring Boot 3 · Java 21**

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-Gemini-6DB33F?style=flat-square&logo=googlegemini&logoColor=white)](https://spring.io/projects/spring-ai)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/Auth-JWT%20-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)](https://github.com/jwtk/jjwt)
[![Lombok](https://img.shields.io/badge/Lombok-Enabled-EC1C24?style=flat-square&logo=lombok&logoColor=white)]()
[![JUnit5](https://img.shields.io/badge/JUnit-5-25A162?style=flat-square&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/Mockito-Unit%20Tests-78C257?style=flat-square)]()
[![Testcontainers](https://img.shields.io/badge/Testcontainers-MySQL-2496ED?style=flat-square&logo=testcontainers&logoColor=white)](https://testcontainers.com/)
[![ArchUnit](https://img.shields.io/badge/ArchUnit-Boundaries%20Checked-4B275F?style=flat-square)](https://www.archunit.org/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blueviolet?style=flat-square)]()
[![OpenPDF](https://img.shields.io/badge/OpenPDF-Receipts-D32F2F?style=flat-square&logo=adobeacrobatreader&logoColor=white)]()
[![Twilio](https://img.shields.io/badge/Twilio-SMS-F22F46?style=flat-square&logo=twilio&logoColor=white)](https://www.twilio.com/)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square&logo=apachemaven&logoColor=white)]()
[![Code Style](https://img.shields.io/badge/Code%20Style-Clean%20Architecture-informational?style=flat-square)]()

[Overview](#-overview) · [Architecture](#-architecture) · [Getting Started](#-quick-start) · [API](#-api--endpoints) · [Security](#-security) · [Tests](#-tests)

</div>

---

## 📖 Overview

**Vividela Backend ("Simple Hexagonal" edition)** is the application foundation for the management system of a **laundry business based in Cameroon**. It provides **authentication and user management, product and stock management, orders, and payments**, following a strict **hexagonal architecture (Ports & Adapters)**.

> 💡 The database schema (`laundry.sql`) also contains tables for supporting capabilities such as tickets, notifications, loyalty, and customer reviews.

### Why this project is interesting

| ✨ | Strength | Description |
|---|---|---|
| 🏛️ | **Pure Hexagonal Architecture** | Isolated domain, ports/adapters, inverted dependencies, verified by **ArchUnit** |
| 🔐 | **Complete JWT Authentication** | Login, register, logout with token blacklist and automatic cleanup |
| 📦 | **FEFO Stock Allocation** | *(First-Expired, First-Out)* — dedicated business policy to consume batches that expire first |
| 🧪 | **Comprehensive Test Coverage** | Domain, application services, adapters (unit + integration with Testcontainers) |
| 🐳 | **Docker Ready** | Multi-stage Java image and API, MySQL, and Adminer orchestrated via Docker Compose |
| 🤖 | **Spring AI (Gemini)** | Ready-to-use component for customer review sentiment analysis |
| 🛡️ | **Role-based Access Control** | 4 roles (`ADMIN`, `MANAGER`, `EMPLOYEE`, `CUSTOMER`) enforced via `@PreAuthorize` |
| 🧾 | **PDF Generation** | OpenPDF ready for tickets and receipts |

---

## 🏗️ Architecture

The project strictly follows the **Ports & Adapters (Hexagonal Architecture)** pattern popularized by Alistair Cockburn: the **business domain** depends on nothing, and everything else (web, persistence, security) depends on it through **ports** (interfaces).

```
┌─────────────────────────────────────────────────────────────────────┐
│                          ADAPTER (Input)                            │
│   REST Controllers · DTOs · JWT Security · Exception Handling      │
└───────────────────────────────┬───────────────────────────────────────┘
                                 │ implements
┌───────────────────────────────▼───────────────────────────────────────┐
│                          APPLICATION                                 │
│  Ports (Input / Output) · Use Cases · Services · Business Policies  │
│                 (StockAllocationPolicy, ProductService...)           │
└───────────────────────────────┬───────────────────────────────────────┘
                                 │ orchestrates
┌───────────────────────────────▼───────────────────────────────────────┐
│                             DOMAIN                                    │
│     Pure entities: User, Product, Stock, StockMovement...           │
│          Zero framework dependency — isolated business logic         │
└───────────────────────────────┬───────────────────────────────────────┘
                                 │ implemented by
┌───────────────────────────────▼───────────────────────────────────────┐
│                         ADAPTER (Output)                             │
│   JPA/MySQL Persistence · BCrypt · JWT (JJWT) · Token blacklist     │
└─────────────────────────────────────────────────────────────────────┘
```

### Project tree

```text
src/
├── main/
│   ├── java/ken/vivid/
│   │   ├── VividelaApplication.java
│   │   ├── adapter/
│   │   │   ├── exception/
│   │   │   ├── input/
│   │   │   │   └── web/
│   │   │   │       ├── dto/
│   │   │   │       ├── payloads/
│   │   │   │       └── security/
│   │   │   │       └── output/
│   │   │   │           ├── persistence/
│   │   │   │           │   ├── adapter/
│   │   │   │           │   ├── jpaEntities/
│   │   │   │           │   └── jpaRepositories/
│   │   │   │           ├── security/
│   │   │   │           └── token/
│   │   ├── application/
│   │   │   ├── port/
│   │   │   │   ├── input/
│   │   │   │   └── output/
│   │   │   └── service/
│   │   ├── config/
│   │   └── domain/
│   │       ├── dto/
│   │       └── entities/
│   └── resources/
│       ├── db/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-prod.yml
│       ├── laundry.sql
│       └── queries.sql
│
└── test/java/ken/vivid/
    ├── adapter/
    ├── application/
    ├── domain/
    └── support/
```

> These boundaries are not just a convention: they are **automatically verified** by an **ArchUnit** test that fails in CI if the domain starts depending on Spring, JPA, or the web layer.

---

## 🛠️ Technical stack

| 🧩 | Category | Technologies |
|---|---|---|
| ☕ | **Language / Runtime** | Java 21 |
| 🌱 | **Framework** | Spring Boot 3.5.10 (Web, Security, Data JPA, Validation, Mail) |
| 🗄️ | **Database** | MySQL 8.0 (via Docker) + HikariCP |
| 🔐 | **Security** | Spring Security · JWT (JWT 0.12.6, HS256) · BCrypt |
| 🤖 | **AI** | Spring AI 1.1.6 — Google GenAI (Gemini) for sentiment analysis |
| 🧾 | **Document Generation** | OpenPDF (tickets/receipts) |
| ✉️ | **Notifications** | Spring Mail (SMTP) · Twilio (SMS) |
| 🧬 | **Boilerplate** | Lombok |
| ⚙️ | **Configuration** | spring-dotenv (automatic `.env` loading) |
| 🧪 | **Tests** | JUnit 5 · Mockito · AssertJ · Testcontainers (MySQL) · ArchUnit |
| 📦 | **Build** | Maven |
| 🐳 | **Ops** | Multi-stage Docker image · Docker Compose (API + MySQL + Adminer) |

---

## 🚀 Quick start

### Prerequisites

- **Java 21+**
- **Maven 3.9+** (if running outside Docker)
- **Docker Engine and Docker Compose v2**

### 1. Clone the repository

```bash
git clone https://github.com/KENMOEmarc/Vividela-Hex.git
cd Vividela-Hex
```

### 2. Configure environment variables

Create a `.env` file at the project root. Docker Compose uses it for configuration; when running the application directly, `spring-dotenv` loads it for local development.

```dotenv
# --- Database ---
MYSQL_ROOT_PASSWORD=change-me
MYSQL_DATABASE=vividela
MYSQL_USER=vividela_user
MYSQL_PASSWORD=change-me
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3307/vividela
SPRING_DATASOURCE_USERNAME=vividela_user
SPRING_DATASOURCE_PASSWORD=change-me

# --- JWT ---
JWT_SECRET=replace-with-a-random-secret-of-at-least-32-characters
JWT_EXPIRATION=86400000
JWT_BLACKLIST_CLEANUP_FIXED_DELAY_MS=3600000

# --- Mail (optional in dev) ---
MAIL_HOST=
MAIL_PORT=587
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_FROM=contact@vividela.com

# --- Twilio SMS (optional) ---
TWILIO_ENABLED=false
TWILIO_ACCOUNT_SID=
TWILIO_AUTH_TOKEN=
TWILIO_FROM_NUMBER=

# --- Spring AI / Gemini (optional) ---
GEMINI_API_KEY=

# --- Server ---
SERVER_PORT=8080
```

> ⚠️ Never commit your `.env` file — it is already ignored by `.gitignore`.

### Docker image

The root `Dockerfile` uses Maven and Java 21 to package the application, then copies only the executable JAR into a Java 21 runtime image. The runtime runs as an unprivileged user. `.dockerignore` keeps local build output, IDE files, Git metadata, and `.env` files out of the build context. Unit tests are not compiled or run as part of the image build; run them separately with Maven.

To build the image without starting the database stack:

```bash
docker build -t vividela-backend:1.0.0 .
```

### 3. Run the complete application stack in Docker

```bash
docker compose up --build -d
```

| 🧱 | Service | URL | Credentials |
|---|---|---|---|
| 🚀 | **API** | http://localhost:8080/api | JWT authentication |
| 🗄️ | **MySQL** | `localhost:3307` | defined in `.env` |
| 🖥️ | **Adminer** (database UI) | http://localhost:8081 | server `mysql` |

Compose waits until MySQL passes its health check before starting the API. To inspect logs or stop the stack:

```bash
docker compose logs -f app
docker compose down
```

To keep the MySQL data volume when stopping the stack, use `docker compose down` without `-v`.

### 4. Build and run locally with Maven (optional)

Start only the database services:

```bash
docker compose up -d mysql adminer
```

Then run the application from the project root. Maven is used directly because this repository does not include the Maven Wrapper:

```bash
mvn spring-boot:run
```

The API is available at `http://localhost:8080/api`. A quick registration check:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
        "firstName": "Ada",
        "lastName": "Lovelace",
        "userName": "ada",
        "email": "ada@vividela.com",
        "phone": "+237600000000",
        "password": "P@ssw0rd!"
      }'
```

---

## 📡 API — Endpoints

> Base path: `/api` · All responses are wrapped in a standard `ApiResponse<T>` (`success`, `message`, `data`).

### 🔑 Authentication — `/auth`

| 🎬 | Method | Endpoint | Access | Description |
|---|---|---|---|---|
| 📝 | `POST` | `/auth/register` | 🌐 Public | Register a client (`CUSTOMER` role) |
| 🔓 | `POST` | `/auth/login` | 🌐 Public | Log in and receive a JWT |
| 🧑‍💼 | `POST` | `/auth/store` | 🛡️ `ADMIN`, `MANAGER` | Create a staff account with an explicit role |
| 🚪 | `POST` | `/auth/logout` | 🔒 Authenticated | Invalidates the current token (blacklist) |

### 👤 Users — `/users`

| 🎬 | Method | Endpoint | Access | Description |
|---|---|---|---|---|
| 🙋 | `GET` | `/users/me` | 🔒 Authenticated | Current user profile |
| ✏️ | `PUT` | `/users/{id}` | 🔒 Authenticated | Update profile |
| 🔑 | `PUT` | `/users/{id}/password` | 🙅 Self-only | Change password |
| 🗑️ | `DELETE` | `/users/{id}` | 🛡️ `ADMIN` | Delete an account |

### 📦 Products — `/products`

| 🎬 | Method | Endpoint | Access | Description |
|---|---|---|---|---|
| 📋 | `GET` | `/products` | 🔒 Authenticated | List all products |
| 🔍 | `GET` | `/products/{id}` | 🔒 Authenticated | Product details |
| ➕ | `POST` | `/products` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Create a product |
| ✏️ | `PUT` | `/products/{id}` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Update a product |
| 🗑️ | `DELETE` | `/products/{id}` | 🛡️ `ADMIN` | Delete a product |

### 📊 Stock — `/stock`

| 🎬 | Method | Endpoint | Access | Description |
|---|---|---|---|---|
| ⚠️ | `GET` | `/stock/low` | 🔒 Authenticated | Products below the alert threshold |
| 📈 | `GET` | `/stock/product/{productId}` | 🔒 Authenticated | Current stock for a product |
| 🗂️ | `GET` | `/stock/product/{productId}/batches` | 🔒 Authenticated | All batches for a product, sorted by expiration |
| 📥 | `POST` | `/stock/batches` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Register a new batch |
| 🛠️ | `PUT` | `/stock/batches/{batchId}` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Manual adjustment of a batch |
| 📤 | `POST` | `/stock/consume` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Stock consumption (automatic **FEFO** allocation) |

### 🧾 Orders — `/orders`

| 🎬 | Method | Endpoint | Access | Description |
|---|---|---|---|---|
| 📋 | `GET` | `/orders` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | List orders |
| 🔍 | `GET` | `/orders/{id}` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Order details |
| 👤 | `GET` | `/orders/client/{clientId}` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Orders for a client |
| ➕ | `POST` | `/orders` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Create an order |
| ✏️ | `PUT` | `/orders/{id}` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Update order details and status |
| 🗑️ | `DELETE` | `/orders/{id}` | 🛡️ `ADMIN` | Delete an order |

### 💳 Payments — `/payments`

| 🎬 | Method | Endpoint | Access | Description |
|---|---|---|---|---|
| ➕ | `POST` | `/payments` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Record a pending payment |
| 🔍 | `GET` | `/payments/{id}` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Payment details |
| 📋 | `GET` | `/payments/order/{orderId}` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Payments for an order |
| ✅ | `PUT` | `/payments/{id}/confirm` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Confirm a pending payment |
| ❌ | `PUT` | `/payments/{id}/fail` | 🛡️ `ADMIN`, `MANAGER`, `EMPLOYEE` | Mark a pending payment as failed |

Recording a payment requires `orderId`, `paymentMethod` (`CASH`, `CHECK`, or `MOBILE_PAYMENT`), and a positive `amount`. Optional fields are `payerPhone` and `transactionReference`.

---

## 🔐 Security

- **Stateless JWT** (HS256) — no server-side session.
- **Token blacklist** persisted in the database: a revoked token (`/auth/logout`) is immediately invalidated, even if it has not yet expired.
- **Automatic cleanup** (`RevokedTokenCleanupJob`): revoked tokens are removed once their natural lifetime has elapsed.
- **Role-based access control** at the route level (`SecurityConfig`) and method level (`@PreAuthorize`), across 4 roles: `ADMIN`, `MANAGER`, `EMPLOYEE`, `CUSTOMER`.
- **Passwords** hashed with **BCrypt** (strength 10).
- **CORS** configured through a dedicated `CorsConfigurationSource`.

---

## 🧪 Tests

The project applies a strict testing policy aligned with the hexagonal boundaries:

```bash
# Run the full test suite
mvn test

# Only unit tests (domain + application)
mvn test -Dtest="ken.vivid.domain.**,ken.vivid.application.**"
```

| 🧪 | Tested layer | Test type | Tools |
|---|---|---|---|
| 🎯 | `domain/entities` | Pure unit | JUnit 5, AssertJ |
| ⚙️ | `application/service` | Unit with mocks | JUnit 5, Mockito |
| 🗄️ | `adapter/input/web/output/persistence` | Integration | Testcontainers (real MySQL) |
| 🔐 | `adapter/input/web/output/security` & `token` | Unit | JUnit 5 |
| 🏛️ | Hexagonal boundaries | Structural | **ArchUnit** |

---

## 🗺️ Roadmap

The core API currently includes **authentication, products, stock, orders, and payments**. Supporting capabilities that can be expanded include:

- [ ] **Drop-off tickets** (with barcodes and expiration tracking)
- [ ] **Customer notifications** (email/SMS via Twilio) with retry and escalation
- [ ] **Loyalty program** (points, thresholds, caps)
- [ ] **Customer review sentiment analysis** via Gemini (Spring AI)
- [ ] **PDF receipts** (OpenPDF is already included as a dependency)

---

## 👥 Application roles

| 🛡️ | Role | Scope |
|---|---|---|
| 👑 | `ADMIN` | Full access, including deletion of accounts/products |
| 🧑‍💼 | `MANAGER` | Operational management: products, stock, creation of staff accounts |
| 🧑‍🔧 | `EMPLOYEE` | Day-to-day operations: products, stock |
| 🙋 | `CUSTOMER` | Standard customer account (self-registration) |

---

<div align="center">

Built with ☕ and uncompromising hexagonal discipline.

**Vividela** — *Laundry management, simplified.*

</div>