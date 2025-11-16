package com.ct08SWA.paymentservice.paymentapplicationservice.ports.outputports;


import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentEvent;
import java.util.UUID;

/**
 * Output Port (Interface) cho Payment Outbox Repository.
 * Lớp Application Service (bên trong) sẽ sử dụng interface này.
 * Lớp DataAccess (bên ngoài) sẽ implement (triển khai) interface này.
 */
public interface PaymentOutboxRepository {

    /**
     * Lưu một message vào outbox (trong cùng transaction với nghiệp vụ chính).
     * Được gọi bởi các Handler (PaymentRequestMessageListenerImpl, PaymentCompensationMessageListenerImpl).
     *
     * @param paymentEvent Đối tượng Outbox (Domain Entity)
     */
   void save(PaymentEvent paymentEvent, UUID SagaId, String Topic);


}
