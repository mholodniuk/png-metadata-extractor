package com.dev.imageprocessingapi.encryption;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {RSA.class})
public class EncryptionTests {



    @Autowired
    private RSA encryptor;


    @Test
    public void test1() {

    }
}
