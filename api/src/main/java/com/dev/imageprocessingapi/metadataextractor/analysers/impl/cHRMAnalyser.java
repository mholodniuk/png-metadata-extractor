package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class cHRMAnalyser implements Analyser {
    private int iterator = 0;

    @Override
    public Map<String, Object> analyse(byte[] rawBytes) {
        if (rawBytes.length != 32) {
            throw new IllegalArgumentException("cHRM chunk should contain 32 bytes of data");
        }

        var result = new LinkedHashMap<String, Object>();

        double whitePointX = getDoubleValue(rawBytes);
        double whitePointY = getDoubleValue(rawBytes);
        result.put("White point X", whitePointX);
        result.put("White point Y", whitePointY);

        double redX = getDoubleValue(rawBytes);
        double redY = getDoubleValue(rawBytes);
        double greenX = getDoubleValue(rawBytes);
        double greenY = getDoubleValue(rawBytes);
        double blueX = getDoubleValue(rawBytes);
        double blueY = getDoubleValue(rawBytes);
        result.put("Red X", redX);
        result.put("Red Y", redY);
        result.put("Green X", greenX);
        result.put("Green Y", greenY);
        result.put("Blue X", blueX);
        result.put("Blue Y", blueY);

        return result;
    }

    private double getDoubleValue(byte[] rawBytes) {
        int value = getIntValue(rawBytes);
        return value / 100_000.0;
    }

    private int getIntValue(byte[] rawBytes) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; iterator++, i++) {
            bytes[i] = rawBytes[iterator];
        }
        return ConversionUtils.fromHexDigits(ConversionUtils.formatHex(bytes));
    }
}

