package com.dev.imageprocessingapi.metadataextractor.logic;

import com.dev.imageprocessingapi.metadataextractor.analysers.Analyser;
import com.dev.imageprocessingapi.metadataextractor.analysers.impl.*;
import com.dev.imageprocessingapi.metadataextractor.model.Chunk;
import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.PNGMetadata;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class ImageMetadataParser {
    private static final int RAW_BYTES_LENGTH_LIMIT = 200;
    private Map<String, Object> IHDRInfo;
    private byte[] bytes;

    public PNGMetadata processImage(Image image) {
        List<RawChunk> rawChunks = readRawChunks(image);
        return appendInterpretedInformation(image.getId(), rawChunks);
    }

    public List<RawChunk> readRawChunks(Image image) {
        bytes = image.getBytes().getData();

        byte[] headerToCheck = new byte[8];
        System.arraycopy(bytes, 0, headerToCheck, 0, 8);
        validateHeader(headerToCheck);

        List<RawChunk> chunks = new ArrayList<>();
        int iterator = 8;

        while (true) {
            int length = readChunkLength(iterator);
            iterator += RawChunk.LENGTH_FIELD_LENGTH;

            String chunkType = readChunkType(iterator);
            iterator += RawChunk.TYPE_FIELD_LENGTH;

            List<String> rawBytes = readRawBytes(iterator, length);
            iterator += length;

            String CRC = readCRC(iterator);
            iterator += RawChunk.CRC_FIELD_LENGTH;

            chunks.add(new RawChunk(chunkType, length, rawBytes, CRC));

            if (chunkType.equals("IEND")) {
                break;
            }
        }
        return chunks;
    }

    private PNGMetadata appendInterpretedInformation(String id, List<RawChunk> chunks) {
        List<Chunk> processedChunks = new ArrayList<>();
        RawChunk IHDR = chunks.get(0);
        IHDRInfo = new IHDRAnalyser().analyse(IHDR.rawBytes());

        for (RawChunk chunk : chunks) {
            Chunk analysedChunk = processRawChunk(chunk, matchAnalyserToChunkType(chunk.type()));
            processedChunks.add(analysedChunk);
        }
        return new PNGMetadata(id, true, processedChunks);
    }

    private Analyser matchAnalyserToChunkType(String type) {
        return switch (type) {
            case "IHDR" -> new IHDRAnalyser();
            case "gAMA" -> new gAMAAnalyser();
            case "PLTE" -> new PLTEAnalyser((int) IHDRInfo.get("Color type"), (int) IHDRInfo.get("Bit depth"));
            case "tIME" -> new tIMEAnalyser();
            case "tEXt" -> new tEXtAnalyser();
            case "zTXt" -> new zTXtAnalyser();
            default -> null;
        };
    }

    private Chunk processRawChunk(RawChunk rawChunk, Analyser analyser) {
        Map<String, Object> properties = analyser != null ? analyser.analyse(rawChunk.rawBytes()) : null;
        List<String> rawBytes = rawChunk.length() > RAW_BYTES_LENGTH_LIMIT
                ? getFirstAndLastStrings(rawChunk.rawBytes(), RAW_BYTES_LENGTH_LIMIT / 2)
                : rawChunk.rawBytes();
        return new Chunk(rawChunk.type(), rawChunk.length(), rawBytes, properties, rawChunk.CRC());
    }

    public static List<String> getFirstAndLastStrings(List<String> strings, int numElements) {
        return Stream.concat(
                Stream.concat(strings.stream().limit(numElements), Stream.of("...")),
                strings.stream().skip(strings.size() - numElements)
        ).collect(Collectors.toList());
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

    private void validateHeader(byte[] bytes) {
        String PNGHeader = "89504e470d0a1a0a";
        var pngHeaderString = ConversionUtils.formatHex(bytes);

        if (bytes.length < 8 || !pngHeaderString.equals(PNGHeader))
            throw new RuntimeException("invalid png header length");
    }
}
