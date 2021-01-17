package com.sashutosh.dbpertenantwithliquibase.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EncryptionTest {

    EncryptionService es = new EncryptionServiceImpl();

    @Test
    public void testEncryptDecrypt() {
        String strToEncrypt = "secret";
        String encrypted = es.encrypt(strToEncrypt, "Cloud_123", "randomSalt");
        String decrypted = es.decrypt(encrypted, "Cloud_123", "randomSalt");
        assertThat(strToEncrypt.equals(decrypted));
    }
}
