package com.ct08SWA.orderservice.orderapplicationservice.config;

import com.ct08SWA.orderservice.orderdomaincore.service.OrderDomainService;
import com.ct08SWA.orderservice.orderdomaincore.service.OrderDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Lớp cấu hình này chịu trách nhiệm khai báo các Bean
 * từ module 'order-domain-core' (vốn là Java thuần túy và không biết về Spring).
 * * Bằng cách này, chúng ta tuân thủ Dependency Rule:
 * Lớp Application (nơi này) phụ thuộc vào Domain (lớp trong),
 * và chịu trách nhiệm "kích hoạt" nó.
 */
@Configuration
public class ApplicationServiceConfig {

    /**
     * Khai báo OrderDomainServiceImpl là một Spring Bean.
     * Bất cứ nơi nào khác trong ứng dụng (như OrderApplicationServiceImpl)
     * yêu cầu tiêm (inject) một 'OrderDomainService', Spring sẽ cung cấp
     * instance được tạo ra từ hàm này.
     *
     * @return một instance của OrderDomainServiceImpl
     */
    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}

