package org.example.EJ1Repaso;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidades para cifrado/descifrado AES
 * Proporciona métodos estáticos para trabajar con criptografía simétrica
 */
public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    // Clave compartida (en producción debería intercambiarse de forma segura)
    private static final String SHARED_KEY = "MiClaveSecreta16"; // 16 bytes = 128 bits

    /**
     * Genera una clave AES aleatoria de 128 bits
     */
    public static SecretKey generarClave() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(128, new SecureRandom());
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generando clave AES", e);
        }
    }

    /**
     * Obtiene la clave compartida predefinida
     */
    public static SecretKey getClaveCompartida() {
        byte[] keyBytes = SHARED_KEY.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * Cifra un mensaje usando AES
     * @param mensaje Mensaje en texto claro
     * @param clave Clave secreta AES
     * @return Mensaje cifrado en Base64
     */
    public static String cifrar(String mensaje, SecretKey clave) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, clave);

            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));

            // Codificar en Base64 para poder transmitir como String
            return Base64.getEncoder().encodeToString(mensajeCifrado);

        } catch (Exception e) {
            throw new RuntimeException("Error cifrando mensaje", e);
        }
    }

    /**
     * Descifra un mensaje cifrado con AES
     * @param mensajeCifrado Mensaje cifrado en Base64
     * @param clave Clave secreta AES
     * @return Mensaje descifrado en texto claro
     */
    public static String descifrar(String mensajeCifrado, SecretKey clave) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, clave);

            // Decodificar desde Base64
            byte[] mensajeBytes = Base64.getDecoder().decode(mensajeCifrado);

            byte[] mensajeDescifrado = cipher.doFinal(mensajeBytes);

            return new String(mensajeDescifrado, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error descifrando mensaje", e);
        }
    }
}
