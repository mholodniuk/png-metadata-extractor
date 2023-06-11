package com.dev.imageprocessingapi.encryption;

import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import com.dev.imageprocessingapi.metadataextractor.logic.ChunkInterpreter;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import static com.dev.imageprocessingapi.encryption.Utils.generateRSACustomKeyPair;
import static com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils.*;

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

    public List<RawChunk> encryptECB(List<RawChunk> chunks, CustomPublicKey publicKey, CustomPrivateKey privateKey) throws BadPaddingException, DataFormatException, NoSuchAlgorithmException {
        int keyLength = publicKey.n().bitLength();

        int compressionLevel = (Integer) interpreter.analyseChunk(chunks.get(0), "IHDR").properties()
                .get("Compression method") == 0 ? Deflater.DEFLATED : Deflater.BEST_COMPRESSION;

        int blockSize = (keyLength / 8) - 1;
        var joinedBytes = joinIDATs(chunks);

        var compressedBytes = concatMany(joinedBytes);
        System.out.println("compressedBytes " + map(compressedBytes));
        System.out.println("compressedBytes.length " + compressedBytes.length);

        var decompressedBytes = decompressZlib(compressedBytes);
        System.out.println("decompressedBytes " + map(decompressedBytes));
        System.out.println("decompressedBytes.length " + decompressedBytes.length);

        var dividedDecompressed = divideByteArray(decompressedBytes, blockSize);

        var encrypted = new ArrayList<byte[]>();
        for (var bytes : dividedDecompressed) {
            var rsa = encrypt(bytes, publicKey);
            encrypted.add(rsa);
        }
        var encryptedJoined = concatMany(encrypted);

        System.out.println("encryptedJoined " + map(encryptedJoined));
        System.out.println("encryptedJoined.length " + encryptedJoined.length);

        var encryptedJoinedCompressed = compressZlib(encryptedJoined, compressionLevel);

        System.out.println("encryptedJoinedCompressed " + map(encryptedJoinedCompressed));
        System.out.println("encryptedJoinedCompressed.length " + encryptedJoinedCompressed.length);

        if (countIDATs(chunks) != 1) {
            throw new IllegalArgumentException("Images with more than 1 IDATs are not supported");
        }

        var result = new ArrayList<RawChunk>();
        for (var chunk : chunks) {
            if (chunk.type().equals("IDAT")) {
                result.add(new RawChunk("IDAT", encryptedJoinedCompressed.length, null, encryptedJoinedCompressed, calculateCRC(encryptedJoinedCompressed, "IDAT")));
            } else {
                result.add(chunk);
            }
        }

        return result;
    }
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

    public List<RawChunk> decryptECB(List<RawChunk> chunks, CustomPrivateKey privateKey) throws BadPaddingException, DataFormatException, NoSuchAlgorithmException {
        int compressionLevel = (Integer) interpreter.analyseChunk(chunks.get(0), "IHDR").properties()
                .get("Compression method") == 0 ? Deflater.DEFLATED : Deflater.BEST_COMPRESSION;

        var joinedBytes = joinIDATs(chunks);

        var compressedBytes = concatMany(joinedBytes);
        System.out.println("compressedBytes " + map(compressedBytes));
        System.out.println("compressedBytes length" + compressedBytes.length);
        var decompressedBytes = decompressZlib(compressedBytes);
        System.out.println("decompressedBytes in decrypt " + map(decompressedBytes));
        System.out.println("decompressedBytes.length in decrypt " + decompressedBytes.length);

        int keyLength = privateKey.n().bitLength();
        int blockSize = (keyLength / 8) - 1;

        var dividedDecompressed = divideByteArray(decompressedBytes, blockSize);
        var decrypted = new ArrayList<byte[]>();
        for (var bytes : dividedDecompressed) {
            var rsa = decrypt(bytes, privateKey, getIDATSizes(chunks).get(0));
            decrypted.add(rsa);
        }
        var decryptedJoined = concatMany(decrypted);
        System.out.println("decryptedJoined " + map(decryptedJoined));
        System.out.println("decryptedJoined.length " + decryptedJoined.length);

        var decryptedJoinedCompressed = compressZlib(decryptedJoined, compressionLevel);

        System.out.println("decryptedJoinedCompressed " + map(decryptedJoinedCompressed));
        System.out.println("decryptedJoinedCompressed.length " + decryptedJoinedCompressed.length);


        if (countIDATs(chunks) != 1) {
            throw new IllegalArgumentException("Images with more than 1 IDATs are not supported");
        }

        var result = new ArrayList<RawChunk>();
        for (var chunk : chunks) {
            if (chunk.type().equals("IDAT")) {
                result.add(new RawChunk("IDAT", decryptedJoinedCompressed.length, null, decryptedJoinedCompressed, calculateCRC(decryptedJoinedCompressed, "IDAT")));
            } else {
                result.add(chunk);
            }
        }

        return result;
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
        return null;
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

    private static List<String> map(byte[] bytes) {
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