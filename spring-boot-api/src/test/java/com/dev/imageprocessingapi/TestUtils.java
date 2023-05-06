package com.dev.imageprocessingapi;

import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;

import java.util.List;

public interface TestUtils {
    List<RawChunk> MOCK_CHUNKS =
            List.of(
                    new RawChunk("IHDR", 4, 4, List.of("00", "00", "00"), "c1"),
                    new RawChunk("PLTE", 4, 8, List.of("00", "00", "00"), "c2"),
                    new RawChunk("gAMA", 4, 12, List.of("00", "00", "00"), "c3"),
                    new RawChunk("cHRM", 4, 16, List.of("00", "00", "00"), "c4"),
                    new RawChunk("IDAT", 4, 20, List.of("00", "00", "00"), "c5"),
                    new RawChunk("IDAT", 4, 24, List.of("00", "00", "00"), "c6"),
                    new RawChunk("IEND", 4, 28, List.of("00", "00", "00"), "c7"));

    List<String> CRITICAL_CHUNKS = List.of("IHDR", "PLTE", "IDAT", "IEND");

}
