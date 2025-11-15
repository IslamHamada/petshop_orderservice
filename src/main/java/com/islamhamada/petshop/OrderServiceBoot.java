package com.islamhamada.petshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class OrderServiceBoot {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceBoot.class, args);
	}

}
