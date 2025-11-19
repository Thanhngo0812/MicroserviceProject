package com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.outputports;


import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Restaurant;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.RestaurantId;

import java.util.Optional;

/**
 * Output Port (Interface "sạch")
 * Yêu cầu CSDL: Lấy Aggregate Root 'Restaurant' (chứa Menu).
 */
public interface RestaurantRepository {

    /**
     * Tải (Load) Restaurant (bao gồm cả Products) bằng ID.
     * Implementation của hàm này (trong data-access) phải
     * Tải (Load) Restaurant VÀ danh sách Products của nó.
     */
    Optional<Restaurant> findRestaurantInformation(RestaurantId restaurantId);

    // (Lưu ý: Không có findProductById() ở đây,
    //  vì chúng ta phải đi qua Aggregate Root 'Restaurant')
}