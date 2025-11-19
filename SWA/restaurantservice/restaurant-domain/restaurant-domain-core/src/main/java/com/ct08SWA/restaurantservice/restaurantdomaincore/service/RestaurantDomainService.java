package com.ct08SWA.restaurantservice.restaurantdomaincore.service;


import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderApproval;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Restaurant;

import java.util.List;

/**
 * Interface cho Domain Service "sạch".
 * Định nghĩa logic nghiệp vụ phức tạp (điều phối nhiều Entity/Aggregate).
 */
public interface RestaurantDomainService {

    /**
     * Logic nghiệp vụ chính: Validate đơn hàng dựa trên thông tin nhà hàng.
     * Sẽ trả về Event (POJO "phẳng") tương ứng.
     *
     * @param restaurant Aggregate Root (chứa Menu)
     * @param orderApproval Aggregate Root (chứa Order)
     * @param failureMessages Danh sách (rỗng) để điền lỗi
     * @return RestaurantApprovalEvent (POJO "phẳng" - Approved hoặc Rejected)
     */
    void validateOrder(Restaurant restaurant,
                                          OrderApproval orderApproval,
                                          List<String> failureMessages);
}