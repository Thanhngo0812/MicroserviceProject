package com.ct08SWA.orderservice.orderdomaincore.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.entity.OrderItem;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderCancelledEvent;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderCreatedEvent;
import com.ct08SWA.orderservice.orderdomaincore.event.OrderPaidEvent;

import static java.time.ZoneOffset.UTC;


public class OrderDomainServiceImpl implements OrderDomainService {

        @Override
        public void validateAndInitiateOrder(Order order) {
            // 1. Validate order
            order.validateOrder();

            // 2. Initialize order (set IDs, status PENDING)
            order.initializeOrder();

            // 3. Convert OrderItems sang OrderItemData (DTO phẳng)
            List<OrderCreatedEvent.OrderItemData> itemsData = order.getItems().stream()
                    .map(this::convertToItemData)
                    .collect(Collectors.toList());

            // 4. Tạo Event từ Order Entity
            OrderCreatedEvent event = new OrderCreatedEvent(
                    order.getId().getValue(),
                    order.getCustomerId().getValue(),
                    order.getRestaurantId().getValue(),
                    order.getPrice().getAmount(),
                    ZonedDateTime.now(UTC),
                    itemsData,
                    order.getTrackingId().getValue()
            );

            // 5. Thêm event vào Aggregate Root (để publish sau)
            order.addDomainEvent(event);
        }


    @Override
    public void payOrder(Order order) {
        order.pay();
        OrderPaidEvent event = new OrderPaidEvent(order.getId().getValue(),
                order.getCustomerId().getValue(),
                order.getRestaurantId().getValue(),
                order.getPrice().getAmount(),
                ZonedDateTime.now(UTC),
                order.getTrackingId().getValue());
        order.addDomainEvent(event);
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
    }

    @Override
    public void cancelOrder(Order order, java.util.List<String> failureMessages) {
        order.cancel(failureMessages);
        OrderCancelledEvent event = new OrderCancelledEvent(order.getId().getValue(),ZonedDateTime.now(UTC),order.getPrice().getAmount(),order.getCustomerId().getValue());
        order.addDomainEvent(event);
    }

    @Override
    public void initiateCancel(Order order, java.util.List<String> failureMessages) {
        order.initCancel(failureMessages);
        OrderCancelledEvent event = new OrderCancelledEvent(order.getId().getValue(),ZonedDateTime.now(UTC),order.getPrice().getAmount(),order.getCustomerId().getValue());
        order.addDomainEvent(event);
    }

    private OrderCreatedEvent.OrderItemData convertToItemData(OrderItem item) {
        return new OrderCreatedEvent.OrderItemData(
                item.getProduct().getId().getValue(),
                item.getQuantity(),
                item.getPrice().getAmount(),
                item.getSubTotal().getAmount()
        );
    }
}