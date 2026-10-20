package com.system.design.moviebooking.exceptions;

public class AuthException extends RuntimeException{
    public AuthException(String message){
        super(message);
    }
}
