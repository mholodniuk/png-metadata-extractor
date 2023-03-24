package com.dev.imageprocessingapi.api.utils;

import com.dev.imageprocessingapi.model.Image;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseEntityUtils {
    public static ResponseEntity<ByteArrayResource> createResponseEntity(Image image, boolean useMagnitude) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(
                useMagnitude ? image.getMagnitude().getData()
                        : image.getBytes().getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(byteArrayResource);
    }
}
