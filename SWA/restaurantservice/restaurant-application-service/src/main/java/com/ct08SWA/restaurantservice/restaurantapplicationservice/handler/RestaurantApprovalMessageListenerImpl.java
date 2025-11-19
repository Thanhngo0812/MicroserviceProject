package com.ct08SWA.restaurantservice.restaurantapplicationservice.handler;


// Import Infrastructure (chỉ dùng ObjectMapper)
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderCancelledCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderPaidCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.RestaurantApprovalRequest;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.exception.RestaurantApplicationServiceException;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.mapper.RestaurantDataMapper;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports.RestaurantApprovalMessageListener;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports.OrderApprovalRepository;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports.RestaurantOutboxRepository;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports.RestaurantRepository;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderApproval;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Restaurant;
import com.ct08SWA.restaurantservice.restaurantdomaincore.service.RestaurantDomainService;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.ApprovalStatus;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.OrderId;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.RestaurantId;

// Import Logging (Pure Java)
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Đây là Handler (Use Case Implementation) "khôn" (smart).
 * Nó implement (triển khai) Input Port (RestaurantApprovalMessageListener).
 * Nó chứa logic nghiệp vụ của việc xử lý Duyệt đơn hàng (SAGA Step 3)
 * và tuân thủ Outbox Pattern (SAGA Step 4).
 * (Không Lombok)
 */
@Service
public class RestaurantApprovalMessageListenerImpl implements RestaurantApprovalMessageListener {

    // Dùng Logger (Pure Java)
    private static final Logger log = Logger.getLogger(RestaurantApprovalMessageListenerImpl.class.getName());

    private final RestaurantDomainService restaurantDomainService; // (File bên phải)
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final RestaurantOutboxRepository  restaurantOutboxRepository;
    // Inject Outbox
//    private final RestaurantOutboxRepository restaurantOutboxRepository;
    private final ZoneId UTC = ZoneId.of("UTC");
    @Value("${restaurant-service.kafka.restaurant-response-topic}")
    private String restaurantResponseTopic;
    // Constructor (Không Lombok)
    public RestaurantApprovalMessageListenerImpl(
            RestaurantDomainService restaurantDomainService,
            RestaurantDataMapper restaurantDataMapper,
            RestaurantRepository restaurantRepository,
            OrderApprovalRepository orderApprovalRepository,
            RestaurantOutboxRepository restaurantOutboxRepository
) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantDataMapper = restaurantDataMapper;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.restaurantOutboxRepository = restaurantOutboxRepository;
    }

    /**
     * Xử lý yêu cầu duyệt đơn hàng (SAGA Step 3).
     * Implement (triển khai) Outbox Pattern.
     */
    @Override
    @Transactional // Đảm bảo CSDL (OrderApproval và Outbox) là 1 Giao dịch (Transaction)
    public void processApprovalRequest(RestaurantApprovalRequest request) {
        String orderIdStr = request.getOrderId().toString();
        log.info("Processing restaurant approval request for order id: " + orderIdStr);
        List<String> failureMessages = new ArrayList<>();

        try {
            // 1. Kiểm tra Idempotency (Trùng lặp)
            if (isApprovalAlreadyProcessed(new OrderId(request.getOrderId()))) {
                log.warning("Approval for order id: " + orderIdStr + " already processed. Ignoring duplicate message.");
                return; // Bỏ qua
            }

            // 2. Map DTO (Input) -> Domain Entity (OrderApproval "sạch")
            OrderApproval orderApproval = restaurantDataMapper.restaurantApprovalRequestToOrderApproval(request);
            orderApproval.initialize(); // Set PENDING

            // 3. Lấy Restaurant (Aggregate Root "sạch") từ CSDL
            Restaurant restaurant = findRestaurant(orderApproval.getRestaurantId());

            // 4. Gọi Domain Service "sạch" (File bên phải)
            //    (Domain Service sẽ gọi restaurant.validateOrder() và trả về Event "phẳng")
            restaurantDomainService.validateOrder(
                    restaurant, orderApproval, failureMessages
            );

            // 5. LƯU CSDL (Atomic Transaction)
            // (Lưu trạng thái duyệt đơn: APPROVED hoặc REJECTED)
            orderApprovalRepository.save(orderApproval);
            if(!restaurant.getDomainEvents().isEmpty()){
                restaurantOutboxRepository.save(restaurant.getDomainEvents().get(0),orderApproval.getOrderId().getValue(),restaurantResponseTopic);
            }
//            // 6. GHI VÀO OUTBOX (SAGA Step 4)
//            // (Lấy tên Event (APPROVED/REJECTED) để dùng làm EventType)
//            String eventType = (event instanceof RestaurantApprovedEvent) ?
//                    "RestaurantApproved" : "RestaurantRejected";
//
//            saveEventToOutbox(event, eventType, orderApproval.getOrderId().getValue());

     //       log.info("Restaurant approval status (" + eventType + ") saved and Outbox message created for order id: " + orderIdStr);

        } catch (RestaurantApplicationServiceException e) {
            // Lỗi hệ thống (ví dụ: Không tìm thấy nhà hàng)
            log.severe("FATAL ERROR (Will Retry): " + e.getMessage());
            // Ném lỗi để Spring Kafka retry message
            throw e;
        } catch (Exception e) {
            // Lỗi hệ thống khác (ví dụ: CSDL sập, JSON serialize lỗi)
            log.severe("Unexpected error processing approval for order id: " + orderIdStr + ". Error: " + e.getMessage());
            // Ném lỗi để Spring Kafka retry message
            throw new RestaurantApplicationServiceException("Unexpected error during approval processing", e);
        }
    }

    @Override
    @Transactional
    public void processOrderCancelled(OrderCancelledCommand command) {
        OrderId orderId = new OrderId(command.getOrderId());
        log.info("Processing Order Cancelled event for order id: "+ orderId.getValue().toString());

        // 1. Tìm OrderApproval trong CSDL của RestaurantService
        Optional<OrderApproval> orderApprovalOptional = orderApprovalRepository.findByOrderId(orderId.getValue());

        // 2. Xử lý trường hợp KHÔNG TÌM THẤY (Yêu cầu của bạn)
        if (orderApprovalOptional.isEmpty()) {
            // Log ra để sau này xử lý Idempotency hoặc debug
            // (Có thể là lệnh Hủy đến trước lệnh Duyệt, hoặc đơn hàng chưa bao giờ được gửi tới Restaurant)
            log.warning("OrderApproval NOT FOUND for order id: "+orderId.getValue()+". This might be an idempotency issue or order was cancelled before reaching restaurant."
                    );
            return; // Kết thúc, không làm gì thêm
        }

        OrderApproval orderApproval = orderApprovalOptional.get();
        ApprovalStatus currentStatus = orderApproval.getApprovalStatus();

        // 3. Kiểm tra trạng thái hiện tại (Quy tắc của bạn)
        switch (currentStatus) {
            case PENDING:   // (Tương ứng WAITING/PENDING)
            case WAITING:
            case APPROVED:  // (Tương ứng APPROVED/PAID)
                // Đưa về CANCELLED (hoặc trạng thái tương đương bên Restaurant là REJECTED/CANCELLED)
                log.info("Order "+orderId.getValue()+" is currently "+currentStatus+". Cancelling approval...");

                orderApproval.cancel(java.util.Collections.singletonList("Order cancelled by User/System"));

                orderApprovalRepository.save(orderApproval);
                log.info("OrderApproval for order "+orderId.getValue()+" has been updated to REJECTED (Cancelled)." );
                break;

            case REJECTED:  // (Yêu cầu: REJECTED THÌ GIỮ NGUYÊN)
                log.info("Order CANCELLED MAYBE BECAUSE IT is already REJECTED. No action needed.");
                break;

            // (Nếu bạn thêm enum CANCELLED vào ApprovalStatus, thêm case ở đây)
            // case CANCELLED:
            //    log.info("Order {} is already CANCELLED.", orderId.getValue());
            //    break;
        }
    }

    @Override
    public void processOrderPaid(OrderPaidCommand command) {
        OrderId orderId = new OrderId(command.getOrderId());
        log.info("Processing Order PAID event for order id: "+ orderId.getValue().toString());

        // 1. Tìm OrderApproval trong CSDL của RestaurantService
        Optional<OrderApproval> orderApprovalOptional = orderApprovalRepository.findByOrderId(orderId.getValue());

        // 2. Xử lý trường hợp KHÔNG TÌM THẤY (Yêu cầu của bạn)
        if (orderApprovalOptional.isEmpty()) {
            // Log ra để sau này xử lý Idempotency hoặc debug
            // (Có thể là lệnh Hủy đến trước lệnh Duyệt, hoặc đơn hàng chưa bao giờ được gửi tới Restaurant)
            log.warning("OrderApproval NOT FOUND for order id: "+orderId.getValue()+". This might be an idempotency issue or order was cancelled before reaching restaurant."
            );
            return; // Kết thúc, không làm gì thêm
        }

        OrderApproval orderApproval = orderApprovalOptional.get();
        ApprovalStatus currentStatus = orderApproval.getApprovalStatus();

        // 3. Kiểm tra trạng thái hiện tại (Quy tắc của bạn)
        switch (currentStatus) {
            case WAITING:   // (Tương ứng WAITING/PENDING)
                log.info("Order "+orderId.getValue()+" is currently "+currentStatus+". PAID approval...");
                orderApproval.paid();
                orderApprovalRepository.save(orderApproval);
                log.info("OrderApproval for order "+orderId.getValue()+" has been updated to REJECTED (Cancelled)." );
                break;

            case REJECTED:  // (Yêu cầu: REJECTED THÌ GIỮ NGUYÊN)
                log.info("Order CANCELLED MAYBE BECAUSE IT is already REJECTED. No action needed.");
                break;
        }
    }

    // --- Các hàm helper (Tương tự PaymentService) ---

    /**
     * Helper: Kiểm tra Idempotency (Trùng lặp)
     */
    private boolean isApprovalAlreadyProcessed(OrderId orderId) {
        // Gọi Output Port
        Optional<OrderApproval> orderApproval = orderApprovalRepository.findByOrderId(orderId.getValue());
        return orderApproval.isPresent();
    }

    /**
     * Helper: Tải (Load) Aggregate Root 'Restaurant'
     */
    private Restaurant findRestaurant(RestaurantId restaurantId) {
        // Gọi Output Port
        Optional<Restaurant> restaurantOptional = restaurantRepository.findRestaurantInformation(restaurantId);
        if (restaurantOptional.isEmpty()) {
            String msg = "Restaurant with id: " + restaurantId.getValue() + " not found!";
            log.severe(msg);
            // Ném lỗi này sẽ khiến Kafka Retry (vì có thể Restaurant CSDL chưa đồng bộ)
            throw new RestaurantApplicationServiceException(msg);
        }
        return restaurantOptional.get();
    }

    /**
     * Hàm helper chung để GHI Event "phẳng" (POJO) vào Outbox.
     * (Đây là logic GHI (Write) của Outbox Pattern)
     */
//    private void saveEventToOutbox(RestaurantApprovalEvent event, String eventType, UUID sagaId) {
//        try {
//            // 1. Serialize (Tuần tự hóa) Event "phẳng" (POJO) -> JSON String
//            //    (Vì Event "phẳng" (file bên phải) là POJO, Jackson tự hiểu)
//            String payload = objectMapper.writeValueAsString(event);
//
//            // 2. Tạo Domain Entity "sạch" cho Outbox
//            RestaurantOutbox outboxMessage = RestaurantOutbox.builder()
//                    .id(UUID.randomUUID())
//                    .sagaId(sagaId)
//                    .createdAt(event.getCreatedAt())
//                    .eventType(eventType)
//                    .payload(payload) // JSON String (CSDL phải là TEXT/JSONB)
//                    .status(OutboxStatus.PENDING)
//                    .build();
//
//            // 3. Gọi Output Port (CSDL)
//            restaurantOutboxRepository.save(outboxMessage);
//            log.info("Saved [" + eventType + "] message to Outbox for saga id: " + sagaId);
//
//        } catch (Exception e) {
//            String msg = "Failed to save [" + eventType + "] message to Outbox for saga id: " + sagaId;
//            log.severe(msg + ". Error: " + e.getMessage());
//            // Ném lỗi để Transaction (bên trên) rollback
//            throw new RestaurantApplicationServiceException(msg, e);
//        }
//    }
}