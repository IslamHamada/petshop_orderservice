package com.islamhamada.petshop.controller;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.List;

public class CartServiceSupplier implements ServiceInstanceListSupplier {
    private final int port;

    public CartServiceSupplier(int port) {
        this.port = port;
    }

    @Override
    public String getServiceId() {
        return "cart-service-svc";
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(List.of(new DefaultServiceInstance(
                "cart-service-svc",
                "cart-service-svc",
                "localhost",
                port,
                false
        )));
    }
}
