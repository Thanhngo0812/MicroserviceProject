package com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order;


import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * DTO: Lệnh gửi đến hệ thống để hủy một đơn hàng cụ thể.
 */
@Getter
@Builder
public class CancelOrderCommand {
    private final UUID orderId;
}