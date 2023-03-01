package com.dev.imageprocessingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Error validating file")
public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String msg) {
        super(msg);
    }
}
