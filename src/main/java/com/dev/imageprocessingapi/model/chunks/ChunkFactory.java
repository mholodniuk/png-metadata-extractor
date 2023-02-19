package com.dev.imageprocessingapi.model.chunks;

import java.util.Collections;

public class ChunkFactory {
    static Chunk create(String type) {
        return switch (type) {
            case "IHDR" -> new IHDR(Chunk.TYPE.IHDR, 0, Collections.emptyList(), null, null);
            case "gAMA" -> new IHDR(Chunk.TYPE.gAMA, 0, Collections.emptyList(), null, null);
            
            default -> throw new RuntimeException();
        };
    }
}