package com.ct08SWA.restaurantservice.restaurantdataaccess.repository;

import com.ct08SWA.restaurantservice.restaurantdataaccess.entity.RestaurantOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository cho PaymentOutboxEntity.
 */
@Repository
public interface RestaurantOutboxJpaRepository extends JpaRepository<RestaurantOutboxEntity, UUID> {
}
