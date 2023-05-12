package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.tEXtAnalyser;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class tEXtAnalyserTest {
    private final tEXtAnalyser analyser = new tEXtAnalyser();

    @Test
    public void testAnalyse() {
        List<String> rawBytes = Arrays.asList(
                "53", "6f", "66", "74", "77", "61", "72",
                "65", "00", "41", "64", "6f", "62", "65",
                "20", "49", "6d", "61", "67", "65", "52",
                "65", "61", "64", "79");

        Map<String, Object> expectedProperties = Map.of(
                "Keyword", "Software",
                "Text string", "Adobe ImageReady");

        Map<String, Object> properties = analyser.analyse(rawBytes);

        assertEquals(expectedProperties, properties);
    }
}
