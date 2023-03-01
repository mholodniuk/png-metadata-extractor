package com.dev.imageprocessingapi.service;

import com.dev.imageprocessingapi.exception.ImageNotFoundException;
import com.dev.imageprocessingapi.metadataextractor.ImageMetaDataExtractor;
import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.PNGMetadata;
import com.dev.imageprocessingapi.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final ImageMetaDataExtractor imageParser;

    public String addImage(String title, MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();

        var image = new Image();
        image.setFileName(title);
        image.setFileType(file.getContentType());
        image.setBytes(new Binary(BsonBinarySubType.BINARY, bytes));

        image = imageRepository.insert(image);
        return image.getId();
    }

    public PNGMetadata getImageMetadata(String id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("image not found"));

        return imageParser.getImageMetadata(image);
    }

    public Optional<Image> getImage(String id) {
        return imageRepository.findById(id);
    }
}