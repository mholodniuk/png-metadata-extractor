package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

public class pHYsAnalyser implements Analyser {
    private int iterator = 0;
    @Override
    public Map<String, Object> analyse(byte[] rawBytes) {
        if (rawBytes.length != 9) {
            throw new IllegalArgumentException("pHYs chunk should contain 9 bytes of data");
        }

        var result = new HashMap<String, Object>();

        int pixelsPerUnitX = getIntValue(rawBytes, 4);
        result.put("Pixels per unit X", pixelsPerUnitX);

        int pixelsPerUnitY = getIntValue(rawBytes, 4);
        result.put("Pixels per unit Y", pixelsPerUnitY);

        int unitSpecifier = getIntValue(rawBytes, 1);
        result.put("Unit specifier", unitSpecifier);

        return result;
    }

    private int getIntValue(byte[] rawBytes, int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; iterator++, i++) {
            bytes[i] = rawBytes[iterator];
        }
        return ConversionUtils.fromHexDigits(ConversionUtils.formatHex(bytes));
    }
}

