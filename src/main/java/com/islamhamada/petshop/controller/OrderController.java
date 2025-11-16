package com.islamhamada.petshop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PreAuthorize("hasAnyRole('Customer')")
    @PostMapping("/{user_id}")
    public ResponseEntity<ElaborateOrder> orderCartItems(@PathVariable long user_id){
        ElaborateOrder elaborateOrder = orderService.orderUserCart(user_id);
        return new ResponseEntity<>(elaborateOrder, HttpStatus.OK);
    }
}
