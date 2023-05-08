package com.dev.imageprocessingapi.service;

import com.dev.imageprocessingapi.exception.ImageNotFoundException;
import com.dev.imageprocessingapi.exception.ImageUploadException;
import com.dev.imageprocessingapi.exception.MagnitudeNotGeneratedException;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageManipulator;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageMetadataParser;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageSerializer;
import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.PNGMetadata;
import com.dev.imageprocessingapi.model.dto.ChunksToDeleteDTO;
import com.dev.imageprocessingapi.repository.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final ImageMetadataParser parser;
    private final ImageSerializer serializer;
    private final ImageManipulator manipulator;

    public String addImage(MultipartFile file) {
        byte[] bytes = validateAndRetrieveBytes(file);

        var image = new Image();
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setBytes(new Binary(BsonBinarySubType.BINARY, bytes));

        image = imageRepository.insert(image);
        return image.getId();
    }

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
        var image = imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));

        return parser.processImage(image);
    }

    public Image getImage(String id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));
    }

    public Image getImageMagnitude(String id) {
        var image = imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));
        if (image.getMagnitude() == null)
            throw new MagnitudeNotGeneratedException();

        return image;
    }

    public void removeChunks(String id, ChunksToDeleteDTO chunksToDelete) {
        List<RawChunk> chunksToKeep;
        var image = imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));

        if (chunksToDelete != null && chunksToDelete.chunks() != null) {
            chunksToKeep = manipulator.removeGivenChunks(image, chunksToDelete.chunks());
        } else {
            chunksToKeep = manipulator.removeAncillaryChunks(image);
        }

        Binary criticalChunksAsBytes = serializer.saveAsPNG(chunksToKeep);
        image.setBytes(criticalChunksAsBytes);

        imageRepository.save(image);
    }
}