package com.ct08SWA.userservice.usercontainer.rest;


import com.ct08SWA.userservice.userapplicationservice.ports.inputports.UserApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/auth") // Endpoint dành riêng cho Admin
@RequiredArgsConstructor
public class AdminController {

    private final UserApplicationService userApplicationService;

    /**
     * API Khóa tài khoản người dùng.
     * Flow: Update DB active=false -> Thêm vào Redis Blacklist -> Xóa Cache Order Service
     */
    @PostMapping("/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable("id") String userId) {
        userApplicationService.blockUser(UUID.fromString(userId));
        return ResponseEntity.ok("User blocked successfully! Blacklist created.");
    }

    /**
     * API Mở khóa tài khoản người dùng.
     * Flow: Update DB active=true -> Xóa khỏi Redis Blacklist -> Xóa Cache Order Service
     */
    @PostMapping("/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable("id") String userId) {
        userApplicationService.unblockUser(UUID.fromString(userId));
        return ResponseEntity.ok("User unblocked successfully! Blacklist removed.");
    }
}