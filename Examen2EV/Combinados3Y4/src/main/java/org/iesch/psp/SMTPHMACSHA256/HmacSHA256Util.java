package org.iesch.psp.SMTPHMACSHA256;// --- HmacSHA256Util.java ---
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class HmacSHA256Util {

    public static byte[] generateKey() {
        byte[] key = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return key;
    }

    public static String getHmac(String msg, byte[] key) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);

            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            byte[] hmacValue = mac.doFinal(msgBytes);
            return Base64.getEncoder().encodeToString(hmacValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkHmac(String msg, String hmac, byte[] key) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);
            byte[] hmacBytes = Base64.getDecoder().decode(hmac);

            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            byte[] hmacValue = mac.doFinal(msgBytes);

            if (hmacValue.length != hmacBytes.length) {
                return false;
            }
            for (int i = 0; i < hmacBytes.length; i++) {
                if (hmacValue[i] != hmacBytes[i]) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}