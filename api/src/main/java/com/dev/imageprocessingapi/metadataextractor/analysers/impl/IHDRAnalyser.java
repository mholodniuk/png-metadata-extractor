package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

public class IHDRAnalyser implements Analyser {
    private int iterator = 0;
    @Override
    public Map<String, Object> analyse(byte[] rawBytes) {
        var result = new HashMap<String, Object>();

        int width = getIntValue(rawBytes, 4);
        result.put("Width", width);
        int height = getIntValue(rawBytes, 4);
        result.put("Height", height);

        // todo: add map int value to string information
        int bitDepth = getIntValue(rawBytes, 1);
        result.put("Bit depth", bitDepth);
        int colorType = getIntValue(rawBytes, 1);
        result.put("Color type", colorType);
        int compressionMethod = getIntValue(rawBytes, 1);
        result.put("Compression method", compressionMethod);
        int filterMethod = getIntValue(rawBytes, 1);
        result.put("Filter method", filterMethod);
        int interlaceMethod = getIntValue(rawBytes, 1);
        result.put("Interlace method", interlaceMethod);

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
