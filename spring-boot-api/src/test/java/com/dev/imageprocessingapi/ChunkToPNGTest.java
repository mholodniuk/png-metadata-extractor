package com.dev.imageprocessingapi;

import com.dev.imageprocessingapi.metadataextractor.ImageSerializer;
import com.dev.imageprocessingapi.metadataextractor.chunks.Chunk;
import com.dev.imageprocessingapi.metadataextractor.chunks.IHDR;
import com.dev.imageprocessingapi.metadataextractor.chunks.gAMA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


public class ChunkToPNGTest {
    @Test
    public void simpleTest() {
        ImageSerializer imageSerializer = new ImageSerializer();
        Chunk ihdr = new IHDR("IHDR", 13, List.of("00", "00", "00", "20", "00", "00", "00", "20", "08", "06", "00", "00", "00"), "737a7af4");
        Chunk gama = new gAMA("gAMA", 4, List.of("00", "01", "86", "a0"), "31e8965f");

        List<Chunk> chunks = List.of(ihdr, gama);

        imageSerializer.saveAsPNG(chunks);

        Assertions.assertTrue(true);
    }
}
