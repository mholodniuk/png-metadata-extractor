package com.dev.imageprocessingapi.metadataextractor.logic;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChunkInterpreterTest {

    @Test
    public void testGetFirstAndLastStrings() {
        List<String> input = Arrays.asList("aa", "bb", "cc", "dd");
        List<String> expectedOutput = Arrays.asList("aa", "...", "dd");
        assertEquals(expectedOutput, ChunkInterpreter.getFirstAndLastStrings(input, 1));
    }
}
