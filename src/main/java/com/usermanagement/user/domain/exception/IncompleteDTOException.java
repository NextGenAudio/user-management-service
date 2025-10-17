package com.usermanagement.user.domain.exception;

public class IncompleteDTOException extends RuntimeException {
    public IncompleteDTOException(String message) {
        super(message);
    }
}
