package com.dev.imageprocessingapi;

import com.dev.imageprocessingapi.consts.PNGChunksDefinitions;
import com.dev.imageprocessingapi.utils.ConversionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.codec.Hex;

import java.util.HexFormat;

@SpringBootTest
public class ConversionUtilsTests {
    @Test
    void StringEncodingTest() {
        String hexStringPNG = ConversionUtils.convertStringToHex("PNG");
        byte[] decodedHexString = ConversionUtils.decodeHexString(hexStringPNG);
        String encodedHexString = ConversionUtils.encodeHexString(decodedHexString);

        Assertions.assertEquals(hexStringPNG, encodedHexString);
    }

    @Test
    void IHDRChunkDetection() {
        byte[] decodedHexString = ConversionUtils.decodeHexString(PNGChunksDefinitions.IHDR_HEX);
        String encodedHexString = ConversionUtils.encodeHexString(decodedHexString);

        System.out.println(PNGChunksDefinitions.HexDefinitions.IHDR.getHexDefinition());

        Assertions.assertEquals(PNGChunksDefinitions.IHDR, ConversionUtils.convertHexToString(encodedHexString));
    }

    @Test
    void TestINGGG() {
        byte[] bytes = "IHDR".getBytes();
        System.out.println(bytes);

        HexFormat hex = HexFormat.of();
        var hexFormatDecode = hex.formatHex(bytes);
        var springDecode = new String(Hex.encode(bytes));

        System.out.println(hexFormatDecode);
        System.out.println(springDecode);
    }
}
