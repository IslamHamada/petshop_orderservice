package com.islamhamada.petshop.service;

import com.islamhamada.petshop.model.ElaborateOrder;
import com.islamhamada.petshop.model.OrderCartRequest;

import java.util.List;

public interface OrderService {
    ElaborateOrder orderUserCart(long user_id, OrderCartRequest request);
    List<ElaborateOrder> getOrders(long user_id);
}
