package com.ct08SWA.paymentservice.paymentdomaincore.service;

import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditEntry;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditHistory;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {

    // Logic chính: Validate và bắt đầu thanh toán
    CreditHistory validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages); // Để chứa lỗi nếu có

    // Logic bù trừ: Validate và bắt đầu hoàn tiền
    CreditHistory validateAndCancelPayment(Payment payment,
                                          CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories,
                                          List<String> failureMessages); // Để chứa lỗi nếu có

}
