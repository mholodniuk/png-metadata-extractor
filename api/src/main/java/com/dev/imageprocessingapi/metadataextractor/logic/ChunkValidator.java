package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.metadataextractor.domain.Chunk;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import com.dev.imageprocessingapi.model.Image;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils.calculateCRC;

@Slf4j
@Component
@AllArgsConstructor
public class ChunkValidator {
    private final ImageMetadataParser parser;

    public Map<String, Boolean> validate(Image image) {
        var metadata = parser.processImage(image);
        var result = new LinkedHashMap<String, Boolean>();

        for (Chunk chunk : metadata.chunks()) {
            var crc = chunk.CRC();
            var recalculatedCRC = calculateCRC(ConversionUtils.parseHex(String.join("", chunk.rawBytes())), chunk.type());
            result.put(chunk.type(), crc.equals(recalculatedCRC));
        }

        return result;
    }
}
