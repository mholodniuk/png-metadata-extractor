package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils.convertHexByteArrayToSimpleString;
import static com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils.decompressZlib;

public class zTXtAnalyser implements Analyser {
    @Override
    public Map<String, Object> analyse(byte[] rawBytes) {
        var result = new HashMap<String, Object>();
        var textualData = ConversionUtils.formatHex(rawBytes);
        var data = ConversionUtils.convertHexStringToSimpleString(ConversionUtils.formatHex(rawBytes));

        int separatorIndex = data.indexOf("\0");
        var keyword = data.substring(0, separatorIndex);
        result.put("Keyword", keyword);
        var compressionMethod = data.substring(separatorIndex, separatorIndex + 1);

        if (compressionMethod.equals("\0")) {
            result.put("Compression method", 0);
            byte[] byteArray = ConversionUtils.parseHexString(textualData);
            byte[] decompressedTextArray;
            try {
                decompressedTextArray = decompressZlib(Arrays.copyOfRange(byteArray, separatorIndex + 2, byteArray.length));
            } catch (Exception e) {
                // todo: add throw some new exception (InvalidChunkAnalysisException) code: 500???
                return null;
            }
            result.put("Text string", convertHexByteArrayToSimpleString(decompressedTextArray));
        } else {
            result.put("Compression method", "Unknown");
            result.put("Raw text string", textualData);
        }

        return result;
    }
}
