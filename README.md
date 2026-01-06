# RestẩuntSystem: High-Performance Distributed Microservices Platform

![Java](https://img.shields.io/badge/Java-17%2B-ed8b00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-6db33f?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ed?style=for-the-badge&logo=docker&logoColor=white)
![Kafka](https://img.shields.io/badge/Kafka-Event_Driven-231f20?style=for-the-badge&logo=apachekafka&logoColor=white)
![Debezium](https://img.shields.io/badge/Debezium-CDC-blue?style=for-the-badge)

## 📖 Executive Summary

**RestaurantSystem** is not just a food ordering application; it is a proof-of-concept for a **Distributed Transaction Processing System** built on a Microservices architecture.

Designed with scalability and fault tolerance in mind, this project solves the challenges of data consistency in distributed systems (Distributed Data Management) using **Event-Driven Architecture** and **Change Data Capture (CDC)** patterns. These are the same architectural principles used in core banking and high-frequency trading systems to ensure zero data loss.

## 🏗️ System Architecture

The system follows the **Database-per-Service** pattern to ensure loose coupling, orchestrated via Docker containers.

### High-Level Architecture Diagram
![System Architecture Diagram](https://res.cloudinary.com/dfcb3zzw9/image/upload/v1767682557/Bi%E1%BB%83u_%C4%91%E1%BB%93_kh%C3%B4ng_c%C3%B3_ti%C3%AAu_%C4%91%E1%BB%81.drawio_2_av0pas.png)


### Data Flow & CDC Architecture
This project implements **Debezium** to capture row-level changes from the MySQL database logs (Binlog) and stream them to Kafka, ensuring real-time data synchronization between the Order Service and Restaurant Service without dual-write problems.

![CDC Data Flow](https://res.cloudinary.com/dfcb3zzw9/image/upload/v1767683181/Bi%E1%BB%83u_%C4%91%E1%BB%93_kh%C3%B4ng_c%C3%B3_ti%C3%AAu_%C4%91%E1%BB%81.drawio_3_qjc9mv.png)


---

## 🧩 Microservices Breakdown

| Service | Port | Key Responsibilities |
| :--- | :--- | :--- |
| **API Gateway** | `8080` | Entry point, Routing, Load Balancing, SSL Termination. |
| **User Service (IAM)** | `8081` | Identity and Access Management, JWT Authentication, Security. |
| **Restaurant Service** | `8082` | Product Catalog, Inventory Management (CQRS Optimized). |
| **Order Service** | `8083` | **Core Transaction Engine**. Handles order lifecycle state machine. |
| **Payment Service** | `8084` | Simulates financial transaction processing and auditing. |
| **Monitor Service** | `9090` | **Spring Boot Admin** for centralized health checking & JVM metrics. |
| **Debezium Connector**| `8083` | Captures DB changes for real-time analytics and sync. |

## 🛠️ Technology Stack & Engineering Decisions

* **Core Backend:** Java 17, Spring Boot 3.x, Spring Cloud Gateway.
* **Data Storage:** MySQL (Primary DB), Redis (Caching - *planned*).
* **Inter-service Communication:**
    * **Synchronous:** REST API (OpenFeign) for read operations.
    * **Asynchronous:** Apache Kafka for high-throughput event streaming.
* **Data Consistency:** Debezium (CDC) for Event Sourcing capability.
* **DevOps & Infrastructure:** Docker, Docker Compose for orchestration.
* **Observability:** Spring Boot Admin, Actuator.

## 🚀 Key Features (Banking-Grade Focus)

1.  **Transactional Integrity:** Implemented SAGA Pattern (Choreography) to ensure data consistency across `Order` and `Payment` services.
2.  **Fault Tolerance:** Circuit Breaker implementation (Resilience4j) to prevent cascading failures when a downstream service is down.
3.  **Real-time Monitoring:** Integrated Dashboard to visualize Heap Memory, Thread Pools, and Request Latency—critical for maintaining SLAs in financial systems.
4.  **Security First:** Centralized Authentication via API Gateway using JWT.

## ⚙️ How to Run

### Prerequisites
* Docker & Docker Compose (v2.0+)
* Java JDK 17+
* Maven

### Step-by-Step
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/Thanhngo0812/MicroserviceProject.git](https://github.com/Thanhngo0812/MicroserviceProject.git)
    cd swa
    ```

2.  **Start the Infrastructure (DB, Kafka, Debezium):**
    ```bash
    docker-compose up -d
    ```

3.  **Verify Deployment:**
    * Access **Spring Boot Admin**: `http://localhost:9090`
    * Access **API Gateway**: `http://localhost:8080`

---

## 👨‍💻 About The Author

I am a final-year Software Engineering student with a deep passion for **Backend Systems (focus on distributed systems)** and **Cloud Native Architecture**. 

My career goal is to join a dynamic **Banking / Fintech** technology team as a Backend Engineer/Fresher. I am eager to apply my knowledge of Microservices, Distributed Systems, and Database Optimization to solve complex financial scale problems.

* **Main Focus:** Java Ecosystem, System Design, DevOps.
* **Contact:** ngocongthanhsg0812@gmail.com
* **LinkedIn:** www.linkedin.com/in/thành-ngô-08374024b