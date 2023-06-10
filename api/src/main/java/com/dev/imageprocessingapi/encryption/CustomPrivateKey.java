package com.dev.imageprocessingapi.encryption;

import java.math.BigInteger;

public record CustomPrivateKey(BigInteger d, BigInteger n) {
}
