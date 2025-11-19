package com.ct08SWA.restaurantservice.restaurantcontainer.rest;


import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderApprovalCommand;
import com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports.OrderApprovalCommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/restaurants")
public class RestaurantController {

    private final OrderApprovalCommandHandler orderApprovalCommandHandler;

    public RestaurantController(OrderApprovalCommandHandler orderApprovalCommandHandler) {
        this.orderApprovalCommandHandler = orderApprovalCommandHandler;
    }

    @PostMapping("/approval")
    public ResponseEntity<String> approveOrder(@RequestBody OrderApprovalCommand dto) {
        log.info("Approving order: {}", dto.getOrderId());
        orderApprovalCommandHandler.approveOrder(dto);
        return ResponseEntity.ok("Order approval processed successfully.");
    }
}