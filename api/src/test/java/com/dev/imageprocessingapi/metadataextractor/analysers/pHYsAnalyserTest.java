package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.pHYsAnalyser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class pHYsAnalyserTest {
    private final pHYsAnalyser analyser = new pHYsAnalyser();

    @Test
    public void testSimpleAnalyse() {
        byte[] rawBytes = new byte[]{0x00, 0x00, 0x0b, 0x13, 0x00, 0x00, 0x0b, 0x13, 0x01};
        Map<String, Object> expected = Map.of(
                "Pixels per unit Y", 2835,
                "Pixels per unit X", 2835,
                "Unit specifier", 1);

        Map<String, Object> result = analyser.analyse(rawBytes);

        assertEquals(expected, result);
    }
}
