package com.dev.imageprocessingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error during chunk processing")
public class ChunkProcessingException extends RuntimeException {
    public ChunkProcessingException(String msg) {
        super(msg);
    }
}
