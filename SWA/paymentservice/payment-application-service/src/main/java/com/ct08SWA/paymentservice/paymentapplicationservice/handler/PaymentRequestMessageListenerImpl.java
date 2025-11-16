package com.ct08SWA.paymentservice.paymentapplicationservice.handler;
import com.ct08SWA.paymentservice.paymentapplicationservice.dto.inputdto.PaymentRequest;
import com.ct08SWA.paymentservice.paymentapplicationservice.mapper.PaymentDataMapper;
import com.ct08SWA.paymentservice.paymentapplicationservice.ports.inputports.PaymentRequestMessageListener;
import com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports.PaymentOutboxRepository;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentFailedEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.service.PaymentDomainService;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditEntry;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditHistory;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;
import com.ct08SWA.paymentservice.paymentdomaincore.exception.PaymentDomainException;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRequestHelper paymentRequestHelper;
    private final PaymentOutboxRepository paymentOutboxRepository;
    @Value("${payment-service.kafka.payment-response-topic}")
    private String paymentResponseTopic;
    public PaymentRequestMessageListenerImpl(PaymentDomainService paymentDomainService,
                                             PaymentDataMapper paymentDataMapper,
            PaymentRequestHelper paymentRequestHelper,PaymentOutboxRepository paymentOutboxRepository
                                           ) {
        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRequestHelper = paymentRequestHelper;
        this.paymentOutboxRepository = paymentOutboxRepository;
    }

    @Override
    @Transactional
    public void processPayment(PaymentRequest paymentRequest) {
        log.info("Processing payment for order id: {}", paymentRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Payment payment = null;
        try {
            payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);
            payment.initializePayment(); // Gọi initialize ở đây
            CustomerId customerId = new CustomerId(UUID.fromString(paymentRequest.getCustomerId()));
            CreditEntry creditEntry = paymentRequestHelper.findCreditEntry(customerId);

            List<CreditHistory> creditHistories = paymentRequestHelper.findCreditHistories(customerId);
            if (paymentRequestHelper.isPaymentAlreadyProcessed(creditHistories, payment.getOrderId())) {
                log.warn("Payment for order id: {} already processed. Ignoring duplicate message.",
                        paymentRequest.getOrderId());
                return; // Bỏ qua (Ignore) message trùng lặp, không làm gì cả
            }

            CreditHistory creditHistory = paymentDomainService.validateAndInitiatePayment(
                    payment, creditEntry, creditHistories, failureMessages);


            // 4. LỚP APPLICATION chịu trách nhiệm LƯU trữ và PUBLISH
            paymentRequestHelper.persistPaymentSuccess(payment,creditEntry,creditHistory); // <-- Gọi hàm lưu và publish thành công
            paymentOutboxRepository.save(payment.getDomainEvents().get(0),payment.getId().getValue(),paymentResponseTopic);
            log.info("Payment processing successful for order id: {}", paymentRequest.getOrderId());

        } catch (PaymentDomainException e) {
            log.warn("Payment failed for order id: {}. Reason: {}", paymentRequest.getOrderId(), e.getMessage());
            // failureMessages đã được cập nhật bên trong Domain Service
            paymentRequestHelper.persistPaymentFailure(payment, failureMessages); // <-- Gọi hàm lưu và publish thất bại
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                    payment.getId() != null ? payment.getId().getValue() : null,
                    payment.getOrderId().getValue(),
                    payment.getCustomerId().getValue(),
                    payment.getPrice().getAmount(),
                    ZonedDateTime.now(),
                    failureMessages,
                    payment.getStatus().toString()
            );
            payment.addDomainEvent(failedEvent);
            paymentOutboxRepository.save(payment.getDomainEvents().get(0),payment.getId().getValue(),paymentResponseTopic);
        } catch (Exception e) {
            log.error("Unexpected error processing payment for order id: {}. Error: {}", paymentRequest.getOrderId(), e.getMessage(), e);
            if (payment != null) {
                failureMessages.add("Unexpected error processing payment.");
                paymentRequestHelper.persistPaymentFailure(payment, failureMessages);
                PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                        payment.getId() != null ? payment.getId().getValue() : null,
                        payment.getOrderId().getValue(),
                        payment.getCustomerId().getValue(),
                        payment.getPrice().getAmount(),
                        ZonedDateTime.now(),
                        failureMessages,
                        payment.getStatus().toString()
                );
                // Gắn event vào Payment entity
                payment.addDomainEvent(failedEvent);
                paymentOutboxRepository.save(payment.getDomainEvents().get(0),payment.getId().getValue(),paymentResponseTopic);

            }
        }
    }



}

