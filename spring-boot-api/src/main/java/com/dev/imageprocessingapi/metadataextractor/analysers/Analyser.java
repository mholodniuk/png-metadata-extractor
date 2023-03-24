package com.dev.imageprocessingapi.metadataextractor.analysers;

import java.util.List;
import java.util.Map;

public interface Analyser {
    Map<String, Object> analyse(List<String> rawBytes);
}
