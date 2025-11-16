package com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order;
//order-application-service/src/main/java/com/ct08swa/orderservice/applicationservice/dto/create/CreateOrderCommand.java

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
    @NotNull UUID customerId,
    @NotNull UUID restaurantId,
    @NotNull BigDecimal price,
    @NotNull List<OrderItem> items,
    @NotNull OrderAddress address
) {
    public record OrderItem(
        @NotNull UUID productId,
        @NotNull Integer quantity,
        @NotNull BigDecimal price,
        @NotNull BigDecimal subTotal
    ) {}

    public record OrderAddress(
        @NotNull String street,
        @NotNull String postalCode,
        @NotNull String city
    ) {}
}