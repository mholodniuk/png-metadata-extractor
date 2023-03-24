package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.IHDRAnalyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.gAMAAnalyser;
import com.dev.imageprocessingapi.metadataextractor.chunks.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
@AllArgsConstructor
public class ChunkFactory {
    private final List<Analyser> analysers;
    // todo: consider removing chunk inheritance -> only Chunk type
    public Chunk create(String type, int length, List<String> rawBytes, String CRC) {
        return switch (type) {
            case "IHDR" -> createIHDRChunk(type, length, rawBytes, CRC);
            case "gAMA" -> creategAMAChunk(type, length, rawBytes, CRC);
            default -> new Chunk(type, length, rawBytes, null, CRC);
        };
    }

    private Chunk createIHDRChunk(String type, int length, List<String> rawBytes, String CRC) {
        Analyser analyser = getAnalyser(type).orElseThrow();
        Map<String, Object> IHDRProperties = analyser.analyse(rawBytes);

        return new Chunk(type, length, rawBytes, IHDRProperties, CRC);
    }

    private Chunk creategAMAChunk(String type, int length, List<String> rawBytes, String CRC) {
        Analyser analyser = getAnalyser(type).orElseThrow();
        Map<String, Object> gAMAProperties = analyser.analyse(rawBytes);

        return new Chunk(type, length, rawBytes, gAMAProperties, CRC);
    }

    private Optional<Analyser> getAnalyser(String chunkType) {
        return switch (chunkType) {
            case "IHDR" -> analysers.stream().filter(analyser -> analyser instanceof IHDRAnalyser).findFirst();
            case "gAMA" -> analysers.stream().filter(analyser -> analyser instanceof gAMAAnalyser).findFirst();
            default -> throw new RuntimeException();
        };
    }
}