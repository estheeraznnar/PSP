// --- SistemaDeChatUDPConcurrenteConCifrado/CifradoAES.java ---
package org.iesch.psp.SistemaDeChatUDPConcurrenteConCifrado;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utilidad para cifrado/descifrado AES
 * Basado en el ejemplo del Tema 4 (páginas 19-22)
 */
public class CifradoAES {

    // Clave de cifrado de 16 bytes (128 bits)
    // En producción debería ser compartida de forma segura
    private static final byte[] key = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16
    };

    // Vector de inicialización (IV) de 16 bytes
    private static final byte[] iv = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16
    };

    /**
     * Cifra un texto usando AES/CBC/PKCS5Padding
     * @param textoPlano Texto a cifrar
     * @return Texto cifrado en Base64
     */
    public static String cifrar(String textoPlano) {
        try {
            // Crear el cifrador AES en modo CBC con padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Crear la clave secreta
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            // Crear el vector de inicialización
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Inicializar en modo cifrado
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // Cifrar los bytes del texto
            byte[] textoCifrado = cipher.doFinal(
                    textoPlano.getBytes(StandardCharsets.UTF_8)
            );

            // Devolver en Base64 para transmisión segura
            return Base64.getEncoder().encodeToString(textoCifrado);

        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar: " + e.getMessage());
        }
    }

    /**
     * Descifra un texto cifrado con AES
     * @param textoCifrado Texto cifrado en Base64
     * @return Texto descifrado
     */
    public static String descifrar(String textoCifrado) {
        try {
            // Crear el cifrador AES
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Crear la clave secreta
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            // Crear el vector de inicialización
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Inicializar en modo descifrado
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            // Decodificar Base64 y descifrar
            byte[] bytesDescifrados = cipher.doFinal(
                    Base64.getDecoder().decode(textoCifrado)
            );

            // Devolver como String
            return new String(bytesDescifrados, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar: " + e.getMessage());
        }
    }
}