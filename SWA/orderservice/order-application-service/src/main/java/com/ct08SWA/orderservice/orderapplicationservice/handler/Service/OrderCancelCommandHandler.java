package com.ct08SWA.orderservice.orderapplicationservice.handler.Service;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CancelOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderOutboxRepository;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderRepository;
import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.exception.OrderDomainException;
import com.ct08SWA.orderservice.orderdomaincore.service.OrderDomainService;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderId;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

@Slf4j
@Component
public class OrderCancelCommandHandler {

    private final OrderRepository orderRepository;
    private final OrderOutboxRepository  orderOutboxRepository;
    private final OrderDomainService orderDomainService;
    @Value("${order-service.kafka.order-cancel-topic}")
    private String OrderCancelEventTopic;
    public OrderCancelCommandHandler(OrderRepository orderRepository, OrderOutboxRepository orderOutboxRepository,OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderOutboxRepository = orderOutboxRepository;
        this.orderDomainService = orderDomainService;
    }

    /**
     * Xử lý việc hủy đơn hàng do người dùng chủ động.
     * Logic kiểm tra trạng thái và quyết định có cần gửi lệnh hoàn tiền không.
     */
    @Transactional
    public void cancelOrder(CancelOrderCommand command) {
        OrderId orderId = new OrderId(command.getOrderId());
        Order order = findOrder(orderId);
        OrderStatus currentStatus = order.getOrderStatus();
        if (currentStatus == OrderStatus.PENDING||currentStatus == OrderStatus.PAYMENT_FAILED) {
            // Kịch bản A: Hủy an toàn (chưa trừ tiền)
            log.info("Cancelling order {} safely. Status is PENDING.", orderId.getValue());
            orderDomainService.cancelOrder(order,Collections.singletonList("User cancelled order while pending payment."));
            orderOutboxRepository.save(order.getDomainEvents().get(0),order.getId().getValue(), OrderCancelEventTopic);
            orderRepository.save(order);
        } else if (currentStatus == OrderStatus.PAID) {
            log.info("Compensation required for order {}. Status is PAID.", orderId.getValue());
            orderDomainService.initiateCancel(order,Collections.singletonList("User initiated cancellation after payment."));
            orderRepository.save(order);
            orderOutboxRepository.save(order.getDomainEvents().get(0),order.getId().getValue(),OrderCancelEventTopic);
            log.info("OrderOutbox message saved for saga id: {}",order.getId().getValue());
            log.info("Compensation request sent to PaymentService for order: {}", orderId.getValue());
        } else if (currentStatus == OrderStatus.CANCELLING || currentStatus == OrderStatus.APPROVED) {
            log.warn("Cannot cancel order {} because its status is {}.", orderId.getValue(), currentStatus);
            throw new OrderDomainException("Order is in an un-cancellable state: " + currentStatus);
        }
        // Các trạng thái kết thúc khác (PAYMENT_FAILED, CANCELLED, REFUNDED) cũng sẽ bị từ chối
    }

    /**
     * Hàm helper để tìm Order, ném exception nếu không thấy.
     */
    private Order findOrder(OrderId orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderDomainException("Order not found for id: " + orderId.getValue()));
    }


}