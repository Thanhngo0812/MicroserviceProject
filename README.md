# MicroserviceProject
This project is a microservices-based system designed to demonstrate a modular, scalable architecture for managing restaurant, ordering, and payment workflows. Each core domain is implemented as an independent service, allowing for flexibility, isolated development, and easier maintenance.
Current Services
Restaurant Service
Manages restaurant information, menus, and related data operations.
Order Service
Handles order creation, updates, tracking, and communication with other services such as payment and restaurant availability.
Payment Service
Processes payments, updates payment status, and integrates with order flows.
Database Initialization Modules
init_db_orderservice
init_db_paymentservice
Scripts and configurations for setting up the databases required by each corresponding service.
Infrastructure & Integration
debeziumconfig
Contains configuration for Debezium-based change data capture (CDC), enabling real-time event streaming between services.
docker-compose.yml
Orchestrates all services, databases, and supporting tools in a local development environment.
