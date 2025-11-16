package com.ct08SWA.paymentservice.paymentapplicationservice.ports.inputports;

import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentRequest;

/**
 * Input Port: Interface định nghĩa cách nhận và xử lý yêu cầu thanh toán từ Kafka.
 */
public interface PaymentRequestMessageListener {

    /**
     * Xử lý một yêu cầu thanh toán nhận được.
     * @param paymentRequest DTO chứa thông tin yêu cầu thanh toán.
     */
    void processPayment(PaymentRequest paymentRequest);

//    /**
//     * Xử lý một yêu cầu hủy thanh toán (compensation).
//     * @param paymentRequest DTO chứa thông tin yêu cầu hủy.
//     */
//    void cancelPayment(PaymentRequest paymentRequest); // Thêm hàm cho SAGA compensation
}
