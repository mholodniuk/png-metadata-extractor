package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.IHDRAnalyser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IHDRAnalyserTest {
    private final IHDRAnalyser analyser = new IHDRAnalyser();

    @Test
    void testAnalyse() {
        byte[] rawBytes = new byte[]{0x00, 0x00, 0x00, 0x20, 0x00,
                0x00, 0x00, 0x20, 0x08, 0x06, 0x00, 0x00, 0x00};

        Map<String, Object> expectedProperties = Map.of(
                "Compression method", 0,
                "Interlace method", 0,
                "Height", 32,
                "Width", 32,
                "Bit depth", 8,
                "Color type", 6,
                "Filter method", 0);

        Map<String, Object> actualProperties = analyser.analyse(rawBytes);

        assertEquals(expectedProperties, actualProperties);
    }
}
