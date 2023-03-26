package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.IHDRAnalyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.gAMAAnalyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.tIMEAnalyser;
import com.dev.imageprocessingapi.metadataextractor.model.Chunk;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@AllArgsConstructor
public class ChunkFactory {
    public static Chunk create(String type, int length, List<String> rawBytes, String CRC) {
        return switch (type) {
            case "IHDR" -> createChunk(type, length, rawBytes, CRC, new IHDRAnalyser());
            case "gAMA" -> createChunk(type, length, rawBytes, CRC, new gAMAAnalyser());
            case "tIME" -> createChunk(type, length, rawBytes, CRC, new tIMEAnalyser());
            default -> new Chunk(type, length, rawBytes, null, CRC);
        };
    }

    private static Chunk createChunk(String type, int length,
                                     List<String> rawBytes, String CRC, Analyser analyser) {
        var IHDRProperties = analyser.analyse(rawBytes);
        return new Chunk(type, length, rawBytes, IHDRProperties, CRC);
    }
}