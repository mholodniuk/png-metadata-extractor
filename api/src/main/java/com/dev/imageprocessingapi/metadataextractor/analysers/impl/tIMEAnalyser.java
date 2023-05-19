package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

public class tIMEAnalyser implements Analyser {
    private int iterator = 0;

    @Override
    public Map<String, Object> analyse(byte[] rawBytes) {
        if (rawBytes.length != 7) {
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

    private int getIntValue(byte[] rawBytes, int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; iterator++, i++) {
            bytes[i] = rawBytes[iterator];
        }
        return ConversionUtils.fromHexDigits(ConversionUtils.formatHex(bytes));
    }
}
