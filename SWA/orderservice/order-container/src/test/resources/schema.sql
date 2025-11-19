-- Create schema for H2 database
CREATE SCHEMA IF NOT EXISTS "order";

-- H2 doesn't support ENUM types like PostgreSQL, so we use VARCHAR
-- The application will handle the enum conversion

-- Orders table (main aggregate)
CREATE TABLE "order".orders (
    id UUID NOT NULL,
    customer_id UUID NOT NULL,
    restaurant_id UUID NOT NULL,
    tracking_id UUID NOT NULL UNIQUE,
    price NUMERIC(10,2) NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    failure_messages VARCHAR,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

-- Order items table (aggregate members)
CREATE TABLE "order".order_items (
    id BIGINT NOT NULL,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    quantity INTEGER NOT NULL,
    sub_total NUMERIC(10,2) NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (id, order_id),
    CONSTRAINT fk_order_id FOREIGN KEY (order_id)
        REFERENCES "order".orders(id)
        ON DELETE CASCADE
);

-- Order outbox table (for transactional outbox pattern)
CREATE TABLE "order".order_outbox (
    id UUID NOT NULL PRIMARY KEY,
    saga_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload VARCHAR(10000) NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_orders_tracking_id
    ON "order".orders(tracking_id);

CREATE INDEX idx_orders_customer_id
    ON "order".orders(customer_id);
