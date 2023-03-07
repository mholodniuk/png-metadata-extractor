package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import com.dev.imageprocessingapi.model.PNGMetadata;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageSerializer {
    public Binary saveChunksAsPNG(PNGMetadata pngMetadata) {
        String PNGHeader = "89504e470d0a1a0a";
        ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();
        try {
            resultOutputStream.write(ConversionUtils.decodeHexString(PNGHeader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pngMetadata.getChunks().forEach(chunk -> {
            byte[] one = ConversionUtils.encodeInteger(chunk.getLength());
            String typeInHex = ConversionUtils.convertStringToHex(chunk.getType());
            byte[] two = ConversionUtils.decodeHexString(typeInHex);
            String bytes = String.join("", chunk.getRawBytes());
            byte[] three = ConversionUtils.decodeHexString(bytes);
            byte[] four = ConversionUtils.decodeHexString(chunk.getCRC());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(one);
                outputStream.write(two);
                outputStream.write(three);
                outputStream.write(four);

                resultOutputStream.write(outputStream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return new Binary(BsonBinarySubType.BINARY, resultOutputStream.toByteArray());
    }
}
