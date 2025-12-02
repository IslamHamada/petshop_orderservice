package com.islamhamada.petshop.exception;

import com.islamhamada.petshop.contracts.exception.ServiceException;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class OrderServiceException extends ServiceException {
    private String errorCode;
    private HttpStatus httpStatus;

    public OrderServiceException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, "ORDER_" + errorCode);
        this.httpStatus = httpStatus;
    }
}
