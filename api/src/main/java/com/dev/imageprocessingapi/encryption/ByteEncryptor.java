package com.dev.imageprocessingapi.encryption;

import com.dev.imageprocessingapi.metadataextractor.domain.RawChunk;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dev.imageprocessingapi.encryption.Utils.generateRSACustomKeyPair;
import static com.dev.imageprocessingapi.encryption.Utils.generateRSAKeyPair;

@Component
@AllArgsConstructor
public class ByteEncryptor {

    // kryterium porownawncze -> ograniczenia na dlugosc bloku

    public void encrypt(List<RawChunk> chunks) throws NoSuchAlgorithmException {
        int keyLength = 2048;
        var p1 = generateRSAKeyPair(keyLength);
        var p2 = generateRSACustomKeyPair(keyLength);
        System.out.println(chunks.size());
        System.out.println(p1);
        System.out.println(p2);

        System.out.println();

//        var result = new ArrayList<RawChunk>(chunks.size());
//        for (var chunk : chunks) {
//            if (chunk.type().equals("IDAT")) {
//                System.out.println(chunk.rawBytes().length);
//                // javax.crypto.IllegalBlockSizeException: Data must not be longer than 245 bytes
////                encryptCipher.update(chunk.rawBytes());
////                var encryptedBytes = encryptCipher.doFinal();
////                var recalculatedCRC = calculateCRC(encryptedBytes, chunk.type());
////                var encryptedChunk = new RawChunk(chunk.type(), chunk.length(), chunk.offset(), encryptedBytes, recalculatedCRC);
////                result.add(encryptedChunk);
//            } else {
//                result.add(chunk);
//            }
//        }
//        System.out.println(result);
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
            arrays.stream()
                    .filter(Objects::nonNull)
                    .forEach(array -> out.write(array, 0, array.length));
        }
        return out.toByteArray();
    }
}
