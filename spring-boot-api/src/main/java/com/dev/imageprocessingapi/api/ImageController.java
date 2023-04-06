package com.dev.imageprocessingapi.api;

import com.dev.imageprocessingapi.api.utils.ResponseEntityUtils;
import com.dev.imageprocessingapi.model.PNGMetadata;
import com.dev.imageprocessingapi.model.dto.ChunksToDeleteDTO;
import com.dev.imageprocessingapi.service.ImageService;
import com.dev.imageprocessingapi.validation.MongoObjectId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile file) {
        var id = imageService.addImage(file);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/metadata")
    public ResponseEntity<PNGMetadata> getImageMetadata(@PathVariable @MongoObjectId String id) {
        return ResponseEntity.ok()
                .body(imageService.getImageMetadata(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable @MongoObjectId String id) {
        var image = imageService.getImage(id);

        return ResponseEntityUtils.createResponseEntity(image, false);
    }

    @GetMapping("/{id}/magnitude")
    public ResponseEntity<ByteArrayResource> getImageMagnitude(@PathVariable @MongoObjectId String id) {
        var image = imageService.getImageMagnitude(id);

        return ResponseEntityUtils.createResponseEntity(image, true);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> removeChunksFromImage(@PathVariable @MongoObjectId String id,
                                                        @RequestBody(required = false) ChunksToDeleteDTO chunks) {
        imageService.removeChunks(id, chunks);
        return new ResponseEntity<>(id, HttpStatus.NO_CONTENT);
    }
}

