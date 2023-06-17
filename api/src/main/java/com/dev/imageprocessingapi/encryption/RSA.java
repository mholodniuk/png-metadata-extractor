package com.dev.imageprocessingapi.encryption;

import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import com.dev.imageprocessingapi.metadataextractor.logic.ImageManipulator;
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

@Component
@AllArgsConstructor
public class RSA {
    private final ImageManipulator imageManipulator;
    private final ImageSerializer imageSerializer;
    // kryterium porownawncze -> ograniczenia na dlugosc bloku
    // rozmowa - decyzje projektowe:
    // w jaki sposob zrealizowano szyfrowanie
    // ktore czesci pliku sa szyforwane
    // jak sobie radzimy z paddingiem
    // jak radzimy sobie z tym ze dane po zaszyfrowaniu maja wiekszy rozmiar

    public Image encryptECB(Image image, CustomPublicKey publicKey) throws IOException, BadPaddingException {
        var inputStream = new ByteArrayInputStream(image.getBytes().getData());
        var imageFromBytes = ImageIO.read(inputStream);
        byte[] pixels = ((DataBufferByte) imageFromBytes.getRaster().getDataBuffer()).getData();

        int blockSize = (publicKey.n().bitLength() / 8) - 1;
        var dividedPixels = divideByteArray(pixels, blockSize);

        var encryptedPixels = new ArrayList<byte[]>();
        for (byte[] buff : dividedPixels) {
            var encryptedChunk = encrypt(buff, publicKey);
            encryptedPixels.add(encryptedChunk);
        }
        var joinedEncrypted = concatLists(encryptedPixels);
        var split = spitArray(joinedEncrypted, pixels.length);

        var bytes = writeImage(imageFromBytes, split.getFirst());
        image.setBytes(bytes);

        var extraBytes = split.getSecond();
        var newChunk =  new RawChunk("xEnc", extraBytes.length, null, extraBytes, calculateCRC(extraBytes, "xENC"));
        var modifiedChunks = imageManipulator.addCustomChunk(image, newChunk);
        Binary bajts = imageSerializer.saveAsPNG(modifiedChunks);

        image.setBytes(bajts);

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

    private static Pair<byte[], byte[]> spitArray(byte[] byteArray, int sizeOfFirst) {
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