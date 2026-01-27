package com.islamhamada.petshop.external.service;

import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Log4j2
@Component
public class CartServiceFallbackFactory implements FallbackFactory<CartService> {

    @Override
    public CartService create(Throwable cause) {
        log.error(cause);
        return new CartService() {
            @Override
            public ResponseEntity<List<ElaborateCartItemDTO>> getCartByUser(long user_id) {
                throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "Cart service is down!", cause);
            }

            @Override
            public ResponseEntity<Long> emptyCartOfUser(long user_id) {
                throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "Cart service is down!", cause);
            }
        };
    }
}
