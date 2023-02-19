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

import static com.dev.imageprocessingapi.Utils.createHash;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final ImageMetaDataExtractor imageParser;

    public String addImage(String title, MultipartFile file) throws IOException {
        String id = createHash(file.getBytes());
        byte[] bytes = file.getBytes();

        Image image = imageRepository.findById(id).orElseGet(() -> {
            var newImage = new Image();
            newImage.setId(createHash(bytes));
            newImage.setFileName(title);
            newImage.setFileType(file.getContentType());
            newImage.setBytes(new Binary(BsonBinarySubType.BINARY, bytes));

            return imageRepository.insert(newImage);
        });

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