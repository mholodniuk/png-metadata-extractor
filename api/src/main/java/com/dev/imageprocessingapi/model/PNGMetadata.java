package com.dev.imageprocessingapi.model;

import com.dev.imageprocessingapi.metadataextractor.dto.Chunk;

import java.util.List;

public record PNGMetadata(String id, boolean isValidPNG, List<Chunk> chunks) {
}
