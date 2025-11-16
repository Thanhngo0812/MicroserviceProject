package com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Listener;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Payment.PaymentResponse;

/**
 * INPUT PORT cho Order Service.
 * Định nghĩa cách Order Service xử lý các message phản hồi từ Payment Service.
 */
public interface PaymentResponseMessageListener {

    /**
     * Xử lý khi thanh toán THÀNH CÔNG.
     * Cập nhật Order status thành PAID và gửi yêu cầu duyệt đơn hàng.
     * @param paymentResponse DTO chứa thông tin phản hồi từ Payment Service.
     */
    void paymentCompleted(PaymentResponse paymentResponse);

    /**
     * Xử lý khi thanh toán THẤT BẠI.
     * Cập nhật Order status thành CANCELLED.
     * @param paymentResponse DTO chứa thông tin phản hồi từ Payment Service.
     */
    void paymentFailed(PaymentResponse paymentResponse);

    /**
     * Xử lý khi hoàn tiền THÀNH CÔNG (sau compensation).
     * Cập nhật Order status thành CANCELLED.
     * @param paymentResponse DTO chứa thông tin phản hồi từ Payment Service.
     */
    void paymentCancelled(PaymentResponse paymentResponse);
}