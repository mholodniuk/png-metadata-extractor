package com.dev.imageprocessingapi.metadataextractor.chunks;

import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

public class IHDR extends Chunk {
    public IHDR(String type, int length, List<String> rawBytes, String CRC) {
        super(type, length, rawBytes, CRC);
    }

    public static int getIntValue(List<String> rawBytes, int size) {
        String hexHeight = rawBytes.stream().limit(size).collect(Collectors.joining());
        return HexFormat.fromHexDigits(hexHeight);
    }
}
