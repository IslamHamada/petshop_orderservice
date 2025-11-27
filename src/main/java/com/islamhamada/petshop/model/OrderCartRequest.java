package com.islamhamada.petshop.model;

import lombok.Data;

@Data
public class OrderCartRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private String city;
    private String postalCode;
    private String houseNumber;
    private String street;
}
