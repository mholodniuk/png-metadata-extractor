package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.encryption.ByteEncryptor;
import com.dev.imageprocessingapi.event.annotation.TrackExecutionTime;
import com.dev.imageprocessingapi.exception.InvalidChunkDeletionException;
import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import com.dev.imageprocessingapi.model.Image;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
@AllArgsConstructor
public class ImageManipulator {
    private final List<String> criticalChunks = List.of("IHDR", "PLTE", "IDAT", "IEND");
    private final ImageMetadataParser parser;
    private final ByteEncryptor encryptor;

    @TrackExecutionTime
    public List<RawChunk> removeAncillaryChunks(Image image) {
        var chunks = parser.readRawChunks(image);

        return chunks.stream()
                .filter(chunk -> criticalChunks.contains(chunk.type()))
                .toList();
    }

    @TrackExecutionTime
    public List<RawChunk> removeGivenChunks(Image image, List<String> chunksToDelete) {
        var chunks = parser.readRawChunks(image);

        if (chunksToDelete.stream().anyMatch(criticalChunks::contains))
            throw new InvalidChunkDeletionException();

        return chunks.stream()
                .filter(chunk -> !chunksToDelete.contains(chunk.type()))
                .toList();
    }

    public List<RawChunk> encryptImage(Image image) {
        var chunks = parser.readRawChunks(image);

        try {
            encryptor.encrypt(chunks);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return chunks;
    }
}
