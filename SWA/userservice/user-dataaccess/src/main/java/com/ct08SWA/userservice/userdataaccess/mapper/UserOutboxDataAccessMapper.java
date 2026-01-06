package com.ct08SWA.userservice.userdataaccess.mapper;

import com.ct08SWA.userservice.userdataaccess.entity.UserOutboxEntity;
import com.ct08SWA.userservice.userdomaincore.event.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class UserOutboxDataAccessMapper {
    private final ObjectMapper objectMapper;

    // Inject ObjectMapper qua constructor
    public UserOutboxDataAccessMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    /**
     * Chuyển từ JPA Entity (DB) sang Domain Entity (Logic)
     */
    public UserOutboxEntity paymentOutboxEntityToPaymentOutbox(UserEvent paymentEvent, UUID SagaId, String Topic) {

        String payloadJson = writePayload(paymentEvent);
        // Dùng Builder "sạch"
        return UserOutboxEntity.builder()
                .id(UUID.randomUUID())
                .sagaId(SagaId)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .eventType(Topic)
                .payload(payloadJson)
                .build();
    }

    // Tách hàm xử lý JSON ra riêng và bọc try-catch
    private String writePayload(UserEvent paymentEvent) {
        try {
            // Đây là dòng code gây lỗi
            return objectMapper.writeValueAsString(paymentEvent);

        } catch (JsonProcessingException e) {
            // Log lỗi nghiêm trọng này

            // Ném ra một RuntimeException để làm fail transaction
            // Đây là cách "clean" để xử lý checked exception
            throw new RuntimeException("Không thể serialize OrderEvent thành JSON", e);

            // Tốt hơn nữa: Ném ra một exception tùy chỉnh
            // throw new OrderDomainException("Không thể serialize OrderEvent", e);
        }
    }
}
