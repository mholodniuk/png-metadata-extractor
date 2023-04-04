package com.dev.imageprocessingapi.metadataextractor.analysers.impl;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;


@AllArgsConstructor
public class PLTEAnalyser implements Analyser {
    private int colorType;

    @Override
    public Map<String, Object> analyse(List<String> rawBytes) {
        System.out.println("Received color type: " + colorType);
        return null;
    }
}
