package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.IHDRAnalyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.gAMAAnalyser;
import com.dev.imageprocessingapi.metadataextractor.model.Chunk;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@AllArgsConstructor
public class ChunkFactory {
    public static Chunk create(String type, int length, List<String> rawBytes, String CRC) {
        return switch (type) {
            case "IHDR" -> createChunk(type, length, rawBytes, CRC, new IHDRAnalyser());
            case "gAMA" -> createChunk(type, length, rawBytes, CRC, new gAMAAnalyser());
            default -> new Chunk(type, length, rawBytes, null, CRC);
        };
    }

    private static Chunk createChunk(String type, int length,
                                     List<String> rawBytes, String CRC, Analyser analyser) {
        Map<String, Object> IHDRProperties = analyser.analyse(rawBytes);
        return new Chunk(type, length, rawBytes, IHDRProperties, CRC);
    }
}