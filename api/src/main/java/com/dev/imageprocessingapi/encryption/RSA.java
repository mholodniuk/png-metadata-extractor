package com.dev.imageprocessingapi.encryption;

import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.dev.imageprocessingapi.encryption.Utils.generateRSACustomKeyPair;

@Component
@AllArgsConstructor
public class RSA {

    // kryterium porownawncze -> ograniczenia na dlugosc bloku
    // rozmowa - decyzje projektowe:
    // w jaki sposob zrealizowano szyfrowanie
    // ktore czesci pliku sa szyforwane
    // jak sobie radzimy z paddingiem
    // jak radzimy sobie z tym ze dane po zaszyfrowaniu maja wiekszy rozmiar

    public List<RawChunk> encryptECB(List<RawChunk> chunks, int keyLength) throws BadPaddingException {
        if (keyLength % 8 != 0) {
            throw new RuntimeException("KEY INVALID!!! SHOULD BE DIVISIBLE BY 8");
        }
        var keys = generateRSACustomKeyPair(keyLength);
        System.out.println(keys.privateKey());
        System.out.println(keys.publicKey());

        var result = new ArrayList<RawChunk>(chunks.size());
        for (var chunk : chunks) {
            result.add(chunk);
            if (chunk.type().equals("IDAT")) {
                System.out.println("chunk.rawBytes() " + Arrays.toString(chunk.rawBytes()));
                System.out.println("chunk.rawBytes().length " + chunk.rawBytes().length);

                var encrypted = crypt(chunk.rawBytes(), keys.publicKey());
                System.out.println("encrypted " + Arrays.toString(encrypted));
                System.out.println("encrypted.length " + encrypted.length);

                var decrypted = decrypt(encrypted, keys.privateKey(), chunk.length());

                System.out.println("decrypted " + Arrays.toString(decrypted));
                System.out.println("decrypted.length " + decrypted.length);

                System.out.println(Arrays.equals(chunk.rawBytes(), decrypted));
            }
        }
        return result;
    }

    public static byte[] crypt(byte[] msg, CustomPublicKey key) throws BadPaddingException {
        BigInteger m = parseMsg(msg, key.n());
        BigInteger c = pow(m, key.e(), key.n());
        return toByteArray(c, getByteLength(key.n()));
    }

    private static byte[] decrypt(byte[] msg, CustomPrivateKey key, int length) throws BadPaddingException {
        BigInteger c = parseMsg(msg, key.n());
        BigInteger m = c.modPow(key.d(), key.n());
        var paddingResult = toByteArray(m, getByteLength(key.n()));
        return unPadByteArray(paddingResult, length);
    }

    private static byte[] unPadByteArray(byte[] byteArray, int originalSize) {
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

    private static BigInteger parseMsg(byte[] msg, BigInteger n) throws BadPaddingException {
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


    private static byte[] joinChunksToByteArray(List<RawChunk> chunks) {
        List<byte[]> byteArrayList = new ArrayList<>();
        for (RawChunk chunk : chunks) {
            if (chunk.type().equals("IDAT")) {
                byteArrayList.add(chunk.rawBytes());
            }
        }

        return concatMany(byteArrayList);
    }

    private static byte[] concatMany(List<byte[]> arrays) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (arrays != null) {
            arrays.stream().filter(Objects::nonNull).forEach(array -> out.write(array, 0, array.length));
        }
        return out.toByteArray();
    }
}