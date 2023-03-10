package com.dev.imageprocessingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Magnitude Not Found")
public class MagnitudeNotGeneratedException extends RuntimeException {
    public MagnitudeNotGeneratedException() {
        super();
    }
}
