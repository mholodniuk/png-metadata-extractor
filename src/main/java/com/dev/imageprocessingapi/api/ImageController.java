package com.dev.imageprocessingapi.api;

import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.PNGMetadata;
import com.dev.imageprocessingapi.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String id = imageService.addImage(file.getOriginalFilename(), file);
        return new ResponseEntity<>(id, HttpStatus.ACCEPTED);
    }

    @GetMapping("/metadata/{id}")
    public ResponseEntity<PNGMetadata> getImageMetadata(@PathVariable String id) {
        return ResponseEntity.ok()
                .body(imageService.getImageMetadata(id));
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable String id) {
        Image image = imageService.getImage(id).orElseThrow(RuntimeException::new);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(new ByteArrayResource(image.getBytes().getData()));
    }

    @GetMapping("/test")
    public ResponseEntity<PNGMetadata> test() {
        return ResponseEntity.ok()
                .body(imageService.getImageMetadata("30c632ab5a67f67046fc095faf6a075f"));
    }
}

