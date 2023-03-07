package com.dev.imageprocessingapi.metadataextractor.chunks;

import java.util.List;

public class IEND extends Chunk {
    public IEND(String type, int length, List<String> rawBytes, String CRC) {
        super(type, length, rawBytes, CRC);
    }
}
