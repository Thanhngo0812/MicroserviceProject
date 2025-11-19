-- Create schema
CREATE SCHEMA IF NOT EXISTS "order";
-- Create ENUM type for order status
CREATE TYPE order_status AS ENUM (
'PENDING',
'PAID',
'APPROVED',
'CANCELLING',
'CANCELLED'
);
-- Orders table (main aggregate)
CREATE TABLE "order".orders (
id UUID NOT NULL,
customer_id UUID NOT NULL,
restaurant_id UUID NOT NULL,
tracking_id UUID NOT NULL UNIQUE,
price NUMERIC(10,2) NOT NULL,
order_status order_status NOT NULL,
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
CREATE TABLE "order".order_outbox (
    id UUID NOT NULL PRIMARY KEY,
    saga_id UUID NOT NULL, -- Thường là Order ID, để theo dõi
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    event_type VARCHAR(255) NOT NULL, -- Tên Event, ví dụ: "OrderCreated"
    payload JSONB NOT NULL,
    status TEXT NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_orders_tracking_id
ON "order".orders(tracking_id);
CREATE INDEX idx_orders_customer_id
ON "order".orders(customer_id);

ALTER DATABASE orderservice SET bytea_output = 'hex';