package com.ct08SWA.restaurantservice.restaurantdomaincore.service;


// Import logging (Pure Java)
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderApproval;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.OrderProduct;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Product;
import com.ct08SWA.restaurantservice.restaurantdomaincore.entity.Restaurant;
import com.ct08SWA.restaurantservice.restaurantdomaincore.event.RestaurantApprovedEvent;
import com.ct08SWA.restaurantservice.restaurantdomaincore.event.RestaurantRejectedEvent;
import com.ct08SWA.restaurantservice.restaurantdomaincore.exception.RestaurantDomainException;
import com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject.ProductId;

import java.util.logging.Logger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map; // Import Map
import java.util.function.Function; // Import Function
import java.util.stream.Collectors; // Import Collectors

// @Slf4j (Bỏ)
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

    // Dùng Logger (Pure Java)
    private static final Logger log = Logger.getLogger(RestaurantDomainServiceImpl.class.getName());

    private final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Implement logic nghiệp vụ chính.
     * Sẽ gọi entity.validateOrder() và xử lý kết quả.
     * SỬA LẠI: Thêm logic "Cảnh báo" (Warning) khi giá (price) sai.
     */
    @Override
    public void validateOrder(Restaurant restaurant,
                              OrderApproval orderApproval,
                              List<String> failureMessages) {

        ZonedDateTime now = ZonedDateTime.now(UTC);
        try {
            // 1. Gọi logic nghiệp vụ (Business logic) trong Entity
            //    (SỬA LẠI: Tách logic kiểm tra giá (price) ra khỏi Entity
            //     để Domain Service xử lý Warning)

            // 1a. Kiểm tra Active và Available (Ném Exception nếu FAILED)
            validateAvailability(restaurant, orderApproval);

            // 1b. Kiểm tra Giá (Chỉ thêm Warning nếu SAI)
            validatePrices(restaurant, orderApproval); // Hàm này sẽ gọi orderApproval.addWarning(...)

            // 2. Thành công -> Tạo Event APPROVED (phẳng)
            log.info("Đơn hàng (Order ID: " + orderApproval.getOrderId().getValue() + ") được DUYỆT cơ bản.");

            // Trả về POJO "phẳng"
//            restaurant.addDomainEvent(new RestaurantApprovedEvent(
//                    orderApproval.getId().getValue(),
//                    orderApproval.getOrderId().getValue(),
//                    orderApproval.getRestaurantId().getValue(),
//                    "APPROVED",
//                    now
//
//            ));

        } catch (RestaurantDomainException e) {
            // 3. Thất bại (Fatal Error) -> Tạo Event REJECTED (phẳng)
            log.warning("Đơn hàng (Order ID: " + orderApproval.getOrderId().getValue() +
                    ") bị TỪ CHỐI (REJECTED). Lý do: " + e.getMessage());
            failureMessages.add(e.getMessage());
            orderApproval.reject(failureMessages); // Cập nhật trạng thái Entity

            // Trả về POJO "phẳng"
            restaurant.addDomainEvent(new RestaurantRejectedEvent(
                    orderApproval.getId().getValue(),
                    orderApproval.getOrderId().getValue(),
                    orderApproval.getRestaurantId().getValue(),
                    now,
                    "REJECTED",
                    failureMessages
            ));
        }
    }

    /**
     * SỬA LẠI: Tách hàm kiểm tra lỗi "Nghiêm trọng" (Fatal)
     * (Hết món / Bị ban)
     */
    private void validateAvailability(Restaurant restaurant, OrderApproval orderApproval) {
        if (!restaurant.isActive()) {
            throw new RestaurantDomainException(
                    "Nhà hàng " + restaurant.getName() + " (ID: " + restaurant.getId().getValue() + ") không hoạt động (inactive)!"
            );
        }

        Map<ProductId, Product> productMap = restaurant.getProducts().stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (OrderProduct item : orderApproval.getProducts()) {
            Product productInMenu = productMap.get(item.getId());

            if (productInMenu == null) {
                throw new RestaurantDomainException(
                        "Món ăn (Product ID: " + item.getId().getValue() + ") không tìm thấy trong menu."
                );
            }

            if (!productInMenu.isAvailable()) {
                throw new RestaurantDomainException(
                        "Món ăn '" + productInMenu.getName() + "' (ID: " + item.getId().getValue() + ") đã hết hàng (unavailable)."
                );
            }
        }
    }

    /**
     * SỬA LẠI: Tách hàm kiểm tra lỗi "Cảnh báo" (Warning)
     * (Giá sai)
     */
    private void validatePrices(Restaurant restaurant, OrderApproval orderApproval) {
        Map<ProductId, Product> productMap = restaurant.getProducts().stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (OrderProduct item : orderApproval.getProducts()) {
            Product productInMenu = productMap.get(item.getId()); // (Đã check null ở hàm trên)

            if (!productInMenu.getPrice().equals(item.getPrice())) {
                String warningMsg = "Giá của món ăn '" + productInMenu.getName() +
                        "' (ID: " + item.getId().getValue() + ") đã thay đổi. " +
                        "Giá Yêu cầu (Request): " + item.getPrice().getAmount() +
                        ", Giá Thực tế (Menu): " + productInMenu.getPrice().getAmount();
                log.warning(warningMsg);

                // SỬ DỤNG CODE CỦA BẠN: Thêm Cảnh báo vào Entity
                orderApproval.addWarning(warningMsg);
            }
        }
    }
}