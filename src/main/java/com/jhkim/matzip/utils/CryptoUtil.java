package com.jhkim.matzip.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtil {
    public static String  hashSha512(String input){
        return CryptoUtil.hashSha512(input, null);
    }
    public static String hashSha512(String input, String fallback){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(input.getBytes(StandardCharsets.UTF_8));
            return String.format("%0128x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException ignored) {
            return fallback;
        }
    }
    private CryptoUtil() {
        super();
    }
}
