package com.ct08SWA.restaurantservice.restaurantcontainer.container;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan("com.ct08SWA.restaurantservice.restaurantdataaccess.entity")           // ← Scan entity package
@EnableJpaRepositories(basePackages = "com.ct08SWA.restaurantservice.restaurantdataaccess.repository") // <--- THÊM DÒNG NÀY
@ComponentScan(basePackages = {
        "com.ct08SWA.restaurantservice.restaurantcontainer",
        "com.ct08SWA.restaurantservice.restaurantapplicationservice",
        "com.ct08SWA.restaurantservice.restaurantdataaccess",
        "com.ct08SWA.restaurantservice.restaurantdomaincore",
        "com.ct08SWA.restaurantservice.restaurantmessaging"


})
public class RestaurantserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantserviceApplication.class, args);
    }

}

