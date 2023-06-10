package com.dev.imageprocessingapi.encryption;

import java.math.BigInteger;

public record CustomPublicKey(BigInteger e, BigInteger n)  {
}
