package com.dev.imageprocessingapi.metadataextractor.chunks;

import java.util.List;

public class IDAT extends Chunk {
    public IDAT(TYPE type, int length, List<String> rawBytes, String CRC) {
        super(type, length, rawBytes, CRC);
    }
}
