package com.ct08SWA.restaurantservice.restaurantmessaging.mapper;

import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderCancelledCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderPaidCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.RestaurantApprovalRequest;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.OrderCancelledEventDto;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.OrderCreatedEventDto;
import com.ct08SWA.restaurantservice.restaurantmessaging.dto.OrderPaidEventDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class RestaurantDataMessagingMapper {


    public  RestaurantApprovalRequest toRestaurantApprovalRequest(OrderCreatedEventDto eventDto) {
        if (eventDto == null) {
            throw new IllegalArgumentException("OrderCreatedEventDto không được null");
        }

        // Map danh sách OrderItemDto -> ProductRequest
        List<RestaurantApprovalRequest.ProductRequest> products = eventDto.getItems()
                .stream()
                .map(RestaurantDataMessagingMapper::toProductRequest)
                .collect(Collectors.toList());

        // Build RestaurantApprovalRequest
        return RestaurantApprovalRequest.builder()
                .orderId(eventDto.getOrderId())
                .restaurantId(eventDto.getRestaurantId())
                .price(eventDto.getPrice())
                .createdAt(eventDto.getCreatedAt())
                .products(products)
                .build();
    }
    public OrderCancelledCommand toOrderCancelledCommand(OrderCancelledEventDto dto) {
        if (dto == null) {
            return null;
        }

        return new OrderCancelledCommand(
                dto.getOrderId(),
                dto.getCreatedAt(),
                dto.getPrice(),
                dto.getCustomerId()
        );
    }

    public OrderPaidCommand toOrderPaidCommand(OrderPaidEventDto dto) {
        if (dto == null) {
            return null;
        }

        return new OrderPaidCommand(
                dto.getOrderId(),
                dto.getCustomerId(),
                dto.getRestaurantId(),
                dto.getPrice(),
                dto.getCreatedAt(),
                dto.getTrackingId()
        );
    }
    /**
     * Map từ OrderItemDto sang ProductRequest
     *
     * @param itemDto Item từ event
     * @return ProductRequest
     */
    private static RestaurantApprovalRequest.ProductRequest toProductRequest(
            OrderCreatedEventDto.OrderItemDto itemDto) {

        if (itemDto == null) {
            throw new IllegalArgumentException("OrderItemDto không được null");
        }

        return RestaurantApprovalRequest.ProductRequest.builder()
                .id(itemDto.getProductId())
                .quantity(itemDto.getQuantity())
                .price(itemDto.getPrice())
                .build();
    }
}
