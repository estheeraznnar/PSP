package org.example.EJ3Repaso;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utilidades de cifrado/descifrado de mensajes con AES/CBC/PKCS5Padding.
 *
 * Formato del mensaje cifrado (en bytes):
 *   [16 bytes IV aleatorio][resto = datos cifrados]
 *
 * AES/CBC/PKCS5Padding es el modo estándar recomendado en la teoría.
 */
public class MessageCrypto {

    private static final String ALGO = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * Genera una clave secreta AES de 128 bits.
     *
     * @return SecretKey lista para usar.
     */
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGO);
            keyGen.init(128); // 128 bits = 16 bytes
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generando clave AES", e);
        }
    }

    /**
     * Cifra un mensaje de texto plano.
     *
     * @param message Mensaje en texto plano.
     * @param key     Clave AES compartida.
     * @return Array de bytes: IV(16 bytes) + datos cifrados.
     */
    public static byte[] encrypt(String message, SecretKey key) {
        try {
            // 1. Generar IV aleatorio de 16 bytes (necesario para CBC)
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 2. Configurar Cipher en modo cifrado
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            // 3. Convertir mensaje a bytes y cifrar
            byte[] plain = message.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = cipher.doFinal(plain);

            // 4. Concatenar IV + cifrado (para poder descifrar después)
            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error cifrando mensaje", e);
        }
    }

    /**
     * Descifra un array de bytes en formato [IV][cifrado].
     *
     * @param encryptedWithIv Array de bytes: IV + datos cifrados.
     * @param key             Clave AES compartida.
     * @return Mensaje original en texto plano.
     */
    public static String decrypt(byte[] encryptedWithIv, SecretKey key) {
        try {
            // 1. Extraer IV (primeros 16 bytes) y datos cifrados (resto)
            byte[] iv = Arrays.copyOfRange(encryptedWithIv, 0, 16);
            byte[] encrypted = Arrays.copyOfRange(encryptedWithIv, 16, encryptedWithIv.length);

            // 2. Configurar Cipher en modo descifrado con el IV correcto
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            // 3. Descifrar y convertir a texto
            byte[] plain = cipher.doFinal(encrypted);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error descifrando mensaje", e);
        }
    }

    /**
     * Reconstruye una SecretKey AES a partir de bytes.
     * Útil para compartir la clave.
     */
    public static SecretKey fromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, ALGO);
    }
}


