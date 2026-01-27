package com.islamhamada.petshop.controller;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.List;

public class ProductServiceSupplier implements ServiceInstanceListSupplier {
    private final int port;

    public ProductServiceSupplier(int port) {
        this.port = port;
    }

    @Override
    public String getServiceId() {
        return "product-service-svc";
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(List.of(new DefaultServiceInstance(
                "product-service-svc",
                "product-service-svc",
                "localhost",
                port,
                false
        )));
    }
}
