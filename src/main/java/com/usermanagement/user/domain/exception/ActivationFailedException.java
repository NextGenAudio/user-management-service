package com.usermanagement.user.domain.exception;

public class ActivationFailedException extends RuntimeException{
    public ActivationFailedException(String msg){
        super(msg);
    }
}
