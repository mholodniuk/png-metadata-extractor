package com.dev.imageprocessingapi.metadataextractor.chunks;

import java.util.List;

public class IHDR extends Chunk {
    public IHDR(TYPE type, int length, List<String> rawBytes, String CRC) {
        super(type, length, rawBytes, CRC);
    }
}
