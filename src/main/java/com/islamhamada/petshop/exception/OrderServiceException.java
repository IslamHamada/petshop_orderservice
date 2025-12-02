package com.islamhamada.petshop.exception;

import com.islamhamada.petshop.contracts.exception.ServiceException;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class OrderServiceException extends ServiceException {
    private long errorCode;
    private HttpStatus httpStatus;

    public OrderServiceException(String message, long errorCode, HttpStatus httpStatus) {
        super(message, "ORDER_" + errorCode);
        this.httpStatus = httpStatus;
    }
}
