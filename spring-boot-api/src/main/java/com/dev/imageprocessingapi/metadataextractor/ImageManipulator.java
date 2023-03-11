package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.exception.InvalidChunkDeletionException;
import com.dev.imageprocessingapi.metadataextractor.chunks.Chunk;
import com.dev.imageprocessingapi.model.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageManipulator {

    private final List<String> criticalChunks = List.of("IHDR", "PLTE", "IDAT", "IEND");
    private final ImageMetadataParser extractor;
    public List<Chunk> removeAncillaryChunks(Image image) {
        var imageMetadata = extractor.getImageMetadata(image);

        return imageMetadata
                .getChunks().stream()
                .filter(chunk -> criticalChunks.contains(chunk.getType()))
                .toList();
    }

    public List<Chunk> removeGivenChunks(Image image, List<String> chunks) {
        var imageMetadata = extractor.getImageMetadata(image);

        if(chunks.stream().anyMatch(criticalChunks::contains))
            throw new InvalidChunkDeletionException();

        return imageMetadata
                .getChunks().stream()
                .filter(chunk -> !chunks.contains(chunk.getType()))
                .toList();
    }
}
