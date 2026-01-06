package com.ct08SWA.userservice.userapplicationservice.ports.outputports;

public interface UserSecurityPort {
    // Thêm vào danh sách đen (TTL = 30 phút, logic TTL nằm ở Adapter)
    void addToBlacklist(String userId);

    // Xóa khỏi danh sách đen
    void removeFromBlacklist(String userId);

}