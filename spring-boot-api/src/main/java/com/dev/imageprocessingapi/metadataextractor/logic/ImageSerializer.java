package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.exception.ChunksSavingException;
import com.dev.imageprocessingapi.metadataextractor.model.Chunk;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ImageSerializer {
    private static final String PNGHeader = "89504e470d0a1a0a";
    public Binary saveAsPNG(List<Chunk> chunks) {
        ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();
        try {
            resultOutputStream.write(ConversionUtils.parseHexString(PNGHeader));
            for (Chunk chunk: chunks) {
                resultOutputStream.write(saveSingleChunk(chunk));
            }
        } catch (IOException e) {
            throw new ChunksSavingException("Error while serializing metadata");
        }

        return new Binary(BsonBinarySubType.BINARY, resultOutputStream.toByteArray());
    }

    private byte[] saveSingleChunk(Chunk chunk) throws IOException {
        byte[] one = ConversionUtils.encodeInteger(chunk.length());
        String typeInHex = ConversionUtils.convertSimpleStringToHexString(chunk.type());
        byte[] two = ConversionUtils.parseHexString(typeInHex);
        String bytes = String.join("", chunk.rawBytes());
        byte[] three = ConversionUtils.parseHexString(bytes);
        byte[] four = ConversionUtils.parseHexString(chunk.CRC());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(one);
        outputStream.write(two);
        outputStream.write(three);
        outputStream.write(four);

        return outputStream.toByteArray();
    }
}
