package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class bKGDAnalyser implements Analyser {
    private final int colorType;
    private int iterator;

    @Override
    public Map<String, Object> analyse(List<String> rawBytes) {
        var result = new HashMap<String, Object>();

        if (colorType == 3 && rawBytes.size() == 1) {
            result.put("Palette index", getIntValue(rawBytes, 1));
        }
        else if ((colorType == 0 || colorType == 4) && rawBytes.size() == 2) {
            result.put("Gray", getIntValue(rawBytes, 2));
        }
        else if ((colorType == 2 || colorType == 6) && rawBytes.size() == 6) {
            int red = getIntValue(rawBytes, 2);
            int green = getIntValue(rawBytes, 2);
            int blue = getIntValue(rawBytes, 2);

            result.put("Red", red);
            result.put("Green", green);
            result.put("Blue", blue);
        } else {
            throw new IllegalArgumentException("bKGD chunk contains invalid data or I am wrong");
        }

        return result;
    }

    private int getIntValue(List<String> rawBytes, int size) {
        var hex = rawBytes.stream().skip(iterator).limit(size).collect(Collectors.joining());
        iterator += size;

        return ConversionUtils.fromHexDigits(hex);
    }
}
