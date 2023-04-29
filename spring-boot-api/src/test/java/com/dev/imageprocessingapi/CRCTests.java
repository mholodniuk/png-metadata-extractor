package com.dev.imageprocessingapi;

import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.zip.CRC32;

public class CRCTests {
    @Test
    void crcTest() {
        String expectedCRC = "7419f340";
        List<String> bytes = List.of(
                "49", "48", "44",
                "52", "00", "00", "04", "86", "00", "00", "03", "ED",
                "08", "02", "00", "00", "00");

        CRC32 crc = new CRC32();
        crc.update(ConversionUtils.parseHexString(String.join("", bytes)));

        Assertions.assertEquals(expectedCRC, Long.toHexString(crc.getValue()));
    }
}
