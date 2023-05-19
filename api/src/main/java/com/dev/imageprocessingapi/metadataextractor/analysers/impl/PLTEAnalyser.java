package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
public class PLTEAnalyser implements Analyser {
    private final int colorType, bitDepth;

    @Override
    public Map<String, Object> analyse(byte[] rawBytes) {
        validateBytes(rawBytes);

        var result = new HashMap<String, Object>();
        List<Color> palette = new ArrayList<>();
        for (int i = 0; i < rawBytes.length; i += 3) {
            int red = Byte.toUnsignedInt(rawBytes[i]);
            int green = Byte.toUnsignedInt(rawBytes[i + 1]);
            int blue = Byte.toUnsignedInt(rawBytes[i + 2]);
            palette.add(new Color(red, green, blue));
        }
        result.put("Number of entries", palette.size());
        result.put("Palette", palette);

        return result;
    }

    private void validateBytes(byte[] bytes) {
        if (bytes.length % 3 != 0) {
            throw new IllegalArgumentException("PLTE chunk length is not divisible by 3");
        }
        int numEntries = bytes.length / 3;
        if (colorType == 0 || colorType == 4) {
            throw new IllegalArgumentException("PLTE chunk should not appear for color types 0 and 4");
        }
        if (colorType == 3 && numEntries > (1 << bitDepth) || numEntries > 256) {
            throw new IllegalArgumentException("Number of palette entries is out of range");
        }
        if (colorType != 3 && numEntries > 0) {
            throw new IllegalArgumentException("PLTE chunk should not appear for color types other than 3");
        }
    }

    record Color(int red, int green, int blue) {}
}

