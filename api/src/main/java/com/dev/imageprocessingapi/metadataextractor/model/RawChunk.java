package com.dev.imageprocessingapi.metadataextractor.model;

public record RawChunk(String type,
                       int length,
                       int offset,
                       byte[] rawBytes,
                       String CRC
                    ) {
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int TYPE_FIELD_LENGTH = 4;
    public static final int CRC_FIELD_LENGTH = 4;
}
