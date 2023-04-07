package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.exception.InvalidChunkDeletionException;
import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
import com.dev.imageprocessingapi.model.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageManipulator {
    private final List<String> criticalChunks = List.of("IHDR", "PLTE", "IDAT", "IEND");
    private final ImageMetadataParser parser;

    public List<RawChunk> removeAncillaryChunks(Image image) {
        var chunks = parser.readRawChunks(image);

        return chunks.stream()
                .filter(chunk -> criticalChunks.contains(chunk.type()))
                .toList();
    }

    public List<RawChunk> removeGivenChunks(Image image, List<String> chunksToDelete) {
        var chunks = parser.readRawChunks(image);

        if (chunksToDelete.stream().anyMatch(criticalChunks::contains))
            throw new InvalidChunkDeletionException();

        return chunks.stream()
                .filter(chunk -> !chunksToDelete.contains(chunk.type()))
                .toList();
    }
}
