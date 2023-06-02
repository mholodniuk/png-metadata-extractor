package com.dev.imageprocessingapi;

import com.dev.imageprocessingapi.metadataextractor.dto.RawChunk;

import java.util.List;

public interface TestConst {
    List<RawChunk> MOCK_CHUNKS =
            List.of(
                    new RawChunk("IHDR", 4, 4, new byte[]{0x00, 0x00, 0x00}, "c1"),
                    new RawChunk("PLTE", 4, 8, new byte[]{0x00, 0x00, 0x00}, "c2"),
                    new RawChunk("gAMA", 4, 12, new byte[]{0x00, 0x00, 0x00}, "c3"),
                    new RawChunk("cHRM", 4, 16, new byte[]{0x00, 0x00, 0x00}, "c4"),
                    new RawChunk("IDAT", 4, 20, new byte[]{0x00, 0x00, 0x00}, "c5"),
                    new RawChunk("IDAT", 4, 24, new byte[]{0x00, 0x00, 0x00}, "c6"),
                    new RawChunk("IEND", 4, 28, new byte[]{0x00, 0x00, 0x00}, "c7"));

    List<String> CRITICAL_CHUNKS = List.of("IHDR", "PLTE", "IDAT", "IEND");

}
