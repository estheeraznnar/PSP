package org.iesch.psp.HTTPHashSHA256;// --- HashUtil.java ---
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtil {

    public static String getHash(String msg) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashValue = digest.digest(msgBytes);
            return Base64.getEncoder().encodeToString(hashValue);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkHash(String msg, String hash) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);
            byte[] hashBytes = Base64.getDecoder().decode(hash);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashValue = digest.digest(msgBytes);

            if (hashValue.length != hashBytes.length) {
                return false;
            }
            for (int i = 0; i < hashBytes.length; i++) {
                if (hashValue[i] != hashBytes[i]) {
                    return false;
                }
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}