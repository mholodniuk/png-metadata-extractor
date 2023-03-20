package com.dev.imageprocessingapi;


import com.dev.imageprocessingapi.metadataextractor.chunks.IHDR;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class IHDRTest {
    @Test
    public void aaa() {
        List<String> hex = List.of("00", "00", "00", "20");

//        int result = IHDR.getIntValue(hex, hex.size());

//        Assertions.assertEquals(32, result);
    }
}
