package com.islamhamada.petshop.external.service;

import com.islamhamada.petshop.contracts.CartItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "CartService/cart")
public interface CartService {

    @GetMapping("/{user_id}")
    ResponseEntity<List<CartItemDTO>> getCartByUser(@PathVariable long user_id);

    @DeleteMapping("/{user_id}")
    ResponseEntity<Void> emptyCartOfUser(@PathVariable long user_id);
}
