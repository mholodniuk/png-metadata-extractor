package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

public class gAMAAnalyser implements Analyser {
    @Override
    public Map<String, Object> analyse(byte[] rawBytes) {
        var result = new HashMap<String, Object>();

        int gamma = ConversionUtils.fromHexDigits(ConversionUtils.formatHex(rawBytes));
        result.put("Gamma", (double) gamma / 100_000);

        return result;
    }
}
