package com.islamhamada.petshop.service;

import com.islamhamada.petshop.model.ElaborateOrder;

import java.util.List;

public interface OrderService {
    ElaborateOrder orderUserCart(long user_id);
    List<ElaborateOrder> getOrders(long user_id);
}
