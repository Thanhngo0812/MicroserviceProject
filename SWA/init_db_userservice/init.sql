CREATE SCHEMA IF NOT EXISTS "user";

CREATE TABLE "user".users (
                              id UUID NOT NULL PRIMARY KEY,
                              username VARCHAR(50) NOT NULL UNIQUE,
                              password VARCHAR(255) NOT NULL, -- Password đã mã hóa (BCrypt)
                              email VARCHAR(100) NOT NULL UNIQUE,
                              first_name VARCHAR(50),
                              last_name VARCHAR(50),
                              role VARCHAR(20) NOT NULL, -- USER, ADMIN
                              active BOOLEAN NOT NULL DEFAULT true,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE "user".user_outbox (
                                              id UUID NOT NULL PRIMARY KEY,
                                              saga_id UUID NOT NULL, -- Thường là Order ID, để theo dõi SAGA
                                              created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                              event_type VARCHAR(255) NOT NULL, -- Tên Event, ví dụ: "PaymentCompleted", "PaymentCancelled"
                                              payload JSONB NOT NULL
);

-- Insert Admin User (Password: admin123 -> BCrypt hash)
INSERT INTO "user".users (id, username, password, email, first_name, last_name, role, active, created_at)
VALUES (
           'd215b5f8-0249-4dc5-89a3-51fd148cfb41',
           'admin',
           '$2b$12$g3ojVMhmIWxkgjykb.Skwex9uZ6oM2yvZ3Qzv55kuog2f79zkyeMq', -- Hash mẫu
           'admin@test.com',
           'Admin',
           'User',
           'ADMIN',
           true,
           NOW()
       );