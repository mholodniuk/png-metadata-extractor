package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.infrastructure.event.annotation.TrackExecutionTime;
import com.dev.imageprocessingapi.metadataextractor.exception.InvalidChunkDeletionException;
import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
import com.dev.imageprocessingapi.model.Image;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
class ImageManipulator {
    private final List<String> criticalChunks = List.of("IHDR", "PLTE", "IDAT", "IEND");
    private final ImageMetadataParser parser;

    List<RawChunk> removeAncillaryChunks(Image image) {
        var chunks = parser.readRawChunks(image);

        return chunks.stream()
                .filter(chunk -> criticalChunks.contains(chunk.type()))
                .toList();
    }

    List<RawChunk> removeSelectedChunks(Image image, List<String> chunksToDelete) {
        var chunks = parser.readRawChunks(image);

        if (chunksToDelete.stream().anyMatch(criticalChunks::contains))
            throw new InvalidChunkDeletionException();

        return chunks.stream()
                .filter(chunk -> !chunksToDelete.contains(chunk.type()))
                .toList();
    }
}
