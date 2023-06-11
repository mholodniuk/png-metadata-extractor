package com.dev.imageprocessingapi.encryption;

import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
<<<<<<< HEAD
import com.dev.imageprocessingapi.metadataextractor.logic.ChunkInterpreter;
=======
>>>>>>> origin/rsa
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
<<<<<<< HEAD
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
=======
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
>>>>>>> origin/rsa
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
<<<<<<< HEAD
import java.util.zip.DataFormatException;

import static com.dev.imageprocessingapi.encryption.Utils.generateRSACustomKeyPair;
import static com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils.*;


/*
 * 1. mam ilosc chunkow idat, poszczegolne dlugosci bajtow w nich mam
 * 2. łączę wszystkie możliwe dane do jednej tablicy
 * 3. muszę znać rozmiar tablic na jakie musi tekst być podzielony
 * 4. szyfruje dane w n ilosci tablic
 * 5. lacze te dane do jednej wielkiej tablicy
 * 6. znajac ilosc danych dla kazdego z chunkow idat, wkladam tam tyle ile sie da
 * 7. robie to dla kazdego z idatow, lub do skonczenia miejsca
 * 8. pozostałe dane wkladam do customowego chunka ???? lub za iend
 * */
=======

import static com.dev.imageprocessingapi.encryption.Utils.generateRSACustomKeyPair;
>>>>>>> origin/rsa

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

    public List<RawChunk> encryptECB(List<RawChunk> chunks, int keyLength) throws BadPaddingException, DataFormatException, NoSuchAlgorithmException {
        if (keyLength % 8 != 0) {
            throw new RuntimeException("KEY INVALID!!! SHOULD BE DIVISIBLE BY 8");
        }

        int compressionLevel = (Integer) interpreter.analyseChunk(chunks.get(0), "IHDR").properties().get("Compression method");
        var customKeyPair = generateRSACustomKeyPair(keyLength);

        int blockSize = keyLength / 8 - 1;
        System.out.println("blockSize: " + blockSize);
        var joinedBytes = joinIDATs(chunks);

        var bytes = concatMany(joinedBytes);
        System.out.println("original: " + Arrays.toString(bytes));
        System.out.println("bytes size: " + bytes.length);

        var decompressed = decompressZlib(bytes);
        System.out.println("decompressed " + Arrays.toString(decompressed));
        System.out.println("decompressed size " + decompressed.length);

        var compressed = compressZlib(bytes, compressionLevel);
        System.out.println("compressed " + Arrays.toString(compressed));
        System.out.println("compressed size " + compressed.length);

        var decompressedBack = decompressZlib(decompressZlib(compressed));
        System.out.println("decompressed back " + Arrays.toString(decompressedBack));
        System.out.println("decompressed back size " + decompressedBack.length);

        System.out.println(Arrays.compare(decompressedBack, decompressedBack) == 0);

//        var numOfBlocks = bytes.length / blockSize;
//        System.out.println("numOfBlocks " + numOfBlocks);
//        System.out.println("bytes.length " + bytes.length);
//
//        var rest = bytes.length - (blockSize * numOfBlocks);
//        System.out.println("rest: " + rest);
//
//        var divided = divideByteArray(bytes, blockSize);
//        System.out.println("divided.size() " + divided.size());
//
//        divided.stream()
//                .filter(arr -> arr.length != blockSize)
//                .forEach(arr -> System.out.println(arr.length));
//
//        System.out.println("last byte array: " + Arrays.toString(divided.get(divided.size() - 1)));
//        System.out.println("last byte array size: " + divided.get(divided.size() - 1).length);


//        for (var chunk : chunks) {
//            result.add(chunk);
//            if (chunk.type().equals("IDAT")) {
//                System.out.println("chunk.rawBytes() " + Arrays.toString(chunk.rawBytes()));
//                System.out.println("chunk.rawBytes().length " + chunk.rawBytes().length);
//
//                var encrypted = crypt(chunk.rawBytes(), keys.publicKey());
//                System.out.println("encrypted " + Arrays.toString(encrypted));
//                System.out.println("encrypted.length " + encrypted.length);
//
//                var decrypted = decrypt(encrypted, keys.privateKey(), chunk.length());
//
//                System.out.println("decrypted " + Arrays.toString(decrypted));
//                System.out.println("decrypted.length " + decrypted.length);
//
//                System.out.println(Arrays.equals(chunk.rawBytes(), decrypted));
//            }
//        }
        var mockBytes = new byte[]{1, 2, 3, 4};
        chunks.add(chunks.size() - 1, new RawChunk("xxxx", 4, null, mockBytes, calculateCRC(mockBytes, "xxxx")));
        return chunks;
    }

    public byte[] crypt(byte[] msg, CustomPublicKey key) throws BadPaddingException {
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


    private static List<byte[]> joinIDATs(List<RawChunk> chunks) {
        return chunks.stream()
                .filter(chunk -> chunk.type().equals("IDAT"))
                .map(RawChunk::rawBytes).toList();
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

        // pad last chunk if not desired length
        int lastChunkIndex = dividedList.size() - 1;
        byte[] lastChunk = dividedList.get(lastChunkIndex);

        // Check if the last chunk size is less than chunkSize
        if (lastChunk.length < chunkSize) {
            byte[] paddedChunk = new byte[chunkSize];
            System.arraycopy(lastChunk, 0, paddedChunk, 0, lastChunk.length);
            dividedList.set(lastChunkIndex, paddedChunk);
        }

        return dividedList;
    }

    private static byte[] concatMany(List<byte[]> arrays) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (arrays != null) {
            arrays.stream().filter(Objects::nonNull).forEach(array -> out.write(array, 0, array.length));
        }
        return out.toByteArray();
    }
}