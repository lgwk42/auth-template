package com.project.authtemplate.global.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse catchValidationException(MethodArgumentNotValidException exception) {
        String message = exception
                .getBindingResult()
                .getAllErrors()
                .getFirst()
                .getDefaultMessage();
        return ErrorResponse.of(message);
    }

    private record ErrorResponse(int status, String message) {
        public static ErrorResponse of(String message) {
            return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        }
    }

}
