package com.ct08SWA.orderservice.orderapplicationservice.mapper;


import java.util.List;
import java.util.UUID;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.*;
import org.springframework.stereotype.Component;
import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.ouputdto.Order.OrderCreatedResponse;
import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdomaincore.entity.OrderItem;
import com.ct08SWA.orderservice.orderdomaincore.entity.Product;

@Component
public class OrderDataMapper {

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.customerId()))
                .restaurantId(new RestaurantId(createOrderCommand.restaurantId()))
                .price(new Money(createOrderCommand.price()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.address()))
                .items(orderItemsToOrderItemEntities(createOrderCommand.items()))
                .build();
    }

    public OrderCreatedResponse orderToCreateOrderResponse(UUID TrackingId) {
        return new OrderCreatedResponse(
                TrackingId,
           OrderStatus.PENDING
        );
    }
    private StreetAddress orderAddressToStreetAddress(CreateOrderCommand.OrderAddress address) {
        return new StreetAddress(
                address.street(),
                address.postalCode(),
                address.city()
        );
    }

    private List<OrderItem> orderItemsToOrderItemEntities(
            List<CreateOrderCommand.OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> 
                    OrderItem.builder()
                        .product(new Product(
                                new ProductId(orderItem.productId()),
                                null, // Name not available in command
                                new Money(orderItem.price())
                        ))
                        .price(new Money(orderItem.price()))
                        .quantity(orderItem.quantity())
                        .subTotal(new Money(orderItem.subTotal()))
                        .build()
                )
                .toList();
    }


}