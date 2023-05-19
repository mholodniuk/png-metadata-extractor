package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.tIMEAnalyser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class tIMEAnalyserTest {
    private final tIMEAnalyser analyser = new tIMEAnalyser();

    @Test
    public void testAnalyse() {
        byte[] rawBytes = new byte[]{0x07, (byte) 0xe3, 0x01, 0x07, 0x17, 0x0c, 0x10};
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

    @Test
    void testInvalidData() {
        byte[] rawBytes = new byte[]{0x00, 0x00, 0x6d, (byte) 0x98, 0x00, 0x00, 0x73, (byte) 0x8e};

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> analyser.analyse(rawBytes));
    }
}
