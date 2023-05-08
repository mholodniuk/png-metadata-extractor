package com.dev.imageprocessingapi.metadataextractor.model;

import java.util.List;
import java.util.Map;

public record Chunk(String type,
                    int length,
                    int offset,
                    List<String> rawBytes,
                    Map<String, Object> properties,
                    String CRC,
                    boolean isCritical,
                    boolean isPublic,
                    boolean isReserved,
                    boolean isUnsafeToCopy
) {
    public Chunk(String type, int length, int offset, List<String> rawBytes, Map<String, Object> properties, String CRC) {
        this(type, length, offset, rawBytes, properties, CRC,
                Character.isUpperCase(type.charAt(0)),
                Character.isUpperCase(type.charAt(1)),
                Character.isUpperCase(type.charAt(2)),
                Character.isUpperCase(type.charAt(3)));
    }
}
