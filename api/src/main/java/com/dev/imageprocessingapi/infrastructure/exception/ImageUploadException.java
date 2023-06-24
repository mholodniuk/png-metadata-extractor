package com.dev.imageprocessingapi.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "File upload error")
public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String msg) {
        super(msg);
    }
}
