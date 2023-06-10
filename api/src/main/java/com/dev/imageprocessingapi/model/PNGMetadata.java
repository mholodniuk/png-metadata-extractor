package com.dev.imageprocessingapi.model;

import com.dev.imageprocessingapi.metadataextractor.domain.Chunk;

import java.util.List;

public record PNGMetadata(String id, boolean isValidPNG, List<Chunk> chunks) {
}
