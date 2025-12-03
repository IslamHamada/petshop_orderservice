package com.islamhamada.petshop.controller;

import com.islamhamada.petshop.contracts.dto.ElaborateOrderDTO;
import com.islamhamada.petshop.model.OrderCartRequest;
import com.islamhamada.petshop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PreAuthorize("hasAnyRole('Customer')")
    @PostMapping("/{user_id}")
    public ResponseEntity<ElaborateOrderDTO> orderUserCart(@Valid @PathVariable long user_id, @Valid @RequestBody OrderCartRequest request){
        ElaborateOrderDTO elaborateOrder = orderService.orderUserCart(user_id, request);
        return new ResponseEntity<>(elaborateOrder, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @GetMapping("/{user_id}")
    public ResponseEntity<List<ElaborateOrderDTO>> getOrders(@Valid @PathVariable long user_id) {
        List<ElaborateOrderDTO> elaborateOrders = orderService.getOrders(user_id);
        return new ResponseEntity<>(elaborateOrders, HttpStatus.OK);
    }
}
