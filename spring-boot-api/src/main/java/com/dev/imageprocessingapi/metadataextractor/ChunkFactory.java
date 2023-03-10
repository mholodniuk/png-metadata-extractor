package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.metadataextractor.chunks.*;

import java.util.List;

public class ChunkFactory {
    public static Chunk create(String type, int length, List<String> rawBytes, String CRC) {
        return switch (type) {
            case "IHDR" -> new IHDR(type, length, rawBytes, CRC);
            case "gAMA" -> new gAMA(type, length, rawBytes, CRC);
            case "IDAT" -> new IDAT(type, length, rawBytes, CRC);
            case "PLTE" -> new PLTE(type, length, rawBytes, CRC);
            case "IEND" -> new IEND(type, length, rawBytes, CRC);
            default -> new Chunk(type, length, rawBytes, CRC);
        };
    }
}