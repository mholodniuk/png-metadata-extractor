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
                .chunks().stream()
                .filter(chunk -> criticalChunks.contains(chunk.getType()))
                .toList();
    }

    public List<Chunk> removeGivenChunks(Image image, List<String> chunksToDelete) {
        var imageMetadata = extractor.getImageMetadata(image);

        if(chunksToDelete.stream().anyMatch(criticalChunks::contains))
            throw new InvalidChunkDeletionException();

        return imageMetadata
                .chunks().stream()
                .filter(chunk -> !chunksToDelete.contains(chunk.getType()))
                .toList();
    }
}
