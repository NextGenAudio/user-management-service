package com.usermanagement.user.domain.exception;

public class UserAlreadyExistException extends Exception{
    public  UserAlreadyExistException(String msg){
        super(msg);
    }
}
