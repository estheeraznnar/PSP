// --- ServidorDeAutenticacionConcurrente/PasswordUtil.java ---
package org.iesch.psp.ServidordeAutenticaciOnConcurrente;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para hash de contraseñas con PBKDF2 y Salt
 * Basado en el ejemplo del Tema 4 (páginas 9-14)
 */
public class PasswordUtil {

    // Número de iteraciones para PBKDF2
    private static final int ITERATIONS = 1000;
    // Longitud de la clave generada en bits
    private static final int KEY_LENGTH = 256;
    // Longitud del salt en bytes
    private static final int SALT_LENGTH = 32;

    /**
     * Genera un hash seguro de la contraseña usando PBKDF2 con salt aleatorio
     * @param password Contraseña en texto plano
     * @return Hash en Base64 (salt + hash concatenados)
     */
    public static String hashPassword(String password) {
        try {
            // Generar salt aleatorio usando SecureRandom
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Crear especificación para PBKDF2
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );

            // Obtener la fábrica de claves para PBKDF2
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            // Generar el hash
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Concatenar salt + hash (64 bytes total)
            byte[] hashWithSalt = new byte[SALT_LENGTH + hash.length];
            System.arraycopy(salt, 0, hashWithSalt, 0, SALT_LENGTH);
            System.arraycopy(hash, 0, hashWithSalt, SALT_LENGTH, hash.length);

            // Devolver en formato Base64
            return Base64.getEncoder().encodeToString(hashWithSalt);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica si una contraseña coincide con su hash almacenado
     * @param password Contraseña a verificar
     * @param storedHash Hash almacenado (en Base64)
     * @return true si coinciden, false en caso contrario
     */
    public static boolean checkPassword(String password, String storedHash) {
        try {
            // Decodificar el hash almacenado
            byte[] hashWithSalt = Base64.getDecoder().decode(storedHash);

            // Extraer el salt (primeros 32 bytes)
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(hashWithSalt, 0, salt, 0, SALT_LENGTH);

            // Extraer el hash original (resto de bytes)
            byte[] originalHash = new byte[hashWithSalt.length - SALT_LENGTH];
            System.arraycopy(hashWithSalt, SALT_LENGTH, originalHash, 0, originalHash.length);

            // Generar hash con la contraseña proporcionada y el salt extraído
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] newHash = factory.generateSecret(spec).getEncoded();

            // Comparar byte a byte (como en el ejemplo del PDF)
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