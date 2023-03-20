package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.chunks.Chunk;

import java.util.Map;

public interface Analyser {
    Map<String, Integer> analyse(Chunk chunk);
}
