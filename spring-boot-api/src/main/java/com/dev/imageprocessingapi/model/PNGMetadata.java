package com.dev.imageprocessingapi.model;

import com.dev.imageprocessingapi.metadataextractor.chunks.Chunk;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class PNGMetadata {
    private boolean isValidPNG;
    List<Chunk> chunks;
    List<String> errors;
}
