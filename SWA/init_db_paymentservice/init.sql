-- Tạo schema riêng cho payment service
CREATE SCHEMA IF NOT EXISTS payment;

-- Tạo ENUM type cho payment status
CREATE TYPE payment.payment_status AS ENUM (
    'PENDING', -- Trạng thái ban đầu khi nhận request
    'COMPLETED', -- Thanh toán thành công
    'FAILED',    -- Thanh toán thất bại (ví dụ: không đủ tiền)
    'CANCELLED'  -- Thanh toán bị hủy (ví dụ: do Order bị reject)
);

-- Tạo ENUM type cho transaction type trong credit history
CREATE TYPE payment.transaction_type AS ENUM (
    'DEBIT', -- Trừ tiền (khi thanh toán)
    'CREDIT' -- Hoàn tiền (khi order bị reject)
);

-- Bảng payments (lưu thông tin thanh toán cho mỗi order)
CREATE TABLE payment.payments (
    id UUID NOT NULL PRIMARY KEY,
    customer_id UUID NOT NULL,
    order_id UUID NOT NULL UNIQUE, -- Đảm bảo mỗi order chỉ có 1 payment
    price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status payment.payment_status NOT NULL
);

-- Bảng credit_entry (lưu số dư tín dụng hiện tại của khách hàng)
CREATE TABLE payment.credit_entry (
    id UUID NOT NULL PRIMARY KEY,
    customer_id UUID NOT NULL UNIQUE, -- Mỗi khách hàng chỉ có 1 credit entry
    total_credit_amount NUMERIC(10, 2) NOT NULL
);

-- Bảng credit_history (lưu lịch sử giao dịch tín dụng)
-- Bảng này quan trọng để kiểm tra tính duy nhất (idempotency)
-- và để thực hiện hoàn tiền (compensation)
CREATE TABLE payment.credit_history (
    id UUID NOT NULL PRIMARY KEY,
    customer_id UUID NOT NULL,
    order_id UUID NOT NULL, -- Liên kết với Order tương ứng
    amount NUMERIC(10, 2) NOT NULL,
    type payment.transaction_type NOT NULL -- DEBIT hoặc CREDIT
);

-- ==========================================================
-- SỬA LẠI: BỔ SUNG BẢNG OUTBOX
-- ==========================================================
-- Bảng này dùng để triển khai Transactional Outbox Pattern
-- (Tương tự như 'order_outbox' bên OrderService)


CREATE TABLE payment.payment_outbox (
    id UUID NOT NULL PRIMARY KEY,
    saga_id UUID NOT NULL, -- Thường là Order ID, để theo dõi SAGA
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    event_type VARCHAR(255) NOT NULL, -- Tên Event, ví dụ: "PaymentCompleted", "PaymentCancelled"
    payload TEXT NOT NULL, 
);
ALTER DATABASE paymentservice SET bytea_output = 'hex';

-- Thêm index để tăng tốc độ truy vấn
CREATE INDEX idx_payments_customer_id ON payment.payments (customer_id);
CREATE INDEX idx_credit_history_customer_id ON payment.credit_history (customer_id);
CREATE INDEX idx_credit_history_order_id ON payment.credit_history (order_id);

-- Thêm index cho Outbox Poller
CREATE INDEX idx_payment_outbox_status ON payment.payment_outbox (outbox_status);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- Thêm dữ liệu mẫu (TÙY CHỌN - dùng để test) --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

-- Giả sử có một khách hàng với ID này và số dư ban đầu là 1000
-- Lưu ý: Customer ID ở đây phải khớp với customer_id trong order request
INSERT INTO payment.credit_entry (id, customer_id, total_credit_amount)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb49', 'd215b5f8-0249-4dc5-89a3-51fd148cfb41', 1000.00)
ON CONFLICT (customer_id) DO NOTHING; -- Tránh lỗi nếu customer_id đã tồn tại

