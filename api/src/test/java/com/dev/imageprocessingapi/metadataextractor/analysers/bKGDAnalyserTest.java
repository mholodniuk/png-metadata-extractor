package com.dev.imageprocessingapi.metadataextractor.analysers;

import com.dev.imageprocessingapi.metadataextractor.analysers.impl.bKGDAnalyser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class bKGDAnalyserTest {

    @Test
    public void testAnalysePaletteIndex() {
        byte[] rawBytes = new byte[]{0x00};
        int colorType = 3;
        bKGDAnalyser analyser = new bKGDAnalyser(colorType);

        Map<String, Object> result = analyser.analyse(rawBytes);

        Assertions.assertEquals(0, result.get("Palette index"));
    }

    @Test
    public void testAnalyseGray() {
        byte[] rawBytes = new byte[]{0x00, 0x7f};

        int colorType = 0;
        bKGDAnalyser analyser = new bKGDAnalyser(colorType);

        Map<String, Object> result = analyser.analyse(rawBytes);

        Assertions.assertEquals(127, result.get("Gray"));
    }

    @Test
    public void testAnalyseRGB() {
        byte[] rawBytes = new byte[]{0x00, (byte) 0xff, 0x00, (byte) 0x80, 0x00, 0x01};
        int colorType = 2;
        bKGDAnalyser analyser = new bKGDAnalyser(colorType);

        Map<String, Object> result = analyser.analyse(rawBytes);

        Assertions.assertEquals(255, result.get("Red"));
        Assertions.assertEquals(128, result.get("Green"));
        Assertions.assertEquals(1, result.get("Blue"));
    }

    @Test
    public void testInvalidData() {
//        List<String> rawBytes = Arrays.asList("00", "00", "00");
        byte[] rawBytes = new byte[]{0x00, 0x00, 0x00};
        int colorType = 4;
        bKGDAnalyser analyser = new bKGDAnalyser(colorType);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> analyser.analyse(rawBytes));
    }
}
