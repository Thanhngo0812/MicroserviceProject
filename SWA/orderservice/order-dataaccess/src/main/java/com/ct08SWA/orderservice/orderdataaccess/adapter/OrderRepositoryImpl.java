package com.ct08SWA.orderservice.orderdataaccess.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ct08SWA.orderservice.orderdomaincore.entity.Order;
import com.ct08SWA.orderservice.orderdataaccess.mapper.OrderDataAccessMapper;
import com.ct08SWA.orderservice.orderapplicationservice.ports.outputports.Repository.OrderRepository;
import com.ct08SWA.orderservice.orderdataaccess.repository.OrderJpaRepository;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.OrderId;
import com.ct08SWA.orderservice.orderdomaincore.valueobject.TrackingId;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository, 
                             OrderDataAccessMapper orderDataAccessMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public Order save(Order order) {
        var orderEntity = orderDataAccessMapper.orderToOrderEntity(order);
        var savedOrderEntity = orderJpaRepository.save(orderEntity);
        return orderDataAccessMapper.orderEntityToOrder(savedOrderEntity);
    }

	@Override
	public Optional<Order> findById(OrderId orderId) {
        return orderJpaRepository.findById(orderId.getValue())
                .map(orderDataAccessMapper::orderEntityToOrder);
	}

	@Override
	public Optional<Order> findByTrackingId(TrackingId trackingId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
}