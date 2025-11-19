package com.ct08SWA.restaurantservice.restaurantapplicationservice.handler;



import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderApprovalCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.exception.RestaurantApplicationServiceException;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports.OrderApprovalCommandHandler;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports.OrderApprovalRepository;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports.RestaurantOutboxRepository;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderApproval;
import com.ct08SWA.restaurantservice.restaurantdomaincore.event.RestaurantApprovedEvent;
import com.ct08SWA.restaurantservice.restaurantdomaincore.event.RestaurantRejectedEvent;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.ApprovalStatus;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.OrderId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderApprovalCommandHandlerImpl implements OrderApprovalCommandHandler {
    private final OrderApprovalRepository orderApprovalRepository;
    private final RestaurantOutboxRepository restaurantOutboxRepository;
    private final ZoneId UTC = ZoneId.of("UTC");
    @Value("${restaurant-service.kafka.restaurant-response-topic}")
    private String restaurantResponseTopic;
    public OrderApprovalCommandHandlerImpl(OrderApprovalRepository orderApprovalRepository,
                                                 RestaurantOutboxRepository restaurantOutboxRepository
                                                ) {
        this.orderApprovalRepository = orderApprovalRepository;
        this.restaurantOutboxRepository = restaurantOutboxRepository;
    }

    @Override
    @Transactional
    public void approveOrder(OrderApprovalCommand orderApprovalCommand) {
        OrderId orderId = new OrderId(UUID.fromString(orderApprovalCommand.getOrderId()));
        log.info("Processing manual approval for order id: {}", orderId.getValue());

        // 1. Tìm OrderApproval trong CSDL (Đã được tạo ở Step 3 - PENDING)
        OrderApproval orderApproval = orderApprovalRepository.findByOrderId(orderId.getValue())
                .orElseThrow(() -> new RestaurantApplicationServiceException(
                        "OrderApproval not found for order id: " + orderId.getValue()));


        // 3. Cập nhật trạng thái dựa trên Input
        if (orderApprovalCommand.getStatus() == ApprovalStatus.APPROVED) {
            if (orderApproval.getApprovalStatus() != ApprovalStatus.PENDING) {
                throw new RestaurantApplicationServiceException(
                        "Order is not in PENDING (UNPAID YET) state for approval! Current state: " + orderApproval.getApprovalStatus());
            }
            log.info("Order {} is APPROVED by restaurant.", orderId.getValue());
            orderApproval.approve();


            restaurantOutboxRepository.save(orderApproval.getDomainEvents().get(0), orderApproval.getOrderId().getValue(),restaurantResponseTopic);

        } else if (orderApprovalCommand.getStatus() == ApprovalStatus.REJECTED) {
            log.info("Order {} is REJECTED by restaurant.", orderId.getValue());
            List<String> failures = orderApprovalCommand.getFailureMessages() != null ?
                    List.of(orderApprovalCommand.getFailureMessages()) : Collections.emptyList();
            orderApproval.reject(failures);
            restaurantOutboxRepository.save(orderApproval.getDomainEvents().get(0), orderApproval.getOrderId().getValue(),restaurantResponseTopic);
        }

        // 4. Lưu trạng thái mới vào CSDL
        orderApprovalRepository.save(orderApproval);
    }


}