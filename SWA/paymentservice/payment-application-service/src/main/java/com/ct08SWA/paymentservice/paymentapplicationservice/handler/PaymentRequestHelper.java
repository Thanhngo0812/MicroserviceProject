package com.ct08SWA.paymentservice.paymentapplicationservice.handler;

import com.ct08SWA.paymentservice.paymentapplicationservice.exception.PaymentApplicationServiceException;
import com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports.*;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditEntry;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditHistory;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentCompletedEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentFailedEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.OrderId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Component
public class PaymentRequestHelper {
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    public PaymentRequestHelper(PaymentRepository paymentRepository,
                                             CreditEntryRepository creditEntryRepository,
                                             CreditHistoryRepository creditHistoryRepository
           ,PaymentOutboxRepository paymentOutboxRepository
    ) {

        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
    }


    /**
     * SỬA LẠI: Hàm helper mới để kiểm tra Idempotency.
     * Kiểm tra xem giao dịch DEBIT cho OrderId này đã tồn tại chưa.
     */
    public boolean isPaymentAlreadyProcessed(List<CreditHistory> creditHistories, OrderId orderId) {
        return creditHistories.stream()
                .anyMatch(history -> history.getTransactionType() == TransactionType.DEBIT &&
                        history.getOrderId().equals(orderId));
    }
    /**
     * Hàm helper để LƯU trạng thái THÀNH CÔNG vào DB và PUBLISH PaymentCompletedEvent.
     * Được gọi khi Domain Service trả về PaymentCompletedEvent.
     */
    public void persistPaymentSuccess(Payment payment,CreditEntry creditEntry, CreditHistory creditHistory) {
        // BƯỚC 3 - LƯU vào CSDL
        paymentRepository.save(payment); // Lưu Payment (status COMPLETED)
        creditEntryRepository.save(creditEntry); // Lưu CreditEntry (đã trừ tiền)
        creditHistoryRepository.save(creditHistory); // Lưu CreditHistory (DEBIT)

        log.info("Published PaymentCompletedEvent for order id: {}", payment.getOrderId().getValue());
    }

    /**
     * Hàm helper để LƯU trạng thái FAILED vào DB và PUBLISH PaymentFailedEvent.
     * Được gọi khi có PaymentDomainException hoặc lỗi không mong muốn.
     */
    public void persistPaymentFailure(Payment payment, List<String> failureMessages) {
        if (payment == null) {
            log.error("Cannot persist payment failure because payment object is null!");
            return;
        }
        log.warn("Persisting failed payment for order id: {}", payment.getOrderId().getValue());
        payment.failPayment(); // Cập nhật status FAILED
        paymentRepository.save(payment); // Lưu Payment (status FAILED)
        log.info("Published PaymentFailedEvent for order id: {}", payment.getOrderId().getValue());
    }

    /**
     * Tìm kiếm Credit Entry của khách hàng.
     */
    public CreditEntry findCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntryOptional = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntryOptional.isEmpty()) {
            log.error("Could not find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: " + customerId.getValue());
        }
        return creditEntryOptional.get();
    }

    /**
     * Tìm kiếm lịch sử giao dịch của khách hàng.
     */
    public List<CreditHistory> findCreditHistories(CustomerId customerId) {
        return creditHistoryRepository.findByCustomerId(customerId);
    }
}
