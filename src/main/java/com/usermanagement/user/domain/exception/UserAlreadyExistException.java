package com.usermanagement.user.domain.exception;

public class UserAlreadyExistException extends RuntimeException{
    public  UserAlreadyExistException(String msg){
        super(msg);
    }
}
