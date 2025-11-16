package com.ct08SWA.paymentservice.paymentapplicationservice.handler;


import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentCompensationRequest;
import com.ct08SWA.paymentservice.paymentapplicationservice.exception.PaymentApplicationServiceException;
import com.ct08SWA.paymentservice.paymentapplicationservice.ports.inputports.PaymentCompensationMessageListener;

import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditEntry;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditHistory;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;

import com.ct08SWA.paymentservice.paymentdomaincore.exception.PaymentDomainException;
import com.ct08SWA.paymentservice.paymentdomaincore.service.PaymentDomainService;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.OrderId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PaymentCompensationMessageListenerImpl implements PaymentCompensationMessageListener {
    private final PaymentCompensationHelper paymentCompensationHelper;
    private final PaymentDomainService paymentDomainService;


    public PaymentCompensationMessageListenerImpl(PaymentCompensationHelper paymentCompensationHelper,
                                                          PaymentDomainService paymentDomainService

                                               ) {
       this.paymentCompensationHelper=paymentCompensationHelper;
        this.paymentDomainService = paymentDomainService;
    }

    /**
     * Xử lý logic hoàn tiền (SAGA Compensation).
     * SỬA LẠI: Thêm Idempotency check.
     */
    @Override
    @Transactional
    public void processCompensation(PaymentCompensationRequest request) {
        log.info("Processing compensation (refund) for order id: {}", request.getOrderId());
        List<String> failureMessages = new ArrayList<>();

        try {
            // 1. Lấy thông tin cần thiết từ DB
            OrderId orderId = new OrderId(request.getOrderId());
            CustomerId customerId = new CustomerId(request.getCustomerId());

            Payment payment = paymentCompensationHelper.findPayment(orderId);
            CreditEntry creditEntry = paymentCompensationHelper.findCreditEntry(customerId);
            List<CreditHistory> creditHistories = paymentCompensationHelper.findCreditHistories(customerId);

            // 2. SỬA LẠI: KIỂM TRA IDEMPOTENCY (Trùng lặp)
            // (Kiểm tra xem đã hoàn tiền (CREDIT) cho orderId này chưa)
            if (paymentCompensationHelper.isCompensationAlreadyProcessed(creditHistories, orderId)) {
                log.warn("Compensation for order id: {} already processed. Ignoring duplicate message.",
                        request.getOrderId());
                return; // Bỏ qua (Ignore) message trùng lặp
            }

            // 3. Gọi Domain Service để thực thi logic nghiệp vụ Hoàn tiền
            // 3. Gọi Domain Service (Sửa 1 - Canvas)
            // (Nó trả về CreditHistory mới)
            CreditHistory creditHistory = paymentDomainService.validateAndCancelPayment(
                    payment, creditEntry, creditHistories, failureMessages);
            // 4. SỬA LẠI: Gọi hàm helper để LƯU và PUBLISH
            paymentCompensationHelper.persistAndPublishCompensation(payment, creditEntry, creditHistory);

            log.info("Compensation (refund) successful for order id: {}", request.getOrderId());

        } catch (PaymentDomainException e) {
            log.warn("Compensation failed (domain validation) for order id: {}. Reason: {}",
                    request.getOrderId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during compensation for order id: {}. Error: {}",
                    request.getOrderId(), e.getMessage(), e);
            throw new PaymentApplicationServiceException("Unexpected error during compensation", e);
        }
    }

}

