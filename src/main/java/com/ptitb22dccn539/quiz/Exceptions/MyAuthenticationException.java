package com.ptitb22dccn539.quiz.Exceptions;

public class MyAuthenticationException extends RuntimeException {
    public MyAuthenticationException() {
        super("Authentication failed");
    }
}
