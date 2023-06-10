package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.event.annotation.TrackExecutionTime;
import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.PNGMetadata;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ImageMetadataParser {
    private final ChunkInterpreter interpreter;
    private byte[] bytes;

    @TrackExecutionTime
    public PNGMetadata processImage(Image image) {
        List<RawChunk> rawChunks = readRawChunks(image);
        return interpreter.appendInterpretedInformation(image.getId(), rawChunks);
    }

    public List<RawChunk> readRawChunks(Image image) {
        bytes = image.getBytes().getData();

        byte[] headerToCheck = new byte[8];
        System.arraycopy(bytes, 0, headerToCheck, 0, 8);
        validateHeader(headerToCheck);

        List<RawChunk> chunks = new ArrayList<>();
        int iterator = 8;

        while (true) {
            int currentIterator = iterator;
            int length = readChunkLength(iterator);
            iterator += RawChunk.LENGTH_FIELD_LENGTH;

            String chunkType = readChunkType(iterator);
            iterator += RawChunk.TYPE_FIELD_LENGTH;

            byte[] rawBytes = readRawBytes(iterator, length);
            iterator += length;

            String CRC = readCRC(iterator);
            iterator += RawChunk.CRC_FIELD_LENGTH;

            chunks.add(new RawChunk(chunkType, length, currentIterator, rawBytes, CRC.toUpperCase()));

            if (chunkType.equals("IEND")) {
                break;
            }
        }
        return chunks;
    }

    private String readCRC(int iterator) {
        byte[] crcBytes = new byte[]{bytes[iterator], bytes[iterator + 1], bytes[iterator + 2], bytes[iterator + 3]};
        return ConversionUtils.formatHex(crcBytes);
    }

    private byte[] readRawBytes(int iterator, int length) {
        byte[] data = new byte[length];
        System.arraycopy(bytes, iterator, data, 0, length);
        return data;
    }

    private String readChunkType(int iterator) {
        byte[] chunkBytes = new byte[]{bytes[iterator], bytes[iterator + 1], bytes[iterator + 2], bytes[iterator + 3]};
        return ConversionUtils.convertHexStringToSimpleString(ConversionUtils.formatHex(chunkBytes));
    }

    private int readChunkLength(int iterator) {
        byte[] lengthBytes = new byte[]{bytes[iterator], bytes[iterator + 1], bytes[iterator + 2], bytes[iterator + 3]};
        return (int) Long.parseLong(ConversionUtils.formatHex(lengthBytes), 16);
    }

    private void validateHeader(byte[] bytes) {
        String PNGHeader = "89504e470d0a1a0a";
        var pngHeaderString = ConversionUtils.formatHex(bytes);

        if (bytes.length < 8 || !pngHeaderString.equals(PNGHeader))
            throw new RuntimeException("invalid png header length");
    }
}
