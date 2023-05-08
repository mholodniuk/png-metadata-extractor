package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.gAMAAnalyser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class gAMAAnalyserTest {
    gAMAAnalyser analyser = new gAMAAnalyser();

    @Test
    public void testSimpleAnalyse() {
        List<String> rawBytes = List.of("00", "01", "86", "a0");
        Map<String, Object> expected = Map.of("Gamma", 1.0);

        Map<String, Object> result = analyser.analyse(rawBytes);

        assertEquals(expected, result);
    }

    @Test
    public void testComplexAnalyse() {
        List<String> rawBytes = List.of("00", "00", "b1", "8f");
        Map<String, Object> expected = Map.of("Gamma", 0.45455);

        Map<String, Object> result = analyser.analyse(rawBytes);

        assertEquals(expected, result);
    }
}
