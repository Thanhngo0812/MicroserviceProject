-- Tạo schema riêng cho restaurant service
CREATE SCHEMA IF NOT EXISTS restaurant;

-- Trạng thái duyệt đơn (Approval Status)
CREATE TYPE restaurant.approval_status AS ENUM (
    'PENDING',  -- Đang chờ duyệt
    'APPROVED', -- Đã duyệt (OK)
    'REJECTED'  -- Đã từ chối (ví dụ: hết món)
);

-- Bảng lưu thông tin nhà hàng (đơn giản)
CREATE TABLE restaurant.restaurants (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true -- Nhà hàng có đang hoạt động không
);

-- Bảng lưu thông tin sản phẩm (Menu của nhà hàng)
CREATE TABLE restaurant.restaurant_products (
    id UUID NOT NULL PRIMARY KEY,
    restaurant_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT true, -- Còn hàng hay hết hàng
    CONSTRAINT fk_restaurant_products_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurant.restaurants (id)
        ON DELETE CASCADE
);

-- Bảng lưu trạng thái duyệt đơn (cho SAGA Idempotency)
CREATE TABLE restaurant.order_approvals (
    id UUID NOT NULL PRIMARY KEY,
    restaurant_id UUID NOT NULL,
    order_id UUID NOT NULL UNIQUE, -- Quan trọng: Đảm bảo 1 Order chỉ được duyệt 1 lần
    status restaurant.approval_status NOT NULL,
    failure_messages TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Bảng Outbox (Để gửi phản hồi APPROVED/REJECTED an toàn)
CREATE TYPE restaurant.outbox_status AS ENUM (
    'PENDING', 'COMPLETED', 'FAILED'
);

CREATE TABLE restaurant.restaurant_outbox (
    id UUID NOT NULL PRIMARY KEY,
    saga_id UUID NOT NULL, -- Chính là Order ID
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    event_type VARCHAR(255) NOT NULL, -- Tên Event (ví dụ: RestaurantApproved, RestaurantRejected)
    payload JSONB NOT NULL, -- Nội dung Event (JSON "phẳng")
    outbox_status restaurant.outbox_status NOT NULL
);

-- Indexes
CREATE INDEX idx_restaurant_products_restaurant_id ON restaurant.restaurant_products (restaurant_id);
CREATE INDEX idx_restaurant_outbox_status ON restaurant.restaurant_outbox (outbox_status);

-- -- -- -- -- -- -- -- -- -- --
-- Thêm dữ liệu mẫu (ĐỂ TEST) --
-- -- -- -- -- -- -- -- -- -- --
-- (Giả sử Restaurant ID này khớp với OrderService)
INSERT INTO restaurant.restaurants (id, name, active)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb45', 'Nhà hàng Mẫu', true)
ON CONFLICT (id) DO NOTHING;

-- (Giả sử Product ID này khớp với OrderService)
INSERT INTO restaurant.restaurant_products (id, restaurant_id, name, price, available)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb48', 'd215b5f8-0249-4dc5-89a3-51fd148cfb45', 'Món Ăn Mẫu', 50.00, true)
ON CONFLICT (id) DO NOTHING;

-- Thêm một món đã HẾT HÀNG (để test kịch bản REJECT)
INSERT INTO restaurant.restaurant_products (id, restaurant_id, name, price, available)
VALUES ('a111b111-0249-4dc5-89a3-51fd148cfb49', 'd215b5f8-0249-4dc5-89a3-51fd148cfb45', 'Món Ăn Hết Hàng', 100.00, false)
ON CONFLICT (id) DO NOTHING;