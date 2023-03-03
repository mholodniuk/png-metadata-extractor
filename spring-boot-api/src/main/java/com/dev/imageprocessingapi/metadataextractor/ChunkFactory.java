package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.metadataextractor.chunks.*;

import java.util.List;

public class ChunkFactory {
    public static Chunk create(String type, int length, List<String> rawBytes, String CRC) {
        return switch (type) {
            case "IHDR" -> new IHDR(Chunk.TYPE.IHDR, length, rawBytes, CRC);
            case "gAMA" -> new gAMA(Chunk.TYPE.gAMA, length, rawBytes, CRC);
            case "IDAT" -> new IDAT(Chunk.TYPE.IDAT, length, rawBytes, CRC);
            case "IEND" -> new IEND(Chunk.TYPE.IEND, length, rawBytes, CRC);
            default -> new Chunk(Chunk.TYPE.UNKNOWN, length, rawBytes, CRC);
        };
    }
}