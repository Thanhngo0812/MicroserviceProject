package com.ct08SWA.userservice.usercontainer.container;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan("com.ct08SWA.userservice.userdataaccess.entity")           // ← Scan entity package
@EnableJpaRepositories(basePackages = "com.ct08SWA.userservice.userdataaccess.repository") // <--- THÊM DÒNG NÀY
@ComponentScan(basePackages = {
        "com.ct08SWA.userservice.usercontainer",
        "com.ct08SWA.userservice.userapplicationservice",
        "com.ct08SWA.userservice.userdataaccess",
        "com.ct08SWA.userservice.userdomaincore"
//        ,
//        "com.ct08SWA.restaurantservice.restaurantmessaging"


})
public class UserserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserserviceApplication.class, args);
    }

}

