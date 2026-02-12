package org.example.Gestion_Usu_SALT;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
import java.util.Arrays;

/**
 * Utilidades de contraseñas con PBKDF2WithHmacSHA256 + sal.
 * saltedHash = Base64(sal[32 bytes] + hash[32 bytes])
 */
public class PasswordUtil {

    public static byte[] generateSalt() {
        byte[] salt = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] getHash(String password, byte[] salt) {
        final int ITERATIONS = 10000;
        final int KEY_LENGTH = 256;

        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createSaltedHash(String password) {
        byte[] salt = generateSalt();
        byte[] hash = getHash(password, salt);

        byte[] combined = new byte[64];
        System.arraycopy(salt, 0, combined, 0, 32);
        System.arraycopy(hash, 0, combined, 32, 32);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static boolean checkPassword(String password, String storedBase64) {
        byte[] combined = Base64.getDecoder().decode(storedBase64);
        byte[] salt = new byte[32];
        byte[] hash = new byte[32];
        System.arraycopy(combined, 0, salt, 0, 32);
        System.arraycopy(combined, 32, hash, 0, 32);

        byte[] checkHash = getHash(password, salt);
        return Arrays.equals(hash, checkHash);
    }
}
