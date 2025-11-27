package com.islamhamada.petshop.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ElaborateOrder {
    private Instant time;
    private double price;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private String city;
    private String street;
    private String postalCode;
    private String houseNumber;
    private List<ElaborateOrderItem> elaborateOrderItems;
}
