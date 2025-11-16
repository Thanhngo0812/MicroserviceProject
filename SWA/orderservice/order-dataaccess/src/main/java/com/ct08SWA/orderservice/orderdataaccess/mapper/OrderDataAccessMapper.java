package com.ct08SWA.orderservice.orderdataaccess.mapper;


import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderEntity;
import com.ct08SWA.orderservice.orderdomaincore.entity.OrderItem;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderItemEntity;
import com.ct08SWA.orderservice.orderdataaccess.entity.OrderStatus;
import com.ct08SWA.orderservice.orderdomaincore.entity.Product;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.*;


@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        if (order == null) {
            return null;
        }

        var orderEntity = OrderEntity.builder()
                .id(order.getId() != null ? order.getId().getValue() : null)
                .customerId(order.getCustomerId() != null ? order.getCustomerId().getValue() : null)
                .restaurantId(order.getRestaurantId() != null ? order.getRestaurantId().getValue() : null)
                .trackingId(order.getTrackingId() != null ? order.getTrackingId().getValue() : null)
                .price(order.getPrice() != null ? order.getPrice().getAmount() : null)
                .orderStatus(mapOrderStatus(order.getOrderStatus()))
                .failureMessages(   order.getFailureMessages() != null ?
                        order.getFailureMessages().stream()
                                .filter(Objects::nonNull)      // loại null
                                .filter(s -> !s.isBlank())     // loại chuỗi rỗng
                                .collect(Collectors.joining(","))
                        : null)
                .createdAt(java.time.ZonedDateTime.now())
                .build();

        // Map order items
        if (order.getItems() != null) {
            List<OrderItemEntity> orderItemEntities = order.getItems().stream()
                    .map(orderItem -> orderItemToOrderItemEntity(orderItem, orderEntity))
                    .collect(Collectors.toList());
            orderEntity.setItems(orderItemEntities);
        }

        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return null;
        }

        var orderBuilder = Order.builder()
                .orderId(orderEntity.getId() != null ? new OrderId(orderEntity.getId()) : null)
                .customerId(orderEntity.getCustomerId() != null ? 
                    new CustomerId(orderEntity.getCustomerId()) : null)
                .restaurantId(orderEntity.getRestaurantId() != null ? 
                    new RestaurantId(orderEntity.getRestaurantId()) : null)
                .price(orderEntity.getPrice() != null ? 
                    new Money(orderEntity.getPrice()) : null)
                .trackingId(orderEntity.getTrackingId() != null ? 
                    new TrackingId(orderEntity.getTrackingId()) : null)
                .orderStatus(mapOrderStatus(orderEntity.getOrderStatus()));

        // Map delivery address (tạm thời dùng giá trị mặc định)
        orderBuilder.deliveryAddress(new StreetAddress("", "", ""));

        // Map order items
        if (orderEntity.getItems() != null) {
            List<OrderItem> orderItems = orderItemEntitiesToOrderItems(orderEntity.getItems());
            orderBuilder.items(orderItems);
        }

        // Map failure messages
        if (orderEntity.getFailureMessages() != null) {
            List<String> failureMessages = List.of(orderEntity.getFailureMessages().split(","));
            orderBuilder.failureMessages(failureMessages);
        } else {
            orderBuilder.failureMessages(new ArrayList<>());
        }

        return orderBuilder.build();
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderItem> orderItems = new ArrayList<>();
        long ordernum=1;
        for (OrderItemEntity orderItemEntity : items) {
            // Validate data
            if (orderItemEntity.getProductId() == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (orderItemEntity.getPrice() == null) {
                throw new IllegalArgumentException("Price cannot be null");
            }
            if (orderItemEntity.getSubTotal() == null) {
                throw new IllegalArgumentException("SubTotal cannot be null");
            }
            if (orderItemEntity.getQuantity() == null || orderItemEntity.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }

            // Create product
            Product product = new Product(
                new ProductId(orderItemEntity.getProductId()),
                "Product-" + orderItemEntity.getProductId(),
                new Money(orderItemEntity.getPrice())
            );

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                .product(product)
                .price(new Money(orderItemEntity.getPrice()))
                .quantity(orderItemEntity.getQuantity())
                .subTotal(new Money(orderItemEntity.getSubTotal()))
                .build();
            orderItem.setId(ordernum);
            ordernum++;
            orderItems.add(orderItem);
        }
        
        return orderItems;
    }
    
    private OrderItemEntity orderItemToOrderItemEntity(OrderItem orderItem, OrderEntity orderEntity) {
        return OrderItemEntity.builder()
                .id(orderItem.getId())
                .order(orderEntity)
                .productId(orderItem.getProduct().getId().getValue())
                .price(orderItem.getPrice().getAmount())
                .quantity(orderItem.getQuantity())
                .subTotal(orderItem.getSubTotal().getAmount())
                .build();
    }

    private OrderStatus mapOrderStatus(com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }
        return OrderStatus.valueOf(orderStatus.name());
    }

    private com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderStatus mapOrderStatus(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }
        return com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderStatus.valueOf(orderStatus.name());
    }
}