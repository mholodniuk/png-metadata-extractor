package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
import com.dev.imageprocessingapi.infrastructure.event.annotation.TrackExecutionTime;
import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.PNGMetadata;
import lombok.AllArgsConstructor;
import org.bson.types.Binary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class MetadataExtractorFacade {
    private final ImageManipulator imageManipulator;
    private final ImageMetadataParser imageMetadataParser;
    private final ImageSerializer imageSerializer;

    @TrackExecutionTime
    public List<RawChunk> removeAncillaryChunks(Image image) {
        return imageManipulator.removeAncillaryChunks(image);
    }

    @TrackExecutionTime
    public List<RawChunk> removeSelectedChunks(Image image, List<String> chunks) {
        return imageManipulator.removeSelectedChunks(image, chunks);
    }

    @TrackExecutionTime
    public PNGMetadata processImage(Image image) {
        return imageMetadataParser.processImage(image);
    }

    @TrackExecutionTime
    public Binary saveAsPNG(List<RawChunk> chunks) {
        return imageSerializer.saveAsPNG(chunks);
    }
}
