package com.ptitb22dccn539.quiz.Exceptions;

import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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
    public APIResponse AuthenticationHandlerExceptionHandler(MyAuthenticationException e) {
        return APIResponse.builder()
                .message(e.getMessage())
                .response(e)
                .build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public APIResponse MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = exception.getBindingResult();
        bindingResult.getFieldErrors().forEach(fieldError -> {
           FieldError error = bindingResult.getFieldError();
           errors.put(fieldError.getField(), ObjectUtils.firstNonNull(error).getDefaultMessage());
        });
        return APIResponse.builder()
                .response(errors)
                .build();
    }
}
