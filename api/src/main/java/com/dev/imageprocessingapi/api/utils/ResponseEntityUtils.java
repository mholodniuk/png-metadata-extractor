package com.dev.imageprocessingapi.api.utils;

import com.dev.imageprocessingapi.model.Image;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseEntityUtils {
    public enum Spectrum {
        MAGNITUDE,
        PHASE
    }
    public static ResponseEntity<ByteArrayResource> createSpectrumResponseEntity(Image image, Spectrum type) {
        var byteArrayResource = new ByteArrayResource(
                type == Spectrum.MAGNITUDE ? image.getMagnitude().getData()
                        : image.getPhase().getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(byteArrayResource);
    }
}
