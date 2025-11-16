package com.ct08SWA.orderservice.orderdataaccess.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ct08SWA.orderservice.orderdataaccess.entity.OrderEntity;

import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}
