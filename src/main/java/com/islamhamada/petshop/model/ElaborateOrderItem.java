package com.islamhamada.petshop.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElaborateOrderItem {
    private String product_name;
    private double price;
    private long count;
}
