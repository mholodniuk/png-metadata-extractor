package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.event.annotation.TrackExecutionTime;
import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.*;
import com.dev.imageprocessingapi.metadataextractor.domain.Chunk;
import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
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
    private static final int RAW_BYTES_LENGTH_LIMIT = 200;

    @TrackExecutionTime
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
                ? getFirstAndLastStrings(rawChunk.rawBytes())
                : map(rawChunk.rawBytes());
        return new Chunk(rawChunk.type(), rawChunk.length(), rawChunk.offset(), rawBytes, properties, rawChunk.CRC());
    }

    private static List<String> map(byte[] bytes) {
        List<String> result = new ArrayList<>();
        for (byte b : bytes) {
            result.add(ConversionUtils.toHexDigits(b));
        }
        return result;
    }

    private static List<String> getFirstAndLastStrings(byte[] bytes) {
        List<String> result = new ArrayList<>();
        int leftElements = RAW_BYTES_LENGTH_LIMIT / 2;

        for (int i = 0; i < Math.min(leftElements, bytes.length); i++) {
            result.add(ConversionUtils.toHexDigits(bytes[i]));
        }
        if (bytes.length > leftElements) {
            result.add("...");
        }
        for (int i = Math.max(bytes.length - leftElements, leftElements); i < bytes.length; i++) {
            result.add(ConversionUtils.toHexDigits(bytes[i]));
        }

        return result;
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
            case "bKGD" -> new bKGDAnalyser((int) IHDRInfo.get("Color type"));
            default -> null;
        };
    }
}