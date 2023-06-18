package com.dev.imageprocessingapi.encryption;

import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageManipulator;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageMetadataParser;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageSerializer;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import com.dev.imageprocessingapi.model.Image;
import lombok.AllArgsConstructor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils.calculateCRC;
import static com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils.formatHex;

@Component
@AllArgsConstructor
public class RSA {
    private final ImageManipulator imageManipulator;
    private final ImageSerializer imageSerializer;
    private final ImageMetadataParser imageMetadataParser;

    public Image encryptECB(Image image, CustomPublicKey publicKey) throws IOException, BadPaddingException {
        var inputStream = new ByteArrayInputStream(image.getBytes().getData());
        var imageFromBytes = ImageIO.read(inputStream);
        byte[] pixels = ((DataBufferByte) imageFromBytes.getRaster().getDataBuffer()).getData();
        System.out.println("original image size: " + pixels.length);

        int blockSize = (publicKey.n().bitLength() / 8) - 1;
        var dividedPixels = divideByteArray(pixels, blockSize);

        var encryptedPixels = new ArrayList<byte[]>();
        for (byte[] buff : dividedPixels) {
            var encryptedChunk = encrypt(buff, publicKey);
            encryptedPixels.add(encryptedChunk);
        }
        var joinedEncrypted = concatLists(encryptedPixels);
        var split = splitArray(joinedEncrypted, pixels.length);

        var bytes = writeImage(imageFromBytes, split.getFirst());
        image.setBytes(bytes);

        var imageWithAppendedChunk = appendExtraChunk(image, split.getSecond(), dividedPixels.get(dividedPixels.size() - 1).length);
        image.setBytes(imageWithAppendedChunk);

        return image;
    }

    public Image decryptECB(Image image, CustomPrivateKey privateKey) throws IOException, BadPaddingException {
        var inputStream = new ByteArrayInputStream(image.getBytes().getData());
        var imageFromBytes = ImageIO.read(inputStream);
        byte[] pixels = ((DataBufferByte) imageFromBytes.getRaster().getDataBuffer()).getData();

        int blockSize = (privateKey.n().bitLength() / 8) - 1;
        int lastSize = getExtraSizeInfo(image);
        var dataInfo = getExtraDataInfo(image);
        pixels = concatLists(List.of(pixels, dataInfo));

        var dividedPixels = divideByteArray(pixels, blockSize + 1);

        var decryptedPixels = new ArrayList<byte[]>();
        for (int i = 0; i < dividedPixels.size(); ++i) {
            boolean isLast = i == dividedPixels.size() - 1;
            var decryptedChunk = decrypt(dividedPixels.get(i), privateKey, isLast ? lastSize : blockSize);
            decryptedPixels.add(decryptedChunk);
        }
        var joinedDecrypted = concatLists(decryptedPixels);
        System.out.println("restored image size: " + joinedDecrypted.length);

        var bytes = writeImage(imageFromBytes, joinedDecrypted);
        image.setBytes(bytes);

        return image;
    }

    public Image encryptCompressedECB(Image image, CustomPublicKey publicKey) throws BadPaddingException {
        var IDATs = imageMetadataParser.readRawChunks(image).stream().filter(chunk -> chunk.type().equals("IDAT"))
                .map(RawChunk::rawBytes).toList();

        if (IDATs.size() > 1) {
            throw new IllegalArgumentException("Encrypting more than one IDAT not supported");
        }

        var bytes = IDATs.get(0);

        int blockSize = (publicKey.n().bitLength() / 8) - 1;
        var dividedPixels = divideByteArray(bytes, blockSize);

        var encryptedPixels = new ArrayList<byte[]>();
        for (byte[] buff : dividedPixels) {
            System.out.println(buff.length);
            System.out.println(formatByteArray(buff));
            var encryptedChunk = encrypt(buff, publicKey);
            encryptedPixels.add(encryptedChunk);
        }
        var joinedEncrypted = concatLists(encryptedPixels);
        System.out.println("joinedEncrypted " + formatByteArray(joinedEncrypted));
        var split = splitArray(joinedEncrypted, bytes.length);

        var sizeArr = ConversionUtils.encodeInteger(split.getSecond().length);
        var dataChunk = new RawChunk("xEnd", split.getSecond().length, null, split.getSecond(), calculateCRC(split.getSecond(), "xEnd"));
        var sizeChunk = new RawChunk("xEns", sizeArr.length, null, sizeArr, calculateCRC(sizeArr, "xEns"));
        var modifiedChunks = imageManipulator.addCustomChunks(image, dataChunk, sizeChunk);

        var result = new ArrayList<RawChunk>();
        for (var chunk : modifiedChunks) {
            if (chunk.type().equals("IDAT")) {
                result.add(new RawChunk("IDAT", split.getFirst().length, null, split.getFirst(), calculateCRC(split.getFirst(), "IDAT")));
            } else {
                result.add(chunk);
            }
        }

        var modifiedBytes = imageSerializer.saveAsPNG(result);
        image.setBytes(modifiedBytes);

        return image;
    }

    public Image decryptCompressedECB(Image image, CustomPrivateKey privateKey) throws BadPaddingException {
        var IDATs = imageMetadataParser.readRawChunks(image).stream().filter(chunk -> chunk.type().equals("IDAT"))
                .map(RawChunk::rawBytes).toList();

        if (IDATs.size() > 1) {
            throw new IllegalArgumentException("Decrypting more than one IDAT not supported");
        }

        var bytes = IDATs.get(0);
        int ogSize = bytes.length;
        System.out.println(ogSize);

        int blockSize = (privateKey.n().bitLength() / 8) - 1;
        var dataInfo = getExtraDataInfo(image);
        bytes = concatLists(List.of(bytes, dataInfo));
        System.out.println("bytes " + formatByteArray(bytes));

        var dividedPixels = divideByteArray(bytes, blockSize + 1);
        System.out.println(dividedPixels.size());

        var decryptedPixels = new ArrayList<byte[]>();
        for (int i = 0; i < dividedPixels.size(); ++i) {
            boolean isLast = i == dividedPixels.size() - 1;
            var decryptedChunk = decrypt(dividedPixels.get(i), privateKey, isLast ? (ogSize - blockSize) : blockSize);
            System.out.println(decryptedChunk.length);
            System.out.println(formatByteArray(decryptedChunk));
            decryptedPixels.add(decryptedChunk);
        }
        var joinedDecrypted = concatLists(decryptedPixels);

        var imageChunks = imageMetadataParser.readRawChunks(image);
        var result = new ArrayList<RawChunk>();
        for (var chunk : imageChunks) {
            if (chunk.type().equals("IDAT")) {
                result.add(new RawChunk("IDAT", joinedDecrypted.length, null, joinedDecrypted, calculateCRC(joinedDecrypted, "IDAT")));
            } else {
                result.add(chunk);
            }
        }

        var modifiedBytes = imageSerializer.saveAsPNG(result);
        image.setBytes(modifiedBytes);

        return image;
    }

    private Binary writeImage(BufferedImage bufferedImage, byte[] pixels) throws IOException {
        var imageToSave = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        imageToSave.setData(Raster.createRaster(bufferedImage.getSampleModel(), new DataBufferByte(pixels, pixels.length), new Point()));
        var byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(imageToSave, "png", byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return new Binary(BsonBinarySubType.BINARY, bytes);
    }

    private Binary appendExtraChunk(Image image, byte[] bytes, int size) {
        var sizeArr = ConversionUtils.encodeInteger(size);
        var dataChunk = new RawChunk("xEnd", bytes.length, null, bytes, calculateCRC(bytes, "xEnd"));
        var sizeChunk = new RawChunk("xEns", sizeArr.length, null, sizeArr, calculateCRC(sizeArr, "xEns"));
        var modifiedChunks = imageManipulator.addCustomChunks(image, dataChunk, sizeChunk);
        return imageSerializer.saveAsPNG(modifiedChunks);
    }

    private int getExtraSizeInfo(Image image) {
        var sizeInfoChunk = imageMetadataParser.readRawChunks(image).stream()
                .filter(chunk -> chunk.type().equals("xEns")).toList().get(0);
        return ConversionUtils.fromHexDigits(ConversionUtils.formatHex(sizeInfoChunk.rawBytes()));
    }

    private byte[] getExtraDataInfo(Image image) {
        return imageMetadataParser.readRawChunks(image).stream().filter(chunk -> chunk.type().equals("xEnd")).toList()
                .get(0).rawBytes();
    }

    public byte[] encrypt(byte[] msg, CustomPublicKey key) throws BadPaddingException {
        BigInteger m = parseMsg(msg, key.n());
        BigInteger c = pow(m, key.e(), key.n());
        return toByteArray(c, getByteLength(key.n()));
    }

    private byte[] decrypt(byte[] msg, CustomPrivateKey key, int length) throws BadPaddingException {
        BigInteger c = parseMsg(msg, key.n());
        BigInteger m = c.modPow(key.d(), key.n());
        var paddedResult = toByteArray(m, getByteLength(key.n()));
        return unPadByteArray(paddedResult, length);
    }


    private byte[] unPadByteArray(byte[] byteArray, int originalSize) {
        int paddedLength = byteArray.length;
        int zeros = paddedLength - originalSize;

        if (zeros <= 0) {
            return new byte[0];
        }

        byte[] unPaddedArray = new byte[originalSize];
        System.arraycopy(byteArray, zeros, unPaddedArray, 0, originalSize);
        return unPaddedArray;
    }


    public static BigInteger pow(BigInteger base, BigInteger exponent, BigInteger modulus) {
        return base.modPow(exponent, modulus);
    }

    private static List<String> formatByteArray(byte[] bytes) {
        List<String> result = new ArrayList<>();
        for (byte b : bytes) {
            result.add(ConversionUtils.toHexDigits(b));
        }
        return result;
    }

    private BigInteger parseMsg(byte[] msg, BigInteger n) throws BadPaddingException {
        BigInteger m = new BigInteger(1, msg);
        if (m.compareTo(n) >= 0) {
            throw new BadPaddingException("Message is larger than modulus");
        }
        return m;
    }

    public static int getByteLength(BigInteger b) {
        int n = b.bitLength();
        return (n + 7) >> 3;
    }

    private static byte[] toByteArray(BigInteger bi, int len) {
        byte[] b = bi.toByteArray();
        int n = b.length;
        if (n == len) {
            return b;
        }
        if (n < len) {
            byte[] t = new byte[len];
            System.arraycopy(b, 0, t, (len - n), n);
            Arrays.fill(t, 0, (len - n), (byte) 0);
            Arrays.fill(b, (byte) 0);
            return t;
        }
        // Original code for handling (n == len + 1) and (b[0] == 0)
        byte[] t = new byte[len];
        System.arraycopy(b, 1, t, 0, len);
        Arrays.fill(b, (byte) 0);
        return t;
    }

    public static List<byte[]> divideByteArray(byte[] inputArray, int chunkSize) {
        List<byte[]> dividedList = new ArrayList<>();

        int inputLength = inputArray.length;
        int startIndex = 0;

        while (startIndex < inputLength) {
            int endIndex = Math.min(startIndex + chunkSize, inputLength);
            byte[] chunk = new byte[endIndex - startIndex];
            System.arraycopy(inputArray, startIndex, chunk, 0, chunk.length);
            dividedList.add(chunk);
            startIndex += chunkSize;
        }

        return dividedList;
    }

    private static Pair<byte[], byte[]> splitArray(byte[] byteArray, int sizeOfFirst) {
        byte[] first = Arrays.copyOfRange(byteArray, 0, sizeOfFirst);
        byte[] second = Arrays.copyOfRange(byteArray, sizeOfFirst, byteArray.length);

        return Pair.of(first, second);
    }

    private static byte[] concatLists(List<byte[]> arrays) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (arrays != null) {
            arrays.stream().filter(Objects::nonNull).forEach(array -> out.write(array, 0, array.length));
        }
        return out.toByteArray();
    }
}