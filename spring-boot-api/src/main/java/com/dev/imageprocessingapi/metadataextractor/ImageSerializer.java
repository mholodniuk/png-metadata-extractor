package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.exception.ChunksSavingException;
import com.dev.imageprocessingapi.metadataextractor.chunks.Chunk;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import com.dev.imageprocessingapi.model.PNGMetadata;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageSerializer {
    private static final String PNGHeader = "89504e470d0a1a0a";
    public Binary saveChunksAsPNG(PNGMetadata pngMetadata) {
        ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();
        try {
            resultOutputStream.write(ConversionUtils.parseHexString(PNGHeader));
            for (Chunk chunk: pngMetadata.getChunks()) {
                resultOutputStream.write(saveSingleChunk(chunk));
            }
        } catch (IOException e) {
            throw new ChunksSavingException("Error while serializing metadata");
        }

        return new Binary(BsonBinarySubType.BINARY, resultOutputStream.toByteArray());
    }

    private byte[] saveSingleChunk(Chunk chunk) throws IOException {
        byte[] one = ConversionUtils.encodeInteger(chunk.getLength());
        String typeInHex = ConversionUtils.convertSimpleStringToHexString(chunk.getType());
        byte[] two = ConversionUtils.parseHexString(typeInHex);
        String bytes = String.join("", chunk.getRawBytes());
        byte[] three = ConversionUtils.parseHexString(bytes);
        byte[] four = ConversionUtils.parseHexString(chunk.getCRC());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(one);
        outputStream.write(two);
        outputStream.write(three);
        outputStream.write(four);

        return outputStream.toByteArray();
    }
}
