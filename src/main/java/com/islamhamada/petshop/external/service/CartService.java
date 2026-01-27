package com.islamhamada.petshop.external.service;

import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "cart-service-svc",
        url = "${cart-service-svc.url:http://cart-service-svc}",
        path = "/cart",
        fallbackFactory = CartServiceFallbackFactory.class
)
public interface CartService {

    @GetMapping("/user/{user_id}")
    ResponseEntity<List<ElaborateCartItemDTO>> getCartByUser(@PathVariable long user_id);

    @DeleteMapping("/user/{user_id}")
    ResponseEntity<Long> emptyCartOfUser(@PathVariable long user_id);
}
