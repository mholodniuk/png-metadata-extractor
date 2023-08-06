package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.metadataextractor.exception.ChunksSavingException;
import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
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

    public Binary saveAsPNG(List<RawChunk> chunks) {
        var resultOutputStream = new ByteArrayOutputStream();
        try {
            resultOutputStream.write(ConversionUtils.parseHexString(PNGHeader));
            for (RawChunk chunk: chunks) {
                resultOutputStream.write(saveSingleChunk(chunk));
            }
        } catch (IOException e) {
            throw new ChunksSavingException("Error while serializing metadata");
        }

        return new Binary(BsonBinarySubType.BINARY, resultOutputStream.toByteArray());
    }

    private byte[] saveSingleChunk(RawChunk chunk) throws IOException {
        byte[] length = ConversionUtils.encodeInteger(chunk.length());
        String typeInHex = ConversionUtils.convertSimpleStringToHexString(chunk.type());
        byte[] type = ConversionUtils.parseHexString(typeInHex);
        byte[] bytes = chunk.rawBytes();
        byte[] CRC = ConversionUtils.parseHexString(chunk.CRC());

        var outputStream = new ByteArrayOutputStream();
        outputStream.write(length);
        outputStream.write(type);
        outputStream.write(bytes);
        outputStream.write(CRC);

        return outputStream.toByteArray();
    }
}
