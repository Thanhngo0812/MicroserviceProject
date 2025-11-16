package com.ct08SWA.paymentservice.paymentcontainer.container;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan("com.ct08SWA.paymentservice.paymentdataaccess.entity")           // ← Scan entity package
@EnableJpaRepositories(basePackages = "com.ct08SWA.paymentservice.paymentdataaccess.repository") // <--- THÊM DÒNG NÀY
@ComponentScan(basePackages = {
        "com.ct08SWA.paymentservice.paymentcontainer",
        "com.ct08SWA.paymentservice.paymentapplicationservice",
        "com.ct08SWA.paymentservice.paymentdataaccess",
        "com.ct08SWA.paymentservice.paymentdomaincore",
        "com.ct08SWA.paymentservice.paymentmessaging"


})
public class PaymentserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentserviceApplication.class, args);
    }

}

