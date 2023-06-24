package com.dev.imageprocessingapi.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Magnitude not found")
public class SpectrumNotGeneratedException extends RuntimeException {
    public SpectrumNotGeneratedException() {
        super();
    }
}
