package com.dev.imageprocessingapi.service;

import com.dev.imageprocessingapi.consts.PNGChunksDefinitions;
import com.dev.imageprocessingapi.model.Image;
import com.dev.imageprocessingapi.model.chunks.Chunk;
import com.dev.imageprocessingapi.repository.ImageRepository;
import com.dev.imageprocessingapi.utils.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public String addImage(String title, MultipartFile file) throws IOException {
        Image image = new Image();
        image.setFileName(title);

        image.setFileType(file.getContentType());
        image.setBytes(
                new Binary(BsonBinarySubType.BINARY, file.getBytes())
        );

        image = imageRepository.insert(image);
        return image.getId();
    }
//        todo: generate pdf
//        todo: builder/factory method of chunk
//        todo: add bytebuffer
//        https://pyokagan.name/blog/2019-10-14-png/
    public void getImageMetadata() {
        Image image = getImage("63dfe630fa47952b6232a765").orElseGet(Image::new);
        byte[] bytes = image.getBytes().getData();

        byte[] headerToCheck = new byte[8];
        System.arraycopy(bytes, 0, headerToCheck, 0, 8);
        readPNGHeader(headerToCheck);

        List<Chunk> chunks = new ArrayList<>();

        int iterator = 8;

        while (true) {
            byte[] lengthBytes = new byte[]{bytes[iterator], bytes[iterator + 1], bytes[iterator + 2], bytes[iterator + 3]};
            int length = (int) Long.parseLong(ConversionUtils.encodeHexString(lengthBytes), 16);
            iterator += Chunk.LENGTH_FIELD_LEN;

            byte[] chunkBytes = new byte[]{bytes[iterator], bytes[iterator + 1], bytes[iterator + 2], bytes[iterator + 3]};
            String chunkTypeHex = ConversionUtils.encodeHexString(chunkBytes);
            String chunkType = ConversionUtils.convertHexToString(chunkTypeHex);
            iterator += Chunk.TYPE_FIELD_LEN;

            byte[] data = new byte[length];
            List<String> rawBytes = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                data[i] = bytes[i + iterator];
                rawBytes.add(ConversionUtils.byteToHex(data[i]));
            }
            iterator += length;

            byte[] crcBytes = new byte[]{bytes[iterator], bytes[iterator + 1], bytes[iterator + 2], bytes[iterator + 3]};
            String CRC = ConversionUtils.encodeHexString(crcBytes);
            iterator += Chunk.CRC_FIELD_LEN;

            chunks.add(new Chunk(Chunk.assignType(chunkType), length, rawBytes, CRC, data));

            if (chunkTypeHex.equals(PNGChunksDefinitions.IEND_HEX)) {
                break;
            }
        }
        chunks.forEach(chunk -> System.out.println(chunk.toString()));
    }

    void readPNGHeader(byte[] bytes) {
        if (bytes.length < 8)
            throw new RuntimeException("ASS");

        var pngHeaderString = ConversionUtils.encodeHexString(bytes);

        if (pngHeaderString.equals(PNGChunksDefinitions.PNG_HEADER_HEX))
            System.out.println("JEST PNG");
    }

    public Optional<Image> getImage(String id) {
        return imageRepository.findById(id);
    }
}