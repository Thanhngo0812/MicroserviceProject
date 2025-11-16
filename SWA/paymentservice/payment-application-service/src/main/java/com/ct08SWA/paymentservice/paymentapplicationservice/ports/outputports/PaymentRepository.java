package com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports;

import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.OrderId;

import java.util.Optional;

/**
 * Output Port: Interface định nghĩa các thao tác cần thiết với Payment entity.
 */
public interface PaymentRepository {

    /**
     * Lưu một đối tượng Payment.
     * @param payment Payment entity.
     * @return Payment entity đã được lưu.
     */
    Payment save(Payment payment);

    /**
     * Tìm kiếm Payment dựa trên OrderId.
     * @param orderId ID của Order.
     * @return Optional chứa Payment nếu tìm thấy.
     */
    Optional<Payment> findByOrderId(OrderId orderId);
}
