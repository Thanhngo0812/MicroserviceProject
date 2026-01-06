package com.ct08SWA.restaurantservice.restaurantcontainer.rest;


import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderApprovalCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.outpudto.RestaurantValidationResponse;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports.OrderApprovalCommandHandler;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports.RestaurantApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/restaurants")
public class RestaurantController {

    private final OrderApprovalCommandHandler orderApprovalCommandHandler;
    private final RestaurantApplicationService restaurantApplicationService;
    public RestaurantController(OrderApprovalCommandHandler orderApprovalCommandHandler, RestaurantApplicationService restaurantApplicationService) {
        this.orderApprovalCommandHandler = orderApprovalCommandHandler;
        this.restaurantApplicationService = restaurantApplicationService;
    }

    @PostMapping("/approval")
    public ResponseEntity<String> approveOrder(@RequestBody OrderApprovalCommand dto) {
        log.info("Approving order: {}", dto.getOrderId());
        orderApprovalCommandHandler.approveOrder(dto);
        return ResponseEntity.ok("Order approval processed successfully.");
    }


    @GetMapping("/{id}/validate")
    public ResponseEntity<RestaurantValidationResponse> validateRestaurant(@PathVariable("id") String restaurantId) {
        // 1. Tìm nhà hàng
        // Nếu không thấy -> Ném RestaurantNotFoundException -> Trả về 404
        RestaurantValidationResponse restaurant = restaurantApplicationService.findRestaurantById(UUID.fromString(restaurantId));


        return ResponseEntity.ok(restaurant);
    }
}