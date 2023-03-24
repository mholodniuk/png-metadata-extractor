package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gAMAAnalyser implements Analyser {
    @Override
    public Map<String, Object> analyse(List<String> rawBytes) {
        Map<String, Object> result = new HashMap<>();

        String hex = String.join("", rawBytes);
        int gamma = ConversionUtils.fromHexDigits(hex);
        result.put("Gamma", (double) gamma / 100000);

        return result;
    }
}
