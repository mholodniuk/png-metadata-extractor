package com.dev.imageprocessingapi.metadataextractor.chunks;

import java.util.List;

public class PLTE extends Chunk {
    public PLTE(String type, int length, List<String> rawBytes, String CRC) {
        super(type, length, rawBytes, CRC);
    }
}
