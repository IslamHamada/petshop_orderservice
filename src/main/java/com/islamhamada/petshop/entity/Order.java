package com.islamhamada.petshop.entity;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Order {

    @Id
    @GeneratedValue
    private long id;

    private long userId;

    private long cartItemId;

    private Date date;
}
