# AnimeVerse

AnimeVerse is a robust, full-stack social media platform for anime fans, architected with a modern microservices approach. It features a high-performance React frontend and a distributed backend composed of multiple Spring Boot services, all orchestrated via Docker Compose for local development and scalable deployment.

## Table of Contents

- [Key Features](#key-features)
- [System Architecture](#system-architecture)
- [Frontend Structure](#frontend-structure)
- [Backend Microservices](#backend-microservices)
- [Infrastructure & DevOps](#infrastructure--devops)
- [Development Workflow](#development-workflow)
- [License](#license)

## Key Features

- Secure JWT-based authentication and session management
- Rich user profiles, friend and group management
- Anime content creation, sharing, and discovery
- Real-time notifications (Kafka-based)
- Full-text search (Elasticsearch)
- Personalized recommendations (collaborative/content-based)
- Responsive, accessible UI with Tailwind CSS

## System Architecture

- **Frontend:** React 19, Vite, Tailwind CSS, Framer Motion, React Router, Radix UI
- **Backend:** Java 21/25, Spring Boot 4.x, microservices
- **Datastores:** PostgreSQL (persistent), Redis (cache/session), Elasticsearch (search)
- **Messaging:** Apache Kafka (event-driven communication)
- **API Gateway:** Spring Cloud Gateway (centralized routing, JWT validation)
- **Containerization:** Docker Compose (local orchestration)
- **DevOps:** AWS CDK (infrastructure as code, see `infrastructure_code/`)

## Frontend Structure

Located in `anime-social-media-platform/`:

- **Entry Point:** `src/main.jsx`, `src/App.jsx`
- **Components:** Modular, reusable UI in `src/components/`
- **Pages:** Route-based views in `src/pages/`
- **State Management:** React Context, hooks
- **API Layer:** `src/lib/apiClient.js` (Axios-based, JWT support)
- **Styling:** Tailwind CSS, custom themes in `src/theme/`
- **Assets:** Static files in `src/assets/`

### Frontend Scripts

- `npm run dev` — Launches Vite dev server with HMR
- `npm run build` — Production build
- `npm run lint` — Linting with ESLint
- `npm run analyze` — Bundle analysis

## Backend Microservices

Located in `backend/`, each service is independently deployable and follows 12-factor principles. All services use Spring Boot 4.x and are containerized.

### 1. Api-Gateway
- **Purpose:** Central entrypoint, request routing, JWT validation
- **Tech:** Spring Cloud Gateway, Java 25
- **Key Dependencies:** `spring-cloud-starter-gateway-server-webflux`, `java-jwt`
- **Config:** Forwards requests to internal services, enforces security

### 2. auth-service
- **Purpose:** User authentication, registration, token issuance
- **Tech:** Spring Security, JWT, PostgreSQL, Redis, Kafka
- **Key Dependencies:** `spring-boot-starter-security`, `jjwt`, `spring-boot-starter-data-jpa`, `spring-boot-starter-kafka`, `springdoc-openapi`
- **Features:** Password hashing, JWT refresh, Redis for session/token blacklisting, Kafka for auth events

### 3. user-service
- **Purpose:** User profile, friends, groups, and social graph
- **Tech:** gRPC, PostgreSQL, Redis, Kafka
- **Key Dependencies:** `spring-boot-starter-data-jpa`, `spring-grpc-server-web-spring-boot-starter`, `grpc-services`, `protobuf-java`, `spring-boot-starter-kafka`
- **Features:** gRPC APIs for internal communication, REST for external, Redis for caching, Kafka for user events

### 4. content-service
- **Purpose:** Anime content CRUD, tagging, user interactions
- **Tech:** gRPC, PostgreSQL, Redis, Kafka
- **Key Dependencies:** `spring-boot-starter-data-jpa`, `spring-grpc-server-web-spring-boot-starter`, `grpc-services`, `spring-boot-starter-kafka`, `springdoc-openapi`
- **Features:** Content versioning, tag/genre management, Redis for trending content, Kafka for content events

### 5. notification-service
- **Purpose:** Real-time and email notifications
- **Tech:** Kafka, Spring Mail
- **Key Dependencies:** `spring-boot-starter-kafka`, `spring-boot-starter-mail`, `springdoc-openapi`
- **Features:** Consumes Kafka events, sends emails (SMTP), supports in-app notifications

### 6. search-service
- **Purpose:** Full-text search for users, content, tags
- **Tech:** Elasticsearch, Kafka
- **Key Dependencies:** `spring-boot-starter-data-elasticsearch`, `spring-boot-starter-kafka`, `springdoc-openapi`
- **Features:** Indexes content and user data, real-time updates via Kafka, advanced search queries

### 7. recommendation-service
- **Purpose:** Personalized anime/content recommendations
- **Tech:** PostgreSQL, Kafka
- **Key Dependencies:** `spring-boot-starter-data-jpa`, `spring-boot-starter-kafka`, `springdoc-openapi`, Jackson
- **Features:** Collaborative and content-based filtering, batch and real-time updates

### 8. infrastructure_code
- **Purpose:** Infrastructure as Code (AWS CDK, Java)
- **Key Dependencies:** `aws-cdk-lib`, `aws-java-sdk`
- **Features:** Provisions AWS resources for production (VPC, RDS, MSK, etc.)

## Infrastructure & DevOps

- **Docker Compose:** Orchestrates all services, databases, and supporting systems for local development
- **Databases:** PostgreSQL for persistent storage, Redis for caching/session, Elasticsearch for search
- **Kafka:** Central event bus for microservice communication
- **Adminer:** Web UI for database management
- **Environment Variables:** All secrets/configs are injected via Docker Compose

## Development Workflow

### Prerequisites
- Node.js & npm (frontend)
- Java 21+ or 25 (backend)
- Docker & Docker Compose

### Local Setup
1. Clone the repository
2. Start backend and infrastructure:
   ```sh
   cd backend
   docker compose up --build
   ```
3. Start frontend:
   ```sh
   cd anime-social-media-platform
   npm install
   npm run dev
   ```

### Testing & Linting
- Backend: Use Maven/Gradle for unit/integration tests
- Frontend: `npm run lint` and React Testing Library (add tests in `src/__tests__/`)

## License

See [LICENSE](./LICENSE).

### Common Tech Stack

- Spring Boot 4.x
- Kafka for messaging
- PostgreSQL for persistent storage
- Redis for caching
- gRPC for inter-service communication (where applicable)
- OpenAPI for API documentation

## Infrastructure

- **Docker Compose:** Orchestrates all services, databases, and supporting infrastructure
- **Elasticsearch:** Used by search-service
- **Adminer:** Database management UI

## Development

### Prerequisites

- Node.js & npm (for frontend)
- Java 21+ (for backend)
- Docker & Docker Compose

### Running Locally

1. Clone the repository
2. Start backend services:
   ```sh
   cd backend
   docker compose up --build
   ```
3. Start frontend:
   ```sh
   cd anime-social-media-platform
   npm install
   npm run dev
   ```

## License

See [LICENSE](../LICENSE).
