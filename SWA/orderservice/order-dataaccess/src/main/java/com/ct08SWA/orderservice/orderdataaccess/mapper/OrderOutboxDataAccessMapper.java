package com.ct08SWA.orderservice.orderdataaccess.mapper;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderOutboxEntity;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderCancelledEvent;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderCreatedEvent;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderEvent;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderPaidEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Mapper: Chuyển đổi giữa OrderOutbox (Domain Entity) và OrderOutboxEntity (JPA Entity).
 */
@Component
@Slf4j // Dùng Lombok để có biến 'log'
public class OrderOutboxDataAccessMapper {
    private final ObjectMapper objectMapper;

    // Inject ObjectMapper qua constructor
    public OrderOutboxDataAccessMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    /**
     * Chuyển đổi từ Domain Entity (Java thuần túy) sang JPA Entity (để lưu vào DB).
     */
    public OrderOutboxEntity domainToEntity(OrderEvent orderEvent, UUID SagaId, String topic) {
        String payloadJson = writePayload(orderEvent);
        return OrderOutboxEntity.builder()
                .id(UUID.randomUUID())
                .sagaId(SagaId)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .eventType(topic)
                .payload(payloadJson)
                .status(resolveStatus(orderEvent))
                .build();
    }

    private String resolveStatus(OrderEvent orderEvent) {
        if (orderEvent instanceof OrderCreatedEvent) {
            return "CREATED";
        } else if (orderEvent instanceof OrderPaidEvent) {
            return "PAID";
        } else if (orderEvent instanceof OrderCancelledEvent) {
            return "CANCELLED";
        }
        return "UNKNOWN"; // Trường hợp mặc định
    }
    // Tách hàm xử lý JSON ra riêng và bọc try-catch
    private String writePayload(OrderEvent orderEvent) {
        try {
            // Đây là dòng code gây lỗi
            return objectMapper.writeValueAsString(orderEvent);

        } catch (JsonProcessingException e) {
            // Log lỗi nghiêm trọng này
            log.error("Không thể serialize OrderEvent thành JSON. Event: {}", orderEvent, e);

            // Ném ra một RuntimeException để làm fail transaction
            // Đây là cách "clean" để xử lý checked exception
            throw new RuntimeException("Không thể serialize OrderEvent thành JSON", e);

            // Tốt hơn nữa: Ném ra một exception tùy chỉnh
            // throw new OrderDomainException("Không thể serialize OrderEvent", e);
        }
    }
}
