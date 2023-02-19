package com.dev.imageprocessingapi.model.chunks;

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
        IHDR, gAMA, IDAT, IEND
    }

    public static TYPE assignType(String type) {
        return switch (type) {
            case "IHDR" -> TYPE.IHDR;
            case "IDAT" -> TYPE.IDAT;
            case "gAMA" -> TYPE.gAMA;
            case "IEND" -> TYPE.IEND;
            default -> throw new RuntimeException("");
        };
    }

    private TYPE type;
    private final int length;
    private final List<String> rawBytes;
    private final String CRC;
    @ToString.Exclude
    private final byte[] data;
}
