package com.dev.imageprocessingapi.metadataextractor.model;

import java.util.List;
import java.util.Map;

public record Chunk(String type,
                    int length,
                    List<String> rawBytes,
                    Map<String, Object> properties,
                    String CRC) {
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int TYPE_FIELD_LENGTH = 4;
    public static final int CRC_FIELD_LENGTH = 4;
////    private final int isCritical = 0; // todo
////    private final int isPublic = 0; // todo
////    private final int isReserved = 0; // todo
////    private final int isUnsafeToCopy = 0; // todo
}
