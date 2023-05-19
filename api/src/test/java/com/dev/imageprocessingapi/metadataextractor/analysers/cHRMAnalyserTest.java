package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.cHRMAnalyser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class cHRMAnalyserTest {
    private final cHRMAnalyser analyser = new cHRMAnalyser();
    @Test
    void testAnalyse() {
        List<String> rawBytes = Arrays.asList(
                "00", "00", "6d", "98", "00", "00", "73", "8e",
                "00", "00", "f7", "1a", "00", "00", "85", "8c",
                "00", "00", "76", "42", "00", "00", "eb", "f8",
                "00", "00", "2f", "a8", "00", "00", "13", "cc"
        );

        byte[] byteArray = new byte[rawBytes.size()];
        for (int i = 0; i < rawBytes.size(); i++) {
            byteArray[i] = (byte) Integer.parseInt(rawBytes.get(i), 16);
        }

        Map<String, Object> result = analyser.analyse(byteArray);

        Assertions.assertEquals(0.28056, result.get("White point X"));
        Assertions.assertEquals(0.29582, result.get("White point Y"));
        Assertions.assertEquals(0.34188, result.get("Red Y"));
        Assertions.assertEquals(0.30274, result.get("Green X"));
        Assertions.assertEquals(0.122, result.get("Blue X"));
        Assertions.assertEquals(0.63258, result.get("Red X"));
        Assertions.assertEquals(0.05068, result.get("Blue Y"));
        Assertions.assertEquals(0.60408, result.get("Green Y"));
    }

    @Test
    void testInvalidData() {
        byte[] rawBytes = new byte[]{0x00, 0x00, 0x6d, (byte) 0x98, 0x00, 0x00, 0x73, (byte) 0x8e};

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> analyser.analyse(rawBytes));
    }
}
