package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.tIMEAnalyser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tIMEAnalyserTest {
    tIMEAnalyser analyser = new tIMEAnalyser();

    @Test
    public void testAnalyse() {
        List<String> rawBytes = Arrays.asList("07", "e3", "01", "07", "17", "0c", "10");
        Map<String, Object> expectedProperties = new HashMap<>();
        expectedProperties.put("Year", 2019);
        expectedProperties.put("Month", 1);
        expectedProperties.put("Day", 7);
        expectedProperties.put("Hour", 23);
        expectedProperties.put("Minute", 12);
        expectedProperties.put("Second", 16);

        Map<String, Object> properties = analyser.analyse(rawBytes);

        assertEquals(expectedProperties, properties);
    }
}
