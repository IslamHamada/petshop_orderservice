package com.islamhamada.petshop.external.service;

import com.islamhamada.petshop.contracts.model.ReduceQuantityRequest;
import com.islamhamada.petshop.contracts.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "product-service-svc",
        url = "${product-service-svc.url:http://product-service-svc}",
        path = "/product"
)
public interface ProductService {
    @GetMapping("/{id}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable("id") long id);

    @PutMapping("/{id}")
    ResponseEntity<ProductDTO> reduceProductQuntity(@PathVariable("id") long id, @RequestBody ReduceQuantityRequest request);
}
