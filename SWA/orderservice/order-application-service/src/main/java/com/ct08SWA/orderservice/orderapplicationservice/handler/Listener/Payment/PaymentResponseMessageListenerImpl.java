package com.ct08SWA.orderservice.orderapplicationservice.handler.Listener.Payment;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Payment.PaymentResponse;
import com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Listener.PaymentResponseMessageListener;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderOutboxRepository;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderRepository;
import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.exception.OrderDomainException;
import com.ct08SWA.orderservice.orderdomaincore.service.OrderDomainService;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderId;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service // Đánh dấu là một Spring Bean
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;
    private final OrderDomainService orderDomainService;
    @Value("${order-service.kafka.order-cancel-topic}")
    private String OrderCancelEventTopic;
    @Value("${order-service.kafka.order-paid-topic}")
    private String OrderPaidEventTopic;
    public PaymentResponseMessageListenerImpl(OrderRepository orderRepository
                                              ,OrderOutboxRepository orderOutboxRepository,OrderDomainService orderDomainService
    ) {
        this.orderRepository = orderRepository;
        this.orderOutboxRepository = orderOutboxRepository;
        this.orderDomainService = orderDomainService;
    }

    /**
     * Xử lý khi thanh toán THÀNH CÔNG (SAGA Step 2 -> 3).
     * @param paymentResponse DTO chứa thông tin phản hồi từ Payment Service.
     */
    @Override
    @Transactional // Đảm bảo việc cập nhật DB là atomic
    public void paymentCompleted(PaymentResponse paymentResponse) {
        log.info("Received PaymentCompletedEvent for order id: {}", paymentResponse.getOrderId());
        Order order = findOrder(paymentResponse.getOrderId());
        // Sử dụng switch-case (hoặc if-else) để xử lý
        OrderStatus currentStatus = order.getOrderStatus();

        switch (currentStatus) {
            case PENDING:
                // KỊCH BẢN 1: HAPPY PATH (Luồng 1)
                // Order đang PENDING, Payment thành công.
                log.info("Happy Path: Order status is PENDING. Updating to PAID.");
                orderDomainService.payOrder(order);
                Order savedOrder = orderRepository.save(order);
                orderOutboxRepository.save(order.getDomainEvents().get(0),order.getId().getValue(),OrderPaidEventTopic );

                // Publish Event 3 (Yêu cầu Restaurant duyệt đơn)
//                OrderPaidEvent orderPaidEvent = new OrderPaidEvent(savedOrder, ZonedDateTime.now(ZoneId.of("UTC")));
//                restaurantApprovalRequestPublisher.publish(orderPaidEvent);
                log.info("Published OrderPaidEvent to trigger restaurant approval for order id: {}", savedOrder.getId().getValue());
                break;

            case CANCELLED:
            case PAYMENT_FAILED:
                // KỊCH BẢN 2: RACE CONDITION (User/Hệ thống đã Hủy)
                // Payment thành công, nhưng Order đã bị Hủy (ví dụ: User tự hủy lúc T=5s)
                log.warn("COMPENSATION TRIGGERED: Payment completed for order id: {} but Order is already CANCELLED/FAILED.",
                        paymentResponse.getOrderId());
                break;

            case CANCELLING:
                log.warn("IGNORING LATE MESSAGE: Received PaymentCompletedEvent for order id: {}, but Order is already {}. " +
                                "Compensation is already in progress. No action needed.",
                        paymentResponse.getOrderId(), currentStatus);
                break;

            case PAID:
            case APPROVED:
                log.warn("IGNORING DUPLICATE MESSAGE: Received PaymentCompletedEvent for order id: {}, but Order is already PAID/APPROVED.",
                        paymentResponse.getOrderId());
                // Bỏ qua (Ignore)
                break;
        }
//        // 1. Cập nhật trạng thái Order
//        order.pay(); // Logic nghiệp vụ: chuyển sang PAID
//
//        // 2. Lưu trạng thái mới vào DB
//        Order savedOrder = orderRepository.save(order);
//        log.info("Order status updated to PAID for order id: {}", savedOrder.getId().getValue());
//
//        // 3. Gửi Event tiếp theo (Trigger cho Restaurant Service)
//        // OrderService điều phối SAGA: Payment xong -> Gửi yêu cầu duyệt đơn
////        OrderPaidEvent orderPaidEvent = new OrderPaidEvent(savedOrder); // Tạo Event PAID
////        orderPaidRequestPublisher.publish(orderPaidEvent); // Output Port gửi lên Kafka
//
//        log.info("Published OrderPaidEvent to trigger restaurant approval for order id: {}", savedOrder.getId().getValue());
    }

    /**
     * Xử lý khi thanh toán THẤT BẠI (SAGA Compensation).
     * @param paymentResponse DTO chứa thông tin phản hồi từ Payment Service.
     */
    @Override
    @Transactional
    public void paymentFailed(PaymentResponse paymentResponse) {
        log.warn("Received PaymentFailedEvent for order id: {}. Reason: {}",
                paymentResponse.getOrderId(),
                String.join(",", paymentResponse.getFailureMessages()));

        Order order = findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order,paymentResponse.getFailureMessages());
        orderRepository.save(order);
        orderOutboxRepository.save(order.getDomainEvents().get(0),order.getId().getValue(),OrderCancelEventTopic);
        log.info("Order status updated to CANCELLED for order id: {}", order.getId().getValue());

        // KHÔNG cần gửi event tiếp theo vì SAGA kết thúc.
    }

    /**
     * Xử lý khi hoàn tiền THÀNH CÔNG (sau compensation).
     * @param paymentResponse DTO chứa thông tin phản hồi từ Payment Service.
     */
    @Override
    @Transactional
    public void paymentCancelled(PaymentResponse paymentResponse) {
        // Logic tương tự paymentFailed, nhưng dành cho luồng compensation (hoàn tiền)
        log.info("Received PaymentCancelledEvent for order id: {}", paymentResponse.getOrderId());

        Order order = findOrder(paymentResponse.getOrderId());
        // 1. Cập nhật trạng thái Order
        // Sử dụng cancel vì đây là sự thất bại từ bước duyệt đơn hàng
        order.cancel(paymentResponse.getFailureMessages()); // Logic nghiệp vụ: chuyển sang CANCELLED
        orderRepository.save(order);
        log.info("Order status updated to CANCELLED (Compensation Successful) for order id: {}", order.getId().getValue());
    }


    /**
     * Hàm helper tìm kiếm Order theo ID.
     * @param orderId UUID của Order.
     * @return Order entity.
     * @throws OrderDomainException nếu không tìm thấy Order.
     */
    private Order findOrder(UUID orderId) {
        Optional<Order> orderOptional = orderRepository.findById(new OrderId(orderId));
        if (orderOptional.isEmpty()) {
            log.error("Order with id: {} not found!", orderId);
            throw new OrderDomainException("Order with id: " + orderId + " not found!");
        }
        return orderOptional.get();
    }

}