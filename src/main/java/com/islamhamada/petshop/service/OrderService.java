package com.islamhamada.petshop.service;

import com.islamhamada.petshop.model.ElaborateOrder;

public interface OrderService {
    ElaborateOrder orderUserCart(long user_id);
}
