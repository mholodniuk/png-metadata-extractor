package com.dev.imageprocessingapi;

import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ConversionUtilsTest {
    private static final byte[] BYTE_ARRAY = {(byte) 0x01, (byte) 0x23, (byte) 0xAB, (byte) 0xCD};
    private static final String HEX_STRING = "0123abcd";
    private static final int INTEGER_VALUE = 123456;

    @Test
    public void testFormatHex() {
        String formattedHex = ConversionUtils.formatHex(BYTE_ARRAY);
        List<String> bytes = List.of("01", "23", "ab", "cd");
        Assertions.assertEquals(String.join("", bytes), formattedHex);
    }

    @Test
    public void testToHexDigits() {
        String hexDigits = ConversionUtils.toHexDigits((byte) 0xAB);
        Assertions.assertEquals("ab", hexDigits);
    }

    @Test
    public void testParseHexString() {
        byte[] byteArray = ConversionUtils.parseHexString(HEX_STRING);
        Assertions.assertArrayEquals(BYTE_ARRAY, byteArray);
    }

    @Test
    public void testEncodeInteger() {
        byte[] byteArray = ConversionUtils.encodeInteger(INTEGER_VALUE);
        byte[] expectedByteArray = {(byte) 0x00, (byte) 0x01, (byte) 0xE2, (byte) 0x40};
        Assertions.assertArrayEquals(expectedByteArray, byteArray);
    }

    @Test
    public void testFromHexDigits() {
        int intValue = ConversionUtils.fromHexDigits("7d");
        Assertions.assertEquals(125, intValue);
    }

    @Test
    public void testConvertSimpleStringToHexString() {
        String hexString = ConversionUtils.convertSimpleStringToHexString("hello");
        Assertions.assertEquals("68656c6c6f", hexString);
    }

    @Test
    public void testConvertHexStringToSimpleString() {
        String simpleString = ConversionUtils.convertHexStringToSimpleString("68656c6c6f");
        Assertions.assertEquals("hello", simpleString);
    }
}