package org.example.EJ2Repaso;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

/**
 * Clase de utilidad para trabajar con contraseñas de forma segura.
 * Implementa "salted password hashing" usando PBKDF2WithHmacSHA256,
 * tal y como se explica en el tema de seguridad (UD4).
 */
public class PasswordUtil {

    /**
     * Genera una sal (salt) aleatoria de 32 bytes.
     * La sal se usa para evitar ataques de diccionario y rainbow tables.
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[32];        // 32 bytes = 256 bits
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);           // Rellena con valores aleatorios
        return salt;
    }

    /**
     * Obtiene el hash de una contraseña usando PBKDF2 + HMAC-SHA256.
     *
     * @param password Contraseña en texto claro.
     * @param salt     Sal aleatoria asociada a esa contraseña.
     * @return Array de bytes con el hash resultante.
     */
    public static byte[] getHash(String password, byte[] salt) {
        final int ITERATIONS = 10000;   // Iteraciones (algoritmo "lento" para frenar fuerza bruta)
        final int KEY_LENGTH = 256;     // Longitud de la clave resultante (bits)

        try {
            // Especificación de PBKDF2: password, salt, iteraciones y longitud
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            // Crea la factoría para PBKDF2 con HMAC-SHA256
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // Genera el hash
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Crea un hash "salted" listo para almacenar:
     * - Genera la sal
     * - Calcula hash(password + salt)
     * - Devuelve Base64(salt[32] + hash[32]) → 64 bytes
     */
    public static String createSaltedHash(String password) {
        byte[] salt = generateSalt();
        byte[] hash = getHash(password, salt);

        // Unimos salt y hash en un único array de 64 bytes
        byte[] combined = new byte[64];
        System.arraycopy(salt, 0, combined, 0, 32);
        System.arraycopy(hash, 0, combined, 32, 32);

        // Lo convertimos a Base64 para poder guardarlo como texto
        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Comprueba si una contraseña en texto coincide
     * con el hash almacenado (formato Base64).
     */
    public static boolean checkPassword(String password, String storedBase64) {
        // Decodificamos la combinación salt+hash desde Base64
        byte[] combined = Base64.getDecoder().decode(storedBase64);

        // Separamos salt y hash
        byte[] salt = new byte[32];
        byte[] hash = new byte[32];
        System.arraycopy(combined, 0, salt, 0, 32);
        System.arraycopy(combined, 32, hash, 0, 32);

        // Calculamos el hash de la contraseña introducida con la misma sal
        byte[] checkHash = getHash(password, salt);

        // Comparamos byte a byte los dos hashes
        if (checkHash.length != hash.length) return false;
        for (int i = 0; i < hash.length; i++) {
            if (hash[i] != checkHash[i]) return false;
        }
        return true;
    }
}

