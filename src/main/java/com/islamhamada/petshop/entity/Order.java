package com.islamhamada.petshop.entity;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "user_id")
    private long userId;

    private Instant time;

    private double price;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String country;

    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "house_number")
    private String houseNumber;

    private String street;
}
