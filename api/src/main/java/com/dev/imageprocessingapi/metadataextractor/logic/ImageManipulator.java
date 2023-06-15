package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.encryption.CustomPrivateKey;
import com.dev.imageprocessingapi.encryption.RSA;
import com.dev.imageprocessingapi.event.annotation.TrackExecutionTime;
import com.dev.imageprocessingapi.exception.InvalidChunkDeletionException;
import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import com.dev.imageprocessingapi.model.Image;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import static com.dev.imageprocessingapi.encryption.Utils.generateRSACustomKeyPair;

@Component
@AllArgsConstructor
public class ImageManipulator {
    private final List<String> criticalChunks = List.of("IHDR", "PLTE", "IDAT", "IEND");
    private final ImageMetadataParser parser;
    private final RSA rsa;

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

        if (chunksToDelete.stream().anyMatch(criticalChunks::contains)) {
            throw new InvalidChunkDeletionException();
        }

        return chunks.stream()
                .filter(chunk -> !chunksToDelete.contains(chunk.type()))
                .toList();
    }

    @TrackExecutionTime
    public void encryptImage(Image image) throws DataFormatException, BadPaddingException, IOException {
        var keyPair = generateRSACustomKeyPair(2048);
        System.out.println("d: " + keyPair.privateKey().d());
        System.out.println("n: " + keyPair.privateKey().n());
        System.out.println(image.getId());
        rsa.encryptECB(image.getBytes().getData(), keyPair.publicKey());
    }
}
