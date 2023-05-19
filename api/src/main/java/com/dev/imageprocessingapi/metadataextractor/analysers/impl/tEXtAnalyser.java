package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

public class tEXtAnalyser implements Analyser {
    @Override
    public Map<String, Object> analyse(byte[] rawBytes) {
        var result = new HashMap<String, Object>();

        var textualData = ConversionUtils.formatHex(rawBytes);
        var data = ConversionUtils.convertHexStringToSimpleString(textualData);
        int separatorIndex = data.indexOf("\0");

        var keyword = data.substring(0, separatorIndex);
        var text = data.substring(separatorIndex + 1);

        result.put("Keyword", keyword);
        result.put("Text string", text);

        return result;
    }
}
