package com.dev.imageprocessingapi.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ChunksSavingException extends RuntimeException {
    public ChunksSavingException(String msg) {
        super(msg);
    }
}
