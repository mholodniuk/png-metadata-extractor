package com.dev.imageprocessingapi.metadataextractor.chunks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@AllArgsConstructor
public class Chunk {
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int TYPE_FIELD_LENGTH = 4;
    public static final int CRC_FIELD_LENGTH = 4;

    private String type;
    private final int length;
//    private final int isCritical = 0; // todo
//    private final int isPublic = 0; // todo
//    private final int isReserved = 0; // todo
//    private final int isUnsafeToCopy = 0; // todo
    private final List<String> rawBytes;
    private final Map<String, Object> properties;
    private final String CRC;
}
