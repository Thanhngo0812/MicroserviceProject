package com.ct08SWA.orderservice.ordercontainer.rest;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CancelOrderCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ct08SWA.orderservice.orderapplicationservice.dto.inputdto.Order.CreateOrderCommand;
import com.ct08SWA.orderservice.orderapplicationservice.dto.ouputdto.Order.OrderCreatedResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.ct08SWA.orderservice.orderapplicationservice.ports.inputports.Service.OrderApplicationService;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping(value = "/orders")
public class OrderController {
	private final OrderApplicationService orderApplicationService;
	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	public OrderController(OrderApplicationService orderApplicationService) {
		this.orderApplicationService = orderApplicationService;
		}
	//new order
	@PostMapping
	public ResponseEntity<OrderCreatedResponse> createOrder(
	@Valid @RequestBody CreateOrderCommand createOrderCommand,
    @RequestHeader("X-User-Id") String loggedInUserId) {

	// Log request
	log.info("Creating order for customer: {} at restaurant: {}",
	createOrderCommand.customerId(),
	createOrderCommand.restaurantId());

        // 2. SO SÁNH ID TỪ TOKEN VÀ ID TRONG REQUEST
        // Chuyển đổi String sang UUID để so sánh chính xác
        UUID tokenUserId = UUID.fromString(loggedInUserId);
	// Delegate to use case (Input Port)
	OrderCreatedResponse response = orderApplicationService.createOrder(createOrderCommand,tokenUserId);
	// Log success
	log.info("Order created with tracking id: {}",
	response.orderTrackingId());
	// Return HTTP 201 Created
	return ResponseEntity
	.status(HttpStatus.CREATED)
	.body(response);
	}

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable UUID orderId) {
        log.info("Request to cancel order received for ID: {}", orderId);

        CancelOrderCommand command = CancelOrderCommand.builder()
                .orderId(orderId)
                .build();

        // 1. Delegate to Use Case (Input Port)
        orderApplicationService.cancelOrder(command);

        // 2. Trả về HTTP 202 Accepted (để báo hiệu SAGA đã bắt đầu)
        return ResponseEntity.accepted().body("Bạn đã hủy đơn hàng thành công");
    }
	}


