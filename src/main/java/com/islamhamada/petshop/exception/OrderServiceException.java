package com.islamhamada.petshop.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class OrderServiceException extends RuntimeException {
    private long errorCode;
    private HttpStatus httpStatus;

    public OrderServiceException(String message, long errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
