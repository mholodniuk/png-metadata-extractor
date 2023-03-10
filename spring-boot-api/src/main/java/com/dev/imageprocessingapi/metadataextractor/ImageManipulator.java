package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.metadataextractor.chunks.Chunk;
import com.dev.imageprocessingapi.model.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageManipulator {
    private final ImageMetaDataExtractor extractor;
    public List<Chunk> removeAncillaryChunks(Image image) {
        var imageMetadata = extractor.getImageMetadata(image);

        return imageMetadata
                .getChunks().stream()
                .peek(System.out::println)
                .filter(chunk -> switch (chunk.getType()) {
                    case "IHDR", "gAMA", "PLTE", "IDAT", "IEND" -> true;
                    default -> false;
                })
                .toList();
    }

    public List<Chunk> removeGivenChunks(Image image, List<String> types) {
        var imageMetadata = extractor.getImageMetadata(image);

        return imageMetadata
                .getChunks().stream()
                .peek(System.out::println)
                .filter(chunk -> !types.contains(chunk.getType()))
                .toList();
    }
}
