package org.iesch.psp.ServidorDeAutenticaciónConcurrente;// --- PasswordUtil.java (PBKDF2 con Salt) ---
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 32;

    public static String hashPassword(String password) {
        try {
            // Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Generar hash con PBKDF2
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Concatenar salt + hash (64 bytes total)
            byte[] hashWithSalt = new byte[SALT_LENGTH + hash.length];
            System.arraycopy(salt, 0, hashWithSalt, 0, SALT_LENGTH);
            System.arraycopy(hash, 0, hashWithSalt, SALT_LENGTH, hash.length);

            return Base64.getEncoder().encodeToString(hashWithSalt);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkPassword(String password, String storedHash) {
        try {
            byte[] hashWithSalt = Base64.getDecoder().decode(storedHash);

            // Extraer salt (primeros 32 bytes)
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(hashWithSalt, 0, salt, 0, SALT_LENGTH);

            // Extraer hash original
            byte[] originalHash = new byte[hashWithSalt.length - SALT_LENGTH];
            System.arraycopy(hashWithSalt, SALT_LENGTH, originalHash, 0, originalHash.length);

            // Generar hash con la contraseña proporcionada
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] newHash = factory.generateSecret(spec).getEncoded();

            // Comparar byte a byte
            if (originalHash.length != newHash.length) {
                return false;
            }
            for (int i = 0; i < originalHash.length; i++) {
                if (originalHash[i] != newHash[i]) {
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}