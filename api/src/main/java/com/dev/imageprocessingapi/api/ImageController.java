package com.dev.imageprocessingapi.api;

import com.dev.imageprocessingapi.api.utils.ResponseEntityUtils;
import com.dev.imageprocessingapi.encryption.CustomKeyPair;
import com.dev.imageprocessingapi.encryption.CustomPrivateKey;
import com.dev.imageprocessingapi.model.PNGMetadata;
import com.dev.imageprocessingapi.model.dto.ChunksToDeleteDTO;
import com.dev.imageprocessingapi.model.dto.KeyLength;
import com.dev.imageprocessingapi.service.ImageService;
import com.dev.imageprocessingapi.validation.MongoObjectId;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/images")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
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

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(new ByteArrayResource(image.getBytes().getData()));
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<Map<String, Boolean>> validateCRC(@PathVariable @MongoObjectId String id) {
        return ResponseEntity.ok()
                .body(imageService.validateImage(id));
    }

    @GetMapping("/{id}/magnitude")
    public ResponseEntity<ByteArrayResource> getImageMagnitudeSpectrum(@PathVariable @MongoObjectId String id) {
        var image = imageService.getImageMagnitude(id);

        return ResponseEntityUtils.createSpectrumResponseEntity(image, ResponseEntityUtils.Spectrum.MAGNITUDE);
    }

    @GetMapping("/{id}/phase")
    public ResponseEntity<ByteArrayResource> getImagePhaseSpectrum(@PathVariable @MongoObjectId String id) {
        var image = imageService.getImagePhase(id);

        return ResponseEntityUtils.createSpectrumResponseEntity(image, ResponseEntityUtils.Spectrum.PHASE);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> removeChunksFromImage(@PathVariable @MongoObjectId String id,
                                                   @RequestBody(required = false) ChunksToDeleteDTO chunks) {
        imageService.removeChunks(id, chunks);
        return new ResponseEntity<>(id, HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/{id}/encrypt")
    public ResponseEntity<CustomKeyPair> encryptImage(@PathVariable @MongoObjectId String id,
                                                      @RequestBody(required = false) KeyLength keyLength) {
        var keyPair = imageService.encryptImage(id, keyLength);
        return ResponseEntity.ok(keyPair);
    }

    @PatchMapping(path = "/{id}/decrypt")
    public ResponseEntity<?> decryptImage(@PathVariable @MongoObjectId String id, @RequestBody CustomPrivateKey privateKey) {
        imageService.decryptImage(id, privateKey);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{id}/encrypt1")
    public ResponseEntity<CustomKeyPair> encryptImage1(@PathVariable @MongoObjectId String id,
                                                      @RequestBody(required = false) KeyLength keyLength) {
        var keyPair = imageService.encryptCompressedImage(id, keyLength);
        return ResponseEntity.ok(keyPair);
    }

    @PatchMapping(path = "/{id}/decrypt1")
    public ResponseEntity<?> decryptImage1(@PathVariable @MongoObjectId String id, @RequestBody CustomPrivateKey privateKey) {
        imageService.decryptCompressedImage(id, privateKey);
        return ResponseEntity.noContent().build();
    }
}

