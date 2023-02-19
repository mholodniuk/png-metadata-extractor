package com.dev.imageprocessingapi.consts;

import com.dev.imageprocessingapi.utils.ConversionUtils;

public interface PNGChunksDefinitions {
    String IHDR_HEX = HexDefinitions.IHDR.getHexDefinition();
    String IEND_HEX = HexDefinitions.IEND.getHexDefinition();
    String IHDR = "IHDR";
    String IEND = "IEND";
    String PNG_HEADER_HEX = "89504e470d0a1a0a";
    enum HexDefinitions {
        IHDR(ConversionUtils.convertStringToHex("IHDR")),
        IEND(ConversionUtils.convertStringToHex("IEND")),
        PNG(ConversionUtils.convertStringToHex("PNG"));

        private final String hexDefinition;
        HexDefinitions(String chunkType) {
            hexDefinition = chunkType;
        }
        public String getHexDefinition() {
            return hexDefinition;
        }
    }
}
