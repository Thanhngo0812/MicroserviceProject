package com.ct08SWA.restaurantservice.restaurantdataaccess.repository;
import com.ct08SWA.restaurantservice.restaurantdataaccess.entity.OrderApprovalEntity;
import com.ct08SWA.restaurantservice.restaurantdataaccess.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderApprovalJpaRepository extends JpaRepository<OrderApprovalEntity, UUID> {

    // Truy vấn (Derived Query) để kiểm tra Idempotency
    Optional<OrderApprovalEntity> findByOrderId(UUID orderId);
}