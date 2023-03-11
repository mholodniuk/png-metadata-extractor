package com.dev.imageprocessingapi.metadataextractor;

import com.dev.imageprocessingapi.metadataextractor.chunks.Chunk;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.PNGMetadata;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ImageMetadataParser {

    private byte[] bytes;

    //    https://thiscouldbebetter.wordpress.com/2011/08/26/compressing-and-uncompressing-data-in-java-using-zlib/
//    todo: use strategy pattern for chunk data extraction -> not yet
    public PNGMetadata getImageMetadata(Image image) {
        bytes = image.getBytes().getData();

        byte[] headerToCheck = new byte[8];
        System.arraycopy(bytes, 0, headerToCheck, 0, 8);
        boolean isValidPNG = readPNGHeader(headerToCheck);

        List<Chunk> chunks = new ArrayList<>();
        int iterator = 8;

        // todo: use builder pattern to build a chunk -> can be done now ?
        while (true) {
            int length = readChunkLength(iterator);
            iterator += Chunk.LENGTH_FIELD_LEN;

            String chunkType = readChunkType(iterator);
            iterator += Chunk.TYPE_FIELD_LEN;

            List<String> rawBytes = readRawBytes(iterator, length);
            iterator += length;

            String CRC = readCRC(iterator);
            iterator += Chunk.CRC_FIELD_LEN;

            chunks.add(ChunkFactory.create(chunkType, length, rawBytes, CRC));

            if (chunkType.equals("IEND")) {
                break;
            }
        }
        return new PNGMetadata(image.getId(), isValidPNG, chunks);
    }

    private String readCRC(int iterator) {
        byte[] crcBytes = new byte[]{bytes[iterator], bytes[iterator + 1], bytes[iterator + 2], bytes[iterator + 3]};
        return ConversionUtils.formatHex(crcBytes);
    }

    private List<String> readRawBytes(int iterator, int length) {
        byte[] data = new byte[length];
        List<String> rawBytes = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            data[i] = bytes[i + iterator];
            rawBytes.add(ConversionUtils.toHexDigits(data[i]));
        }
        return rawBytes;
    }

    private String readChunkType(int iterator) {
        byte[] chunkBytes = new byte[]{bytes[iterator], bytes[iterator + 1], bytes[iterator + 2], bytes[iterator + 3]};
        return ConversionUtils.convertHexStringToSimpleString(ConversionUtils.formatHex(chunkBytes));
    }

    private int readChunkLength(int iterator) {
        byte[] lengthBytes = new byte[]{bytes[iterator], bytes[iterator + 1], bytes[iterator + 2], bytes[iterator + 3]};
        return (int) Long.parseLong(ConversionUtils.formatHex(lengthBytes), 16);
    }

    private boolean readPNGHeader(byte[] bytes) {
        String PNGHeader = "89504e470d0a1a0a";
        if (bytes.length < 8)
            throw new RuntimeException("invalid png header length");
        var pngHeaderString = ConversionUtils.formatHex(bytes);

        return pngHeaderString.equals(PNGHeader);
    }
}
