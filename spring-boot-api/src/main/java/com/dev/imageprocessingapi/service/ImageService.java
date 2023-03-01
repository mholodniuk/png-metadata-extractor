package com.dev.imageprocessingapi.service;

import com.dev.imageprocessingapi.exception.ImageNotFoundException;
import com.dev.imageprocessingapi.exception.ImageUploadException;
import com.dev.imageprocessingapi.exception.InvalidObjectIdException;
import com.dev.imageprocessingapi.metadataextractor.ImageMetaDataExtractor;
import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.PNGMetadata;
import com.dev.imageprocessingapi.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final ImageMetaDataExtractor imageParser;

    public String addImage(MultipartFile file) {
        byte[] bytes = validateAndRetrieveBytes(file);

        var image = new Image();
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setBytes(new Binary(BsonBinarySubType.BINARY, bytes));

        image = imageRepository.insert(image);
        return image.getId();
    }

    // todo: add legit spring validation
    private byte[] validateAndRetrieveBytes(MultipartFile file) {
        byte[] bytes;
        if (file.isEmpty()) {
            log.warn("Received file is empty");
            throw new ImageUploadException("File is empty");
        }
        if (!Objects.equals(file.getContentType(), "image/png")) {
            log.warn("Received file is not image/png");
            throw new ImageUploadException("File extension is not PNG");
        }

        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            log.error("Error while retrieving bytes from image");
            throw new ImageUploadException(e.getMessage());
        }
        return bytes;
    }

    public PNGMetadata getImageMetadata(String id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));

        return imageParser.getImageMetadata(image);
    }

    public Image getImage(String id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));
    }
}