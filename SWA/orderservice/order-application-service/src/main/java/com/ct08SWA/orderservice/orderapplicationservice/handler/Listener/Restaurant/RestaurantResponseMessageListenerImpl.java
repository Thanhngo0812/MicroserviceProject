package com.ct08SWA.orderservice.orderapplicationservice.handler.Listener.Restaurant;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Restaurant.RestaurantResponse;
import com.ct08SWA.orderservice.orderapplicationservice.exception.OrderDomainException;
import com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Listener.RestaurantResponseMessageListener;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderOutboxRepository;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderRepository;
import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.service.OrderDomainService;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service // Đánh dấu là một Spring Bean
public class RestaurantResponseMessageListenerImpl implements RestaurantResponseMessageListener {
    private OrderRepository orderRepository;
    private OrderDomainService orderDomainService;
    private OrderOutboxRepository orderOutboxRepository;
    @Value("${order-service.kafka.order-state-topic}")
    private String OrderCancelEventTopic;
    public RestaurantResponseMessageListenerImpl(OrderRepository orderRepository,OrderDomainService orderDomainService, OrderOutboxRepository orderOutboxRepository) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.orderOutboxRepository = orderOutboxRepository;
    }

    @Override
    public void processApproved(RestaurantResponse restaurantResponse) {
        Order order = findOrder(restaurantResponse.getOrderId());
        OrderStatus currentStatus = order.getOrderStatus();

        log.info("Processing APPROVED signal for Order: {} (Current Status: {})",
                order.getId().getValue(), currentStatus);

        switch (currentStatus) {
            case PAID:
                // HAPPY PATH: Paid -> Approved
                orderDomainService.approveOrder(order);
                orderRepository.save(order);
                log.info("Order {} status updated to APPROVED.", order.getId().getValue());
                break;

            case PENDING:
                // LỖI LOGIC: Chưa thanh toán mà đã duyệt?
                log.error("LOGIC ERROR: Order {} is PENDING but received Restaurant APPROVED signal. Ignoring.",
                        order.getId().getValue());
                break;

            case CANCELLING:
            case CANCELLED:
                // RACE CONDITION: Đã hủy rồi thì bỏ qua lệnh duyệt
                log.warn("Order {} is already {}. Ignoring Restaurant APPROVED signal.",
                        order.getId().getValue(), currentStatus);
                break;

            case APPROVED:
                log.info("Order {} is already APPROVED. Ignoring duplicate message.", order.getId().getValue());
                break;

            default:
                log.warn("Order {} status is {}. No action taken for APPROVED signal.", order.getId().getValue(), currentStatus);
        }
    }

    @Override
    public void processCancelled(RestaurantResponse restaurantResponse) {
        Order order = findOrder(restaurantResponse.getOrderId());
        OrderStatus currentStatus = order.getOrderStatus();

        log.info("Processing REJECTED signal for Order: {} (Current Status: {})",
                order.getId().getValue(), currentStatus);

        switch (currentStatus) {
            case PAID:
                // SAGA COMPENSATION: Đã trả tiền -> Phải hoàn tiền
                log.info("Order {} is PAID but Rejected by Restaurant. Starting Compensation (Refund).", order.getId().getValue());

                // 1. Chuyển sang CANCELLING (Chờ hoàn tiền)
                orderDomainService.initiateCancel(order,restaurantResponse.getFailureMessages());
                orderRepository.save(order);
                orderOutboxRepository.save(order.getDomainEvents().get(0),order.getId().getValue(),OrderCancelEventTopic );
                break;

            case PENDING:
            case APPROVED: // Giả sử cho phép hủy sau khi approve (hiếm gặp)
                // Hủy ngay lập tức (Tiền chưa trừ hoặc không cần hoàn tiền theo luồng này)
                log.info("Order {} is {}. Cancelling immediately due to Restaurant Rejection.",
                        order.getId().getValue(), currentStatus);

                orderDomainService.cancelOrder(order,restaurantResponse.getFailureMessages());
                orderRepository.save(order);
                orderOutboxRepository.save(order.getDomainEvents().get(0),order.getId().getValue(),OrderCancelEventTopic );
                break;

            case CANCELLING:
            case CANCELLED:
                log.warn("Order {} is already {}. Ignoring Restaurant REJECTED signal.",
                        order.getId().getValue(), currentStatus);
                break;
        }

    }

    private Order findOrder(UUID orderId) {
        return orderRepository.findById(new com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderId(orderId))
                .orElseThrow(() -> new OrderDomainException("Order not found: " + orderId));
    }
}
