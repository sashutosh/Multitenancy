package com.sashutosh.dbpertenantwithliquibase.util;

public interface EncryptionService {
    String encrypt(String password, String secret, String salt);

    String decrypt(String strToDecrypt, String secret, String salt);
}
