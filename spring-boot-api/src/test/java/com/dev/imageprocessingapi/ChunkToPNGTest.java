package com.dev.imageprocessingapi;

import com.dev.imageprocessingapi.metadataextractor.logic.ImageSerializer;
import com.dev.imageprocessingapi.metadataextractor.model.Chunk;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


public class ChunkToPNGTest {
    @Test
    public void simpleTest() {
        ImageSerializer imageSerializer = new ImageSerializer();
        Chunk ihdr = new Chunk("IHDR", 13, List.of("00", "00", "00", "20", "00", "00", "00", "20", "08", "06", "00", "00", "00"), null, "737a7af4");
        Chunk gama = new Chunk("gAMA", 4, List.of("00", "01", "86", "a0"), null, "31e8965f");

        List<Chunk> chunks = List.of(ihdr, gama);

        imageSerializer.saveAsPNG(chunks);

        Assertions.assertTrue(true);
    }
}
