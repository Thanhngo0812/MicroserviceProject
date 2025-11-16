package com.ct08SWA.paymentservice.paymentdomaincore.service;

import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditEntry;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.CreditHistory;
import com.ct08SWA.paymentservice.paymentdomaincore.entity.Payment;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentCancelledEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentCompletedEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentFailedEvent;
import com.ct08SWA.paymentservice.paymentdomaincore.exception.PaymentDomainException;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.CreditHistoryId;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.Money;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.PaymentStatus;
import com.ct08SWA.paymentservice.paymentdomaincore.valueobject.TransactionType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

/**
 * Implementation của PaymentDomainService.
 * Chứa logic nghiệp vụ cốt lõi cho việc xử lý thanh toán và tín dụng.
 * Lưu ý: Class này KHÔNG sử dụng annotation của Spring (@Service, @Autowired).
 */

public class PaymentDomainServiceImpl implements PaymentDomainService {

    /**
     * Xác thực và khởi tạo giao dịch thanh toán.
     * Kiểm tra số dư, kiểm tra giao dịch trùng lặp, cập nhật trạng thái
     * và tạo CreditHistory tương ứng.
     *
     * @param payment         Payment entity (vừa được khởi tạo, status PENDING).
     * @param creditEntry     CreditEntry của khách hàng.
     * @param creditHistories Lịch sử giao dịch gần đây của khách hàng.
     * @param failureMessages Danh sách để ghi lại thông báo lỗi nếu có.
     * @return PaymentEvent (PaymentCompletedEvent hoặc PaymentFailedEvent).
     */
    @Override
    public CreditHistory validateAndInitiatePayment(Payment payment,
                                                   CreditEntry creditEntry,
                                                   List<CreditHistory> creditHistories,
                                                   List<String> failureMessages) {
        // SỬA LỖI: Thêm .toString()
        payment.validatePayment(); // Kiểm tra giá tiền > 0

        // 1. Kiểm tra giao dịch trùng lặp (Idempotency check)
        checkPreviousTransactions(payment, creditHistories);

        // 2. Kiểm tra số dư
        checkCreditLimit(payment, creditEntry, failureMessages);

        // 3. Cập nhật số dư tín dụng
        subtractCreditEntry(payment, creditEntry);

        // 4. Cập nhật lịch sử tín dụng
        CreditHistory creditHistory = updateCreditHistory(payment, TransactionType.DEBIT);

        // 5. Cập nhật trạng thái Payment
        payment.completePayment(); // Chuyển status sang COMPLETED

        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(payment.getId().getValue(),
                payment.getOrderId().getValue(),
                payment.getCustomerId().getValue(),
                payment.getPrice().getAmount(),
                payment.getCreatedAt(),
                creditHistory.getId().getValue(),
                payment.getStatus().toString());
        payment.addDomainEvent(paymentCompletedEvent);
        // SỬA LỖI: Thêm .toString()
        // 6. Trả về sự kiện thành công
        return creditHistory;
    }


    /**
     * Xác thực và hủy giao dịch thanh toán (SAGA Compensation).
     * Kiểm tra trạng thái, kiểm tra giao dịch trùng lặp, cập nhật trạng thái
     * và tạo CreditHistory hoàn tiền.
     *
     * @param payment         Payment entity cần hủy.
     * @param creditEntry     CreditEntry của khách hàng.
     * @param creditHistories Lịch sử giao dịch gần đây của khách hàng.
     * @param failureMessages Danh sách để ghi lại thông báo lỗi nếu có.
     * @return PaymentCancelledEvent nếu hủy thành công.
     */
    @Override
    public CreditHistory validateAndCancelPayment(Payment payment,
                                                 CreditEntry creditEntry,
                                                 List<CreditHistory> creditHistories,
                                                 List<String> failureMessages) {
        // SỬA LỖI: Thêm .toString()
        payment.validatePayment(); // Đảm bảo payment object hợp lệ

        checkPreviousCancellation(payment, creditHistories);

        // 2. Cập nhật số dư (hoàn tiền)
        addCreditEntry(payment, creditEntry);

        // 3. Cập nhật lịch sử (ghi nhận giao dịch CREDIT)
        CreditHistory creditHistory = updateCreditHistory(payment, TransactionType.CREDIT);

        // 4. Cập nhật trạng thái Payment
        payment.cancelPayment(); // Chuyển status sang CANCELLED
        // SỬA LỖI: Thêm .toString()
        PaymentCancelledEvent paymentCancelledEvent = new PaymentCancelledEvent(payment.getId().getValue(),
                payment.getOrderId().getValue(),
                payment.getCustomerId().getValue(),
                payment.getPrice().getAmount(),
                payment.getCreatedAt(),
                creditHistory.getId().getValue(),
                payment.getStatus().toString());
        payment.addDomainEvent(paymentCancelledEvent);
        // 5. Trả về sự kiện hủy thành công
        return creditHistory;

    }


    // --- Private Helper Methods ---

    /**
     * Kiểm tra xem giao dịch thanh toán (DEBIT) cho Order này đã tồn tại chưa.
     */
    private void checkPreviousTransactions(Payment payment, List<CreditHistory> creditHistories) {
        creditHistories.stream()
                .filter(history -> history.getTransactionType() == TransactionType.DEBIT &&
                        history.getOrderId().equals(payment.getOrderId()))
                .findAny()
                .ifPresent(history -> {
                    // SỬA LỖI: Thêm .toString()
                    // SỬA LỖI: Thêm .toString()
                    throw new PaymentDomainException("Payment for order id: " + payment.getOrderId().getValue().toString() + " already processed!");
                });
    }

    /**
     * Kiểm tra xem khách hàng có đủ tín dụng để thanh toán không.
     * Nếu không đủ, ném exception và cập nhật failureMessages.
     */
    private void checkCreditLimit(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {
        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            // SỬA LỖI: Thêm .toString()
            String message = "Customer with id: " + payment.getCustomerId().getValue().toString() +
                    " doesn't have enough credit for payment!";
            failureMessages.add(message);
            // Ném exception để dừng quá trình và trả về PaymentFailedEvent
            throw new PaymentDomainException(message);
            // Lưu ý: Chúng ta sẽ không tạo FAILED event ở đây,
            // lớp Application Service sẽ bắt exception này và tạo FAILED event
        }
    }

    /**
     * Trừ số tiền thanh toán khỏi số dư của khách hàng.
     */
    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    /**
     * Cộng lại số tiền (hoàn tiền) vào số dư của khách hàng.
     */
    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.addCreditAmount(payment.getPrice());
    }


    /**
     * Tạo và trả về một bản ghi CreditHistory mới.
     */
    private CreditHistory updateCreditHistory(Payment payment, TransactionType transactionType) {
        return CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .orderId(payment.getOrderId()) // Liên kết với Order
                .amount(payment.getPrice())
                .transactionType(transactionType)
                .build();
    }

    /**
     * Kiểm tra xem giao dịch hoàn tiền (CREDIT) cho Order này đã tồn tại chưa.
     */
    private void checkPreviousCancellation(Payment payment, List<CreditHistory> creditHistories) {
        creditHistories.stream()
                .filter(history -> history.getTransactionType() == TransactionType.CREDIT &&
                        history.getOrderId().equals(payment.getOrderId()))
                .findAny()
                .ifPresent(history -> {
                    // SỬA LỖI: Thêm .toString()
                    // SỬA LỖI: Thêm .toString()
                    throw new PaymentDomainException("Payment cancellation for order id: " + payment.getOrderId().getValue().toString() + " already processed!");
                });
    }

    // Lưu ý: Logic tạo PaymentFailedEvent sẽ nằm ở lớp Application Service
    // khi nó bắt được PaymentDomainException từ checkCreditLimit.
}

