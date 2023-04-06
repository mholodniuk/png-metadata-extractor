package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.*;
import com.dev.imageprocessingapi.metadataextractor.model.*;
import com.dev.imageprocessingapi.model.PNGMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChunkInterpreter {
    private Map<String, Object> IHDRInfo;

    public PNGMetadata appendInterpretedInformation(String id, List<RawChunk> chunks) {
        List<Chunk> processedChunks = new ArrayList<>();
        RawChunk IHDR = chunks.get(0);
        IHDRInfo = new IHDRAnalyser().analyse(IHDR.rawBytes());

        for (RawChunk chunk : chunks) {
            Chunk analysedChunk = processRawChunk(chunk, matchAnalyserToChunkType(chunk.type()));
            processedChunks.add(analysedChunk);
        }
        return new PNGMetadata(id, true, processedChunks);
    }

    private Chunk processRawChunk(RawChunk rawChunk, Analyser analyser) {
        Map<String, Object> properties = analyser != null ? analyser.analyse(rawChunk.rawBytes()) : null;
        return new Chunk(rawChunk.type(), rawChunk.length(), rawChunk.rawBytes(), properties, rawChunk.CRC());
    }

    private Analyser matchAnalyserToChunkType(String type) {
        return switch (type) {
            case "IHDR" -> new IHDRAnalyser();
            case "gAMA" -> new gAMAAnalyser();
            case "PLTE" -> new PLTEAnalyser((int) IHDRInfo.get("Color type"));
            case "tIME" -> new tIMEAnalyser();
            default -> null;
        };
    }
}
