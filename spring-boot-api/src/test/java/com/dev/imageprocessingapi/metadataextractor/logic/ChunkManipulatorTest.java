package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.exception.InvalidChunkDeletionException;
import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
import com.dev.imageprocessingapi.model.Image;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@SpringJUnitConfig(classes = {ImageManipulator.class})
public class ChunkManipulatorTest {
    @MockBean
    private ImageMetadataParser parser;
    @Autowired
    private ImageManipulator manipulator;

    @Test
    @DisplayName("Before chunk removing, image should be parsed")
    void shouldParseImageChunks() {
        var image = new Image();
        given(parser.readRawChunks(image)).willReturn(chunks());

        manipulator.removeAncillaryChunks(image);

        then(parser).should().readRawChunks(image);
    }

    @Test
    @DisplayName("Only ancillary chunks should be removed")
    void removeAncillaryChunks() {
        var image = new Image();
        given(parser.readRawChunks(image)).willReturn(chunks());

        var chunks = manipulator.removeAncillaryChunks(image);

        Assertions.assertEquals(5, chunks.size());
        Assertions.assertEquals(0, chunks.stream().filter(c -> !criticalChunks.contains(c.type())).count());
    }

    @Test
    @DisplayName("Only ancillary chunks can be removed")
    void cannotRemoveCriticalChunk() {
        var image = new Image();
        given(parser.readRawChunks(image)).willReturn(chunks());

        Assertions.assertThrows(InvalidChunkDeletionException.class,
                () -> manipulator.removeGivenChunks(image, List.of("PLTE", "IEND")));
    }

    @Test
    @DisplayName("Selected chunks are removed")
    void removeSelectedChunk() {
        var image = new Image();
        var chunksToDelete = List.of("gAMA", "cHRM");
        given(parser.readRawChunks(image)).willReturn(chunks());

        var chunks = manipulator.removeGivenChunks(image, chunksToDelete);

        Assertions.assertEquals(5, chunks.size());
        Assertions.assertEquals(0, chunks.stream().filter(c -> chunksToDelete.contains(c.type())).count());
    }

    private static List<RawChunk> chunks() {
        return List.of(
                new RawChunk("IHDR", 4, 4, null, "CRC1"),
                new RawChunk("PLTE", 4, 8, null, "CRC2"),
                new RawChunk("gAMA", 4, 12, null, "CRC3"),
                new RawChunk("cHRM", 4, 16, null, "CRC4"),
                new RawChunk("IDAT", 4, 20, null, "CRC5"),
                new RawChunk("IDAT", 4, 24, null, "CRC6"),
                new RawChunk("IEND", 4, 28, null, "CRC7"));
    }

    private static final List<String> criticalChunks = List.of("IHDR", "PLTE", "IDAT", "IEND");
}
