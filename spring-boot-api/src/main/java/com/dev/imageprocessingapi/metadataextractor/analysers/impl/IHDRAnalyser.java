package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.chunks.Chunk;
import com.dev.imageprocessingapi.metadataextractor.chunks.IHDR;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IHDRAnalyser implements Analyser {
    private Integer iterator = 0;
    @Override
    public Map<String, Integer> analyse(Chunk chunk) {
        Map<String, Integer> result = new HashMap<>();
        if (!(chunk instanceof IHDR)) {
            throw new IllegalArgumentException("This should receive only IHDR chunks");
        }

        int width = getIntValue(chunk.getRawBytes(), 4);
        result.put("Width", width);
        int height = getIntValue(chunk.getRawBytes(), 4);
        result.put("Height", height);

        // todo: add map int value to string information
        int bitDepth = getIntValue(chunk.getRawBytes(), 1);
        result.put("Bit depth", bitDepth);
        int colorType = getIntValue(chunk.getRawBytes(), 1);
        result.put("Color type", colorType);
        int compressionMethod = getIntValue(chunk.getRawBytes(), 1);
        result.put("Compression method", compressionMethod);
        int filterMethod = getIntValue(chunk.getRawBytes(), 1);
        result.put("Filter method", filterMethod);
        int interlaceMethod = getIntValue(chunk.getRawBytes(), 1);
        result.put("Interlace method", interlaceMethod);

        iterator = 0;
        return result;
    }

    private int getIntValue(List<String> rawBytes, int size) {
        String hex = rawBytes.stream().skip(iterator).limit(size).collect(Collectors.joining());
        iterator += size;

        return ConversionUtils.fromHexDigits(hex);
    }
}
