package com.dev.imageprocessingapi.model.chunks;

import java.util.List;

public class IHDR extends Chunk {
    public IHDR(TYPE type, int length, List<String> rawBytes, String CRC, byte[] data) {
        super(type, length, rawBytes, CRC, data);
    }
}
