package com.ct08SWA.paymentservice.paymentapplicationservice.ports.inputports;


import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentCompensationRequest;

/**
 * Input Port (Use Case Interface)
 * Định nghĩa hành vi xử lý một yêu cầu hoàn tiền (Compensation).
 */
public interface PaymentCompensationMessageListener {

    /**
     * Xử lý logic hoàn tiền.
     * @param request DTO chứa thông tin hoàn tiền.
     */
    void processCompensation(PaymentCompensationRequest request);
}
