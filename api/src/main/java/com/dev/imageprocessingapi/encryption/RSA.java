package com.dev.imageprocessingapi.encryption;

import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import com.dev.imageprocessingapi.metadataextractor.logic.ChunkInterpreter;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.imageio.ImageIO;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class RSA {
    private final ChunkInterpreter interpreter;

    // kryterium porownawncze -> ograniczenia na dlugosc bloku
    // rozmowa - decyzje projektowe:
    // w jaki sposob zrealizowano szyfrowanie
    // ktore czesci pliku sa szyforwane
    // jak sobie radzimy z paddingiem
    // jak radzimy sobie z tym ze dane po zaszyfrowaniu maja wiekszy rozmiar

    public void encryptECB(byte[] bytes, CustomPublicKey publicKey) throws IOException {
        var inputStream = new ByteArrayInputStream(bytes);
        var image = ImageIO.read(inputStream);
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        System.out.println(formatByteArray(pixels));
        System.out.println(pixels.length);

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

    private List<RawChunk> splitBetweenIDATs(byte[] bytes, List<Integer> sizes) {
        var result = new ArrayList<RawChunk>();

        return result;
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

    private static long countIDATs(List<RawChunk> chunks) {
        return chunks.stream().filter(chunk -> chunk.type().equals("IDAT")).count();
    }

    private static List<Integer> getIDATSizes(List<RawChunk> chunks) {
        return chunks.stream().filter(chunk -> chunk.type().equals("IDAT")).map(RawChunk::length).toList();
    }

    private static List<byte[]> joinIDATs(List<RawChunk> chunks) {
        return chunks.stream().filter(chunk -> chunk.type().equals("IDAT")).map(RawChunk::rawBytes).toList();
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

//        // pad last chunk if not desired length
//        int lastChunkIndex = dividedList.size() - 1;
//        byte[] lastChunk = dividedList.get(lastChunkIndex);
//
//        // Check if the last chunk size is less than chunkSize
//        if (lastChunk.length < chunkSize) {
//            byte[] paddedChunk = new byte[chunkSize];
//            System.arraycopy(lastChunk, 0, paddedChunk, 0, lastChunk.length);
//            dividedList.set(lastChunkIndex, paddedChunk);
//        }

        return dividedList;
    }

    private static byte[] concatLists(List<byte[]> arrays) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (arrays != null) {
            arrays.stream().filter(Objects::nonNull).forEach(array -> out.write(array, 0, array.length));
        }
        return out.toByteArray();
    }
}