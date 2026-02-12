package org.example.Cifrado;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utilidad para cifrado simétrico de texto usando el algoritmo AES (Advanced Encryption Standard).
 * Implementa cifrado básico sin modo de operación ni vector de inicialización explícito.
 */
public class CifradoAES {

    // Clave de cifrado de 16 bytes (128 bits) - DEBE cambiarse en producción y almacenarse de forma segura
    private static final String CLAVE = "1234567890123456"; // 16 bytes = 128 bits

    /**
     * Cifra un texto plano usando AES con la clave predefinida.
     * El resultado se codifica en Base64 para facilitar su almacenamiento y transmisión.
     *
     * @param texto String con el texto plano a cifrar
     * @return String con el texto cifrado codificado en Base64
     * @throws RuntimeException si ocurre algún error durante el proceso de cifrado
     */
    public static String cifrar(String texto) {
        try {
            // Crear la clave AES a partir del String de clave (convertido a bytes)
            SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(), "AES");

            // Obtener instancia del cifrador AES (usa modo ECB por defecto)
            Cipher cipher = Cipher.getInstance("AES");

            // Inicializar el cifrador en modo cifrado con la clave
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // Cifrar el texto: convertir a bytes, aplicar cifrado y obtener resultado
            byte[] textoCifrado = cipher.doFinal(texto.getBytes());

            // Codificar los bytes cifrados en Base64 para obtener un String manejable
            return Base64.getEncoder().encodeToString(textoCifrado);

        } catch (Exception e) {
            // Encapsular cualquier excepción (NoSuchAlgorithmException, InvalidKeyException, etc.)
            throw new RuntimeException("Error cifrando", e);
        }
    }
}
