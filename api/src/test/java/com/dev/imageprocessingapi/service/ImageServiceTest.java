package com.dev.imageprocessingapi.service;

import com.dev.imageprocessingapi.exception.ImageNotFoundException;
import com.dev.imageprocessingapi.exception.ImageUploadException;
import com.dev.imageprocessingapi.exception.SpectrumNotGeneratedException;
import com.dev.imageprocessingapi.metadataextractor.logic.ChunkValidator;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageManipulator;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageMetadataParser;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageSerializer;
import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.dto.ChunksToDeleteDTO;
import com.dev.imageprocessingapi.repository.ImageRepository;
import lombok.SneakyThrows;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;


@SpringJUnitConfig(classes = {ImageService.class})
public class ImageServiceTest {
    private static final String mockId = "644d739d981447205ec6f809";
    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private ImageMetadataParser parser;
    @MockBean
    private ImageSerializer serializer;
    @MockBean
    private ImageManipulator manipulator;
    @MockBean
    private ChunkValidator validator;
    @Autowired
    private ImageService imageService;

    @Test
    @SneakyThrows
    @DisplayName("Correct file upload")
    void correctImageUpload() {
        var file = new MockMultipartFile("name", "name.png", "image/png", new byte[]{1, 2, 3, 4});
        var imageToInsert = new Image();
        imageToInsert.setFileName(file.getOriginalFilename());
        imageToInsert.setFileType(file.getContentType());
        imageToInsert.setCreatedAt(any());
        imageToInsert.setBytes(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        var insertedImage = new Image();
        insertedImage.setId(mockId);

        when(imageRepository.insert(imageToInsert)).thenReturn(insertedImage);

        var savedImageId = imageService.addImage(file);

        Assertions.assertEquals(mockId, savedImageId);
    }

    @Test
    @SneakyThrows
    @DisplayName("Image upload failure - empty file")
    void imageUploadFailure1() {
        var file = new MockMultipartFile("name", "name.png", "image/png", new byte[]{});

        var e = Assertions.assertThrows(ImageUploadException.class, () -> imageService.addImage(file));
        Assertions.assertEquals("File is empty", e.getMessage());
    }

    @Test
    @SneakyThrows
    @DisplayName("Image upload failure - invalid file format")
    void imageUploadFailure2() {
        var file = new MockMultipartFile("name", "name.jpg", "image/jpg", new byte[]{1, 2, 3});

        var e = Assertions.assertThrows(ImageUploadException.class, () -> imageService.addImage(file));
        Assertions.assertEquals("File extension is not PNG", e.getMessage());
    }

    @Test
    @DisplayName("Call image parser to get metadata")
    void retrieveMetadata() {
        var image = new Image();
        given(imageRepository.findById(mockId)).willReturn(Optional.of(image));

        imageService.getImageMetadata(mockId);

        then(parser).should().processImage(image);
    }

    @Test
    @DisplayName("No image found with given ID")
    void noImageFound() {
        when(imageRepository.findById(mockId)).thenThrow(ImageNotFoundException.class);

        try {
            imageService.getImage(mockId);
        } catch (RuntimeException e) {
            Assertions.assertTrue(e instanceof ImageNotFoundException);
        }
    }

    @Test
    @DisplayName("Magnitude not generated")
    void noImageMagnitudeFound() {
        var imageWithoutMagnitude = new Image();
        imageWithoutMagnitude.setMagnitude(null);
        when(imageRepository.findById(mockId)).thenReturn(Optional.of(imageWithoutMagnitude));

        try {
            imageService.getImageMagnitude(mockId);
        } catch (RuntimeException e) {
            Assertions.assertTrue(e instanceof SpectrumNotGeneratedException);
        }
    }

    @Test
    @DisplayName("Ancillary chunks removing")
    void removeAncillaryChunks() {
        var image = new Image();
        given(imageRepository.findById(mockId)).willReturn(Optional.of(image));

        imageService.removeChunks(mockId, null);

        then(manipulator).should().removeAncillaryChunks(image);
        then(manipulator).should(never()).removeGivenChunks(any(), any());

    }

    @Test
    @DisplayName("Selected chunks removing")
    void removeSelectedChunks() {
        var image = new Image();
        var chunksToDelete = new ChunksToDeleteDTO(List.of("bKGD", "gAMA"));
        given(imageRepository.findById(mockId)).willReturn(Optional.of(image));

        imageService.removeChunks(mockId, chunksToDelete);

        then(manipulator).should().removeGivenChunks(image, chunksToDelete.chunks());
        then(manipulator).should(never()).removeAncillaryChunks(any());
    }

    @Test
    @DisplayName("Call serializer after removing ancillary chunks")
    void serializeChunks() {
        var image = new Image();
        var chunksToDelete = new ChunksToDeleteDTO(List.of("bKGD", "gAMA"));
        given(imageRepository.findById(mockId)).willReturn(Optional.of(image));
        when(manipulator.removeAncillaryChunks(image)).thenReturn(Collections.emptyList());

        imageService.removeChunks(mockId, chunksToDelete);

        then(serializer).should().saveAsPNG(Collections.emptyList());
        then(imageRepository).should().save(image);
    }
}
