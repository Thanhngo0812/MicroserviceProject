package com.ct08SWA.restaurantservice.restaurantdomaincore.valueobject;

public enum ApprovalStatus {
    WAITING,
    PENDING,  // Đang chờ duyệt
    APPROVED, // Đã duyệt (OK)
    REJECTED,  // Đã từ chối (ví dụ: hết món)
    CANCELLED //Hủy
}