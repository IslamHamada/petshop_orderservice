package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ElaborateOrderDTO;
import com.islamhamada.petshop.model.OrderCartRequest;

import java.util.List;

public interface OrderService {
    ElaborateOrderDTO orderUserCart(long user_id, OrderCartRequest request);
    List<ElaborateOrderDTO> getOrders(long user_id);
}
