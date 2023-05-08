package com.dev.imageprocessingapi.metadataextractor.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HexFormat;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ConversionUtils {

    private static final HexFormat hex = HexFormat.of();

    public static String formatHex(byte[] byteArray) {
        return hex.formatHex(byteArray);
    }

    public static String toHexDigits(byte num) {
        return hex.toHexDigits(num);
    }

    public static byte[] parseHexString(String hexString) {
        return hex.parseHex(hexString);
    }

    public static byte[] encodeInteger(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static int fromHexDigits(String hex) {
        return HexFormat.fromHexDigits(hex);
    }

    public static String convertSimpleStringToHexString(String str) {
        StringBuilder hex = new StringBuilder();
        for (char temp : str.toCharArray()) {
            hex.append(Integer.toHexString(temp));
        }
        return hex.toString();
    }
    public static String convertHexStringToSimpleString(String hex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String tempInHex = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(tempInHex, 16);
            result.append((char) decimal);
        }
        return result.toString();
    }

    public static String convertHexByteArrayToSimpleString(byte[] bytes) {
        return ConversionUtils.convertHexStringToSimpleString(ConversionUtils.formatHex(bytes));
    }

    public static byte[] decompressZlib(byte[] inputBytes) throws DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(inputBytes);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputBytes.length)) {
            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new DataFormatException("Failed to decompress Zlib data: " + e.getMessage());
        } finally {
            inflater.end();
        }
    }

    public static String calculateCRC(List<String> bytes, String type) {
        CRC32 crc = new CRC32();
        crc.update(ConversionUtils.parseHexString(ConversionUtils.convertSimpleStringToHexString(type) + String.join("", bytes)));
        return Long.toHexString(crc.getValue()).toUpperCase();
    }
}
