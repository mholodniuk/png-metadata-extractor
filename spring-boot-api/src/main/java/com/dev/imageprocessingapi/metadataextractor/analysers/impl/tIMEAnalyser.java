package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// todo: add listener after operation on an image to update tIME chunk
public class tIMEAnalyser implements Analyser {
    private Integer iterator = 0;

    @Override
    public Map<String, Object> analyse(List<String> rawBytes) {
        if (rawBytes.size() != 7) {
            throw new IllegalArgumentException("tIME chunk should contain 7 bytes of data");
        }
        var result = new HashMap<String, Object>();

        int year = getIntValue(rawBytes, 2);
        result.put("Year", year);
        int Month = getIntValue(rawBytes, 1);
        result.put("Month", Month);
        int day = getIntValue(rawBytes, 1);
        result.put("Day", day);
        int hour = getIntValue(rawBytes, 1);
        result.put("Hour", hour);
        int minute = getIntValue(rawBytes, 1);
        result.put("Minute", minute);
        int second = getIntValue(rawBytes, 1);
        result.put("Second", second);

        return result;
    }

    private int getIntValue(List<String> rawBytes, int size) {
        var hex = rawBytes.stream().skip(iterator).limit(size).collect(Collectors.joining());
        iterator += size;

        return ConversionUtils.fromHexDigits(hex);
    }
}
