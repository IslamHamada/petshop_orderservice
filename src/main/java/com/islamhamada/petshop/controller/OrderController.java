package com.islamhamada.petshop.controller;

import com.islamhamada.petshop.model.ElaborateOrder;
import com.islamhamada.petshop.model.OrderCartRequest;
import com.islamhamada.petshop.service.OrderService;
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
    public ResponseEntity<ElaborateOrder> orderUserCart(@PathVariable long user_id, @RequestBody OrderCartRequest request){
        ElaborateOrder elaborateOrder = orderService.orderUserCart(user_id, request);
        return new ResponseEntity<>(elaborateOrder, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @GetMapping("/{user_id}")
    public ResponseEntity<List<ElaborateOrder>> getOrders(@PathVariable long user_id) {
        List<ElaborateOrder> elaborateOrders = orderService.getOrders(user_id);
        return new ResponseEntity<>(elaborateOrders, HttpStatus.OK);
    }
}
