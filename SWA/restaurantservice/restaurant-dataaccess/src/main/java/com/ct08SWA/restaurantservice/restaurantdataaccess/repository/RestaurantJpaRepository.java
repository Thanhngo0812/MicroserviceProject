package com.ct08SWA.restaurantservice.restaurantdataaccess.repository;

import com.ct08SWA.restaurantservice.restaurantdataaccess.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, UUID> {

    /**
     * Tùy chỉnh (Custom) Query:
     * Lấy Restaurant VÀ "JOIN FETCH" (tải ngay lập tức) danh sách Products
     * để tránh lỗi N+1 Query.
     */
    @Query("SELECT r FROM RestaurantEntity r LEFT JOIN FETCH r.products WHERE r.id = :restaurantId")
    Optional<RestaurantEntity> findRestaurantInformation(UUID restaurantId);
}