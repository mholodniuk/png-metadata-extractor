package com.dev.imageprocessingapi.metadataextractor.chunks;

import java.util.List;

public class gAMA extends Chunk {
    public gAMA(String type, int length, List<String> rawBytes, String CRC) {
        super(type, length, rawBytes, CRC);
    }
}
