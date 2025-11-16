package com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports;

import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentCompletedEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentFailedEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentCancelledEvent; // Thêm event cho cancel

/**
 * Output Port: Interface định nghĩa việc publish các event phản hồi kết quả thanh toán.
 */
public interface PaymentResponseMessagePublisher {

    /**
     * Publish sự kiện thanh toán thành công.
     * @param paymentCompletedEvent Sự kiện.
     */
    void publish(PaymentCompletedEvent paymentCompletedEvent);

    /**
     * Publish sự kiện thanh toán thất bại.
     * @param paymentFailedEvent Sự kiện.
     */
    void publish(PaymentFailedEvent paymentFailedEvent);

    /**
     * Publish sự kiện hủy thanh toán thành công (compensation).
     * @param paymentCancelledEvent Sự kiện.
     */
    void publish(PaymentCancelledEvent paymentCancelledEvent); // Thêm hàm cho cancel
}
