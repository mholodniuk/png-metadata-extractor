package com.dev.imageprocessingapi.metadataextractor.model;

import java.util.List;
import java.util.Map;

public record Chunk(String type,
                    int length,
                    List<String> rawBytes,
                    Map<String, Object> properties,
                    String CRC,
                    boolean isCritical,
                    boolean isPublic,
                    boolean isReserved,
                    boolean isUnsafeToCopy
                    ) {
    public Chunk(String type, int length, List<String> rawBytes, Map<String, Object> properties, String CRC) {
        this(type, length, rawBytes, properties, CRC,
                Character.isUpperCase(type.charAt(0)),
                Character.isUpperCase(type.charAt(1)),
                Character.isUpperCase(type.charAt(2)),
                Character.isUpperCase(type.charAt(3)));
    }
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int TYPE_FIELD_LENGTH = 4;
    public static final int CRC_FIELD_LENGTH = 4;
}
