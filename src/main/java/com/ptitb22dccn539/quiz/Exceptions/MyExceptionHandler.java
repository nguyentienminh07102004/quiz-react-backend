package com.ptitb22dccn539.quiz.Exceptions;

import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = DataInvalidException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public APIResponse myDataInvalidExceptionHandler(DataInvalidException e) {
        return APIResponse.builder()
                .message(e.getMessage())
                .response(e)
                .build();
    }

    @ExceptionHandler(value = ServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public APIResponse myServerErrorException(ServerErrorException e) {
        return APIResponse.builder()
                .message(e.getMessage())
                .response(e)
                .build();
    }

    @ExceptionHandler(value = MyAuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public APIResponse AuthenticationHandlerException(MyAuthenticationException e) {
        return APIResponse.builder()
                .message(e.getMessage())
                .response(e)
                .build();
    }

//    @ExceptionHandler(value = AccessDeniedException.class)
//    @ResponseStatus(value = HttpStatus.FORBIDDEN)
//    public APIResponse AccessDeniedHandlerException(AccessDeniedException e) {
//        return APIResponse.builder()
//                .message(e.getMessage())
//                .response(e)
//                .build();
//    }
}
