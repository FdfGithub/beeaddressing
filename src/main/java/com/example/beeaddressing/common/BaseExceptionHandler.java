package com.example.beeaddressing.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseExceptionHandler {
    /**
     * 异常处理
     */
    @ExceptionHandler(Exception.class)
    public Response error(Exception e) {
        e.printStackTrace();
        return Response.createError(e.getMessage());
    }
}
