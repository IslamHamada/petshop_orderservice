package com.islamhamada.petshop.external.service;

import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.contracts.model.ReduceQuantityRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@Component
public class ProductServiceFallbackFactory implements FallbackFactory<ProductService> {
    @Override
    public ProductService create(Throwable cause) {
        log.error(cause);
        return new ProductService() {

            @Override
            public ResponseEntity<ProductDTO> getProductById(long id) {
                throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "Product service is down!", cause);
            }

            @Override
            public ResponseEntity<Integer> reduceProductQuntity(long id, ReduceQuantityRequest request) {
                throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "Product service is down!", cause);
            }
        };
    }
}
