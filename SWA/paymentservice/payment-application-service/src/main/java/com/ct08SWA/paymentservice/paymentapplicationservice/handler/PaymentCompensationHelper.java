package com.ct08SWA.paymentservice.paymentapplicationservice.handler;

import com.ct08SWA.paymentservice.paymentapplicationservice.exception.PaymentApplicationServiceException;
import com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports.*;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditEntry;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditHistory;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;

import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CustomerId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.OrderId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Component
public class PaymentCompensationHelper {

    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    public PaymentCompensationHelper(
                                                  PaymentRepository paymentRepository,
                                                  CreditEntryRepository creditEntryRepository,
                                                  CreditHistoryRepository creditHistoryRepository) {

        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
    }

    /**
     * SỬA LẠI: Hàm helper mới để kiểm tra Idempotency cho Hoàn tiền.
     * Kiểm tra xem giao dịch CREDIT cho OrderId này đã tồn tại chưa.
     */
    public boolean isCompensationAlreadyProcessed(List<CreditHistory> creditHistories, OrderId orderId) {
        return creditHistories.stream()
                .anyMatch(history -> history.getTransactionType() == TransactionType.CREDIT &&
                        history.getOrderId().equals(orderId));
    }
    /**
     * HÀM HELPER MỚI: Chịu trách nhiệm LƯU CSDL và PUBLISH event sau khi
     * logic domain (hoàn tiền) đã chạy thành công.
     * @param payment Payment (đã được Domain Service cập nhật status -> CANCELLED)
     * @param creditEntry CreditEntry (đã được Domain Service cập nhật số dư)
     */
    public void persistAndPublishCompensation(Payment payment, CreditEntry creditEntry, CreditHistory  creditHistory) {

        // 2. LƯU CÁC THAY ĐỔI (Persist)
        log.info("Persisting compensation changes for order id: {}", payment.getOrderId().getValue());
        paymentRepository.save(payment); // Lưu Payment (status CANCELLED)
        creditEntryRepository.save(creditEntry); // Lưu CreditEntry (số dư đã CỘNG LẠI)
        creditHistoryRepository.save(creditHistory); // Lưu CreditHistory (CREDIT)
    }
    // --- Các hàm helper (findPayment, findCreditEntry, findCreditHistories) ---

    public Payment findPayment(OrderId orderId) {
        Optional<Payment> paymentOptional = paymentRepository.findByOrderId(orderId);
        if (paymentOptional.isEmpty()) {
            log.error("Could not find payment for order id: {}", orderId.getValue());
            throw new PaymentApplicationServiceException("Could not find payment for order id: " + orderId.getValue());
        }
        return paymentOptional.get();
    }

    public CreditEntry findCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntryOptional = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntryOptional.isEmpty()) {
            log.error("Could not find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: " + customerId.getValue());
        }
        return creditEntryOptional.get();
    }

    public List<CreditHistory> findCreditHistories(CustomerId customerId) {
        return creditHistoryRepository.findByCustomerId(customerId);
    }
}
