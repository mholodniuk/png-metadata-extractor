package com.dev.imageprocessingapi.metadataextractor.utils;

import java.util.HexFormat;

public class ConversionUtils {

    private static final HexFormat hex = HexFormat.of();

    public static String encodeHexString(byte[] byteArray) {
        return hex.formatHex(byteArray);
    }

    public static String byteToHex(byte num) {
        return hex.toHexDigits(num);
    }

    public static byte[] decodeHexString(String hexString) {
        return hex.parseHex(hexString);
    }

    public static byte[] encodeInteger(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static String convertStringToHex(String str) {
        StringBuilder hex = new StringBuilder();
        for (char temp : str.toCharArray()) {
            hex.append(Integer.toHexString(temp));
        }
        return hex.toString();
    }
    public static String convertHexToString(String hex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String tempInHex = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(tempInHex, 16);
            result.append((char) decimal);
        }
        return result.toString();
    }
}
