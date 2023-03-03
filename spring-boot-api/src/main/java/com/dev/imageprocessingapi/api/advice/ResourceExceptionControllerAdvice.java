package com.dev.imageprocessingapi.api.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class ResourceExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    @Value("${hostname}")
    private String hostname;

    @ExceptionHandler
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn(e.getMessage());
        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, "Invalid ID");
        pd.setTitle("Entity Not Found");
        pd.setProperty("hostname", hostname);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(pd);
    }
}
