package com.kata.ecommerce.common.exception;

// Règle métier violée
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
