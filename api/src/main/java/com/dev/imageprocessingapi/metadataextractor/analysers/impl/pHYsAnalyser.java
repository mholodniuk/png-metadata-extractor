package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class pHYsAnalyser implements Analyser {
    private int iterator = 0;
    @Override
    public Map<String, Object> analyse(List<String> rawBytes) {
        if (rawBytes.size() != 9) {
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

    private int getIntValue(List<String> rawBytes, int size) {
        var hex = rawBytes.stream().skip(iterator).limit(size).collect(Collectors.joining());
        iterator += size;
        return ConversionUtils.fromHexDigits(hex);
    }
}

