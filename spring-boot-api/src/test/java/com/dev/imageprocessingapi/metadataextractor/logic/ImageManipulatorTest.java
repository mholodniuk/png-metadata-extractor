package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.exception.InvalidChunkDeletionException;
import com.dev.imageprocessingapi.model.Image;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static com.dev.imageprocessingapi.TestUtils.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@SpringJUnitConfig(classes = {ImageManipulator.class})
public class ImageManipulatorTest {
    @MockBean
    private ImageMetadataParser parser;
    @Autowired
    private ImageManipulator manipulator;

    @Test
    @DisplayName("Before chunk removing, image should be parsed")
    void shouldParseImageChunks() {
        var image = new Image();
        given(parser.readRawChunks(image)).willReturn(MOCK_CHUNKS);

        manipulator.removeAncillaryChunks(image);

        then(parser).should().readRawChunks(image);
    }

    @Test
    @DisplayName("Only ancillary chunks should be removed")
    void removeAncillaryChunks() {
        var image = new Image();
        given(parser.readRawChunks(image)).willReturn(MOCK_CHUNKS);

        var chunks = manipulator.removeAncillaryChunks(image);

        Assertions.assertEquals(5, chunks.size());
        Assertions.assertEquals(0, chunks.stream().filter(c -> !CRITICAL_CHUNKS.contains(c.type())).count());
    }

    @Test
    @DisplayName("Only ancillary chunks can be removed")
    void cannotRemoveCriticalChunk() {
        var image = new Image();
        given(parser.readRawChunks(image)).willReturn(MOCK_CHUNKS);

        Assertions.assertThrows(InvalidChunkDeletionException.class,
                () -> manipulator.removeGivenChunks(image, List.of("PLTE", "IEND")));
    }

    @Test
    @DisplayName("Selected chunks are removed")
    void removeSelectedChunk() {
        var image = new Image();
        var chunksToDelete = List.of("gAMA", "cHRM");
        given(parser.readRawChunks(image)).willReturn(MOCK_CHUNKS);

        var chunks = manipulator.removeGivenChunks(image, chunksToDelete);

        Assertions.assertEquals(5, chunks.size());
        Assertions.assertEquals(0, chunks.stream().filter(c -> chunksToDelete.contains(c.type())).count());
    }
}
