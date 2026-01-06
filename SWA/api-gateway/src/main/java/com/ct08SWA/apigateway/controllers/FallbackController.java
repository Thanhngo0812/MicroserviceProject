package com.ct08SWA.apigateway.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @RequestMapping("/order")
    public Mono<String> orderServiceFallback() {
        return Mono.just("Hệ thống đặt hàng đang bảo trì hoặc quá tải. Vui lòng thử lại sau 5 phút.");
    }

    @RequestMapping("/restaurants")
    public Mono<String> restaurantServiceFallback() {
        return Mono.just("Hệ thống nhà hàng đang bảo trì hoặc quá tải. Vui lòng thử lại sau 5 phút.");
    }


    @RequestMapping("/auth")
    public Mono<String> userServiceFallback() {
        return Mono.just("Hệ thống quản lý người dùng đang bảo trì hoặc quá tải. Vui lòng thử lại sau 5 phút.");
    }
}
