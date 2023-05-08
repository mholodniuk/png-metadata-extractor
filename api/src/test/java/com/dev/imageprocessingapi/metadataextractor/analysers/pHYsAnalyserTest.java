package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.pHYsAnalyser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class pHYsAnalyserTest {
    pHYsAnalyser analyser = new pHYsAnalyser();

    @Test
    public void testSimpleAnalyse() {
        List<String> rawBytes = List.of("00", "00", "0b", "13", "00", "00", "0b", "13", "01");
        Map<String, Object> expected = Map.of(
                "Pixels per unit Y", 2835,
                "Pixels per unit X", 2835,
                "Unit specifier", 1);

        Map<String, Object> result = analyser.analyse(rawBytes);

        assertEquals(expected, result);
    }
}
