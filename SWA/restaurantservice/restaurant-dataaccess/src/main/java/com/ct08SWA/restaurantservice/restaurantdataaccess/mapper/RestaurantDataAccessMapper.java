package com.ct08SWA.restaurantservice.restaurantdataaccess.mapper;


import com.ct08SWA.restaurantservice.restaurantdataaccess.entity.OrderApprovalEntity;
import com.ct08SWA.restaurantservice.restaurantdataaccess.entity.ProductEntity;
import com.ct08SWA.restaurantservice.restaurantdataaccess.entity.RestaurantEntity;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderApproval;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Product;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Restaurant;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper (Adapter): "Dịch" (Map) giữa Domain Entities (sạch)
 * và JPA Entities (bẩn).
 * (Không Lombok)
 */
@Component
public class RestaurantDataAccessMapper {

    // --- Restaurant / Product Mapping ---
    // (Lưu ý: Đây là map phức tạp - 2 chiều - OneToMany)

    /**
     * Chuyển từ JPA Entity (CSDL) sang Domain Entity (Logic "sạch")
     * (Đây là hàm quan trọng nhất)
     */
    public Restaurant restaurantEntityToRestaurant(RestaurantEntity entity) {
        if (entity == null) return null;

        // 1. Chuyển List<ProductEntity> (bẩn) -> List<Product> (sạch)
        List<Product> domainProducts = entity.getProducts().stream()
                .map(this::productEntityToProduct)
                .collect(Collectors.toList());

        // 2. Tạo Domain Entity "sạch"
        return Restaurant.builder()
                .id(new RestaurantId(entity.getId()))
                .name(entity.getName())
                .active(entity.isActive())
                .products(domainProducts)
                .build();
    }

    /**
     * Hàm helper: Chuyển ProductEntity -> Product
     */
    private Product productEntityToProduct(ProductEntity entity) {
        return Product.builder()
                .id(new ProductId(entity.getId()))
                .name(entity.getName())
                .price(new Money(entity.getPrice()))
                .available(entity.isAvailable())
                .build();
    }

    // (Lưu ý: Chúng ta thường không cần map ngược từ Restaurant (Domain)
    //  sang RestaurantEntity (JPA) vì CSDL (Menu) thường được
    //  nhập liệu/quản lý riêng, không phải do Service tạo ra).


    // --- OrderApproval Mapping ---

    /**
     * Chuyển từ Domain Entity (Logic) sang JPA Entity (CSDL)
     */
    public OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval domain) {
        if (domain == null) return null;

        OrderApprovalEntity entity = new OrderApprovalEntity();
        entity.setId(domain.getId().getValue());
        entity.setRestaurantId(domain.getRestaurantId().getValue());
        entity.setOrderId(domain.getOrderId().getValue());
        entity.setStatus(domain.getApprovalStatus());
        entity.setFailureMessages(domain.getFailureMessages() != null ?
                domain.getFailureMessages().stream()
                        .filter(Objects::nonNull)      // loại null
                        .filter(s -> !s.isBlank())     // loại chuỗi rỗng
                        .collect(Collectors.joining(","))
                : null);
        entity.setWarnings(domain.getWarnings() != null ?
                domain.getWarnings().stream()
                        .filter(Objects::nonNull)      // loại null
                        .filter(s -> !s.isBlank())     // loại chuỗi rỗng
                        .collect(Collectors.joining(","))
                : null);
        entity.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC"))); // Gán thời gian
        return entity;
    }

    /**
     * Chuyển từ JPA Entity (CSDL) sang Domain Entity (Logic)
     */
    public OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity entity) {
        if (entity == null) return null;

        // (Lưu ý: Domain 'OrderApproval' không cần List<Product> khi tải lại,
        //  vì nó chỉ dùng để kiểm tra Idempotency. Nếu cần, chúng ta phải map
        //  ngược lại List<OrderProduct> từ CSDL (nếu chúng ta lưu chúng))
        var orderApprovalBuilder = OrderApproval.builder().id(new OrderApprovalId(entity.getId()))
                .restaurantId(new RestaurantId(entity.getRestaurantId()))
                .orderId(new OrderId(entity.getOrderId()))
                .approvalStatus(entity.getStatus());
        if (entity.getFailureMessages() != null) {
            List<String> failureMessages = List.of(entity.getFailureMessages().split(","));
            orderApprovalBuilder.failureMessages(failureMessages);
        } else {
            orderApprovalBuilder.failureMessages(new ArrayList<>());
        }
        return orderApprovalBuilder.build();
    }
}