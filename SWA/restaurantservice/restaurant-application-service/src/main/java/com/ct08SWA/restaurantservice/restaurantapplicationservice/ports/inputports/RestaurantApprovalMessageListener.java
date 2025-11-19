package com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports;

import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderCancelledCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderPaidCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.RestaurantApprovalRequest;

/**
 * Input Port (Use Case Interface) "sạch".
 * Định nghĩa hành vi xử lý Yêu cầu Duyệt đơn hàng.
 */
public interface RestaurantApprovalMessageListener {

    /**
     * Xử lý logic nghiệp vụ duyệt đơn hàng (SAGA Step 3).
     * @param request DTO "sạch" chứa thông tin đơn hàng.
     */
    void processApprovalRequest(RestaurantApprovalRequest request);
    void processOrderCancelled(OrderCancelledCommand command);
    void processOrderPaid(OrderPaidCommand command);
}