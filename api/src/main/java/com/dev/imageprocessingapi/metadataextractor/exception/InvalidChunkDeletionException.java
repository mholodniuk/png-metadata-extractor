package com.dev.imageprocessingapi.metadataextractor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Attempted to remove critical chunks")
public class InvalidChunkDeletionException extends RuntimeException {
    public InvalidChunkDeletionException() {
        super();
    }
}
