package com.dev.imageprocessingapi;

import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.codec.Hex;

import java.util.HexFormat;

@SpringBootTest
public class ConversionUtilsTests {
    @Test
    void StringEncodingTest() {
        String hexStringPNG = ConversionUtils.convertSimpleStringToHexString("PNG");
        byte[] decodedHexString = ConversionUtils.parseHexString(hexStringPNG);
        String encodedHexString = ConversionUtils.formatHex(decodedHexString);

        Assertions.assertEquals(hexStringPNG, encodedHexString);
    }

    @Test
    void IHDRChunkDetection() {
        byte[] decodedHexString = "IHDR".getBytes();
        String encodedHexString = ConversionUtils.formatHex(decodedHexString);

        Assertions.assertEquals("IHDR", ConversionUtils.convertHexStringToSimpleString(encodedHexString));
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
