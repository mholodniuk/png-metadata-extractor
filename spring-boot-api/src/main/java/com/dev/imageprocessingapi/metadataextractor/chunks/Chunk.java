package com.dev.imageprocessingapi.metadataextractor.chunks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class Chunk {
    public static final int LENGTH_FIELD_LEN = 4;
    public static final int TYPE_FIELD_LEN = 4;
    public static final int CRC_FIELD_LEN = 4;

    public enum TYPE {
        IHDR, gAMA, IDAT, IEND, UNKNOWN
    }

    private TYPE type;
    private final int length;
    private final List<String> rawBytes;
    private final String CRC;
}
