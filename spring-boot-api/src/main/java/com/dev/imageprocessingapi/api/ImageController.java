package com.dev.imageprocessingapi.api;

import com.dev.imageprocessingapi.model.PNGMetadata;
import com.dev.imageprocessingapi.service.ImageService;
import com.dev.imageprocessingapi.validation.MongoObjectId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        var id = imageService.addImage(file);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("/metadata/{id}")
    public ResponseEntity<PNGMetadata> getImageMetadata(@PathVariable @MongoObjectId String id) {
        return ResponseEntity.ok()
                .body(imageService.getImageMetadata(id));
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable @MongoObjectId String id) {
        var image = imageService.getImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(new ByteArrayResource(image.getBytes().getData()));
    }

    @GetMapping("/images/{id}/magnitude")
    public ResponseEntity<ByteArrayResource> getImageMagnitude(@PathVariable @MongoObjectId String id) {
        var image = imageService.getImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(new ByteArrayResource(image.getMagnitude().getData()));
    }
}

