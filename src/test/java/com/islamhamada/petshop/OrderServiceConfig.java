package com.islamhamada.petshop;

import com.islamhamada.petshop.controller.CartServiceSupplier;
import com.islamhamada.petshop.controller.OrderControllerTest;
import com.islamhamada.petshop.controller.ProductServiceSupplier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class OrderServiceConfig {

    @Bean
    public ServiceInstanceListSupplier cartServiceSupplier() {
        return new CartServiceSupplier(OrderControllerTest.cartService.port());
    }

    @Bean
    public ServiceInstanceListSupplier productServiceSupplier() {
        return new ProductServiceSupplier(OrderControllerTest.productService.port());
    }
}
