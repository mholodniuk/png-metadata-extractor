package com.dev.imageprocessingapi.metadataextractor;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.ByteArrayOutputStream;

import static com.dev.imageprocessingapi.TestConst.MOCK_CHUNKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {ImageSerializer.class})
class ImageSerializerTest {
    @Autowired
    private ImageSerializer serializer;

    @Test
    @SneakyThrows
    void saveChunksAsImage() {
        ByteArrayOutputStream mockOutputStream = mock(ByteArrayOutputStream.class);
        when(mockOutputStream.toByteArray()).thenReturn(new byte[]{1, 2, 3, 4, 5});

        var result = serializer.saveAsPNG(MOCK_CHUNKS);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getData().length > 0);
    }
}
