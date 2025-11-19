package com.ct08SWA.restaurantservice.restaurantapplicationservice.ports.inputports;

import com.ct08SWA.restaurantservice.restaurantapplicationservice.dto.inputdto.OrderApprovalCommand;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderApproval;

public interface OrderApprovalCommandHandler {
    void approveOrder(OrderApprovalCommand orderApprovalCommand);
}
