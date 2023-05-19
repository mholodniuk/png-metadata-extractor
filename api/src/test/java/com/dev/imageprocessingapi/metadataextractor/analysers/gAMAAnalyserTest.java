package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.gAMAAnalyser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class gAMAAnalyserTest {
    private final gAMAAnalyser analyser = new gAMAAnalyser();

    @Test
    public void testSimpleAnalyse() {
        byte[] rawBytes = new byte[]{0x00, 0x01, (byte) 0x86, (byte) 0xa0};
        Map<String, Object> expected = Map.of("Gamma", 1.0);

        Map<String, Object> result = analyser.analyse(rawBytes);

        assertEquals(expected, result);
    }

    @Test
    public void testComplexAnalyse() {
        byte[] rawBytes = new byte[]{0x00, 0x00, (byte) 0xb1, (byte) 0x8f};

        Map<String, Object> expected = Map.of("Gamma", 0.45455);

        Map<String, Object> result = analyser.analyse(rawBytes);

        assertEquals(expected, result);
    }
}
