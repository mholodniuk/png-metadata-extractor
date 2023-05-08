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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ChunkInterpreter {
    private Map<String, Object> IHDRInfo;
    private static final int RAW_BYTES_LENGTH_LIMIT = 200;

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
        List<String> rawBytes = rawChunk.length() > RAW_BYTES_LENGTH_LIMIT
                ? getFirstAndLastStrings(rawChunk.rawBytes(), RAW_BYTES_LENGTH_LIMIT / 2)
                : rawChunk.rawBytes();
        return new Chunk(rawChunk.type(), rawChunk.length(), rawChunk.offset(), rawBytes, properties, rawChunk.CRC());
    }

    public static List<String> getFirstAndLastStrings(List<String> strings, int leftElements) {
        return Stream.concat(
                Stream.concat(strings.stream().limit(leftElements), Stream.of("...")),
                strings.stream().skip(strings.size() - leftElements)
        ).collect(Collectors.toList());
    }

    private Analyser matchAnalyserToChunkType(String type) {
        return switch (type) {
            case "IHDR" -> new IHDRAnalyser();
            case "gAMA" -> new gAMAAnalyser();
            case "PLTE" -> new PLTEAnalyser((int) IHDRInfo.get("Color type"), (int) IHDRInfo.get("Bit depth"));
            case "tIME" -> new tIMEAnalyser();
            case "tEXt" -> new tEXtAnalyser();
            case "zTXt" -> new zTXtAnalyser();
            case "pHYs" -> new pHYsAnalyser();
            case "cHRM" -> new cHRMAnalyser();
            default -> null;
        };
    }
}