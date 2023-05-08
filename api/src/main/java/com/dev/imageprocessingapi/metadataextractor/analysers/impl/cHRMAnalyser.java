package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class cHRMAnalyser implements Analyser {
    private int iterator = 0;

    @Override
    public Map<String, Object> analyse(List<String> rawBytes) {
        if (rawBytes.size() != 32) {
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

    private double getDoubleValue(List<String> rawBytes) {
        int value = getIntValue(rawBytes);
        return value / 100_000.0;
    }

    private int getIntValue(List<String> rawBytes) {
        var hex = rawBytes.stream().skip(iterator).limit(4).collect(Collectors.joining());
        iterator += 4;
        return ConversionUtils.fromHexDigits(hex);
    }
}

