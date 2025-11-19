package com.ct08SWA.restaurantservice.restaurantapplicationservice.mapper;


import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.RestaurantApprovalRequest;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderApproval;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderProduct;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.Money;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.OrderId;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.ProductId;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper (lớp Application) "sạch".
 * Chuyển đổi DTO -> Domain Entity.
 * (Không Lombok)
 */
@Component
public class RestaurantDataMapper {

    /**
     * Chuyển DTO (Input) sang Aggregate Root (Domain)
     */
    public OrderApproval restaurantApprovalRequestToOrderApproval(RestaurantApprovalRequest request) {

        // 1. Map danh sách Product (con)
        List<OrderProduct> orderProducts = request.getProducts().stream()
                .map(this::productRequestToOrderProduct)
                .collect(Collectors.toList());

        // 2. Tạo Aggregate Root (cha)
        return OrderApproval.builder()
                // (ID và Status sẽ được set bởi hàm initialize() trong Entity)
                .orderId(new OrderId(request.getOrderId()))
                .restaurantId(new RestaurantId(request.getRestaurantId()))
                .products(orderProducts)
                .build();
    }

    /**
     * Hàm helper: Map DTO (ProductRequest) sang Entity (OrderProduct)
     */
    private OrderProduct productRequestToOrderProduct(RestaurantApprovalRequest.ProductRequest productRequest) {
        return OrderProduct.builder()
                .id(new ProductId(productRequest.getId()))
                .quantity(productRequest.getQuantity())
                .price(new Money(productRequest.getPrice()))
                .build();
    }
}