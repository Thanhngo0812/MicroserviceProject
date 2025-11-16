package com.ct08SWA.paymentservice.paymentdataaccess.mapper;

import com.ct08SWA.paymentservice.paymentdataaccess.entity.PaymentOutboxEntity;
import com.ct08SWA.paymentservice.paymentdomaincore.event.PaymentEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Mapper: Chuyển đổi giữa PaymentOutbox (Domain Entity "sạch")
 * và PaymentOutboxEntity (JPA Entity "bẩn").
 * KHÔNG CÓ LOMBOK.
 */
@Component
@Slf4j // Dùng Lombok để có biến 'log'
public class PaymentOutboxDataAccessMapper {
    private final ObjectMapper objectMapper;

    // Inject ObjectMapper qua constructor
    public PaymentOutboxDataAccessMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    /**
     * Chuyển từ JPA Entity (DB) sang Domain Entity (Logic)
     */
    public PaymentOutboxEntity paymentOutboxEntityToPaymentOutbox(PaymentEvent paymentEvent, UUID SagaId,String Topic) {

        String payloadJson = writePayload(paymentEvent);
        // Dùng Builder "sạch"
        return PaymentOutboxEntity.builder()
                .id(UUID.randomUUID())
                .sagaId(SagaId)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .eventType(Topic)
                .payload(payloadJson)
                .build();
    }

    // Tách hàm xử lý JSON ra riêng và bọc try-catch
    private String writePayload(PaymentEvent paymentEvent) {
        try {
            // Đây là dòng code gây lỗi
            return objectMapper.writeValueAsString(paymentEvent);

        } catch (JsonProcessingException e) {
            // Log lỗi nghiêm trọng này
            log.error("Không thể serialize OrderEvent thành JSON. Event: {}", paymentEvent, e);

            // Ném ra một RuntimeException để làm fail transaction
            // Đây là cách "clean" để xử lý checked exception
            throw new RuntimeException("Không thể serialize OrderEvent thành JSON", e);

            // Tốt hơn nữa: Ném ra một exception tùy chỉnh
            // throw new OrderDomainException("Không thể serialize OrderEvent", e);
        }
    }
}
