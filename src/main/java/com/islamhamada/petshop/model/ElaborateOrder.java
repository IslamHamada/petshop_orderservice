package com.islamhamada.petshop.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ElaborateOrder {
    private Instant time;
    private List<ElaborateOrderItem> elaborateOrderItems;
}
