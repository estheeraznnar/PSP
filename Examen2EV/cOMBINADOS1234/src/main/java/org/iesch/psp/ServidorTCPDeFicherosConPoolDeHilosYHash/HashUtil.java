// --- ServidorTCPDeFicherosConPoolDeHilosYHash/HashUtil.java ---
package org.iesch.psp.ServidorTCPDeFicherosConPoolDeHilosYHash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilidad para generar y verificar hash SHA-256
 * Basado en el ejemplo del Tema 4 (páginas 5-6)
 */
public class HashUtil {

    /**
     * Genera el hash SHA-256 de un array de bytes
     * @param data Datos a hashear
     * @return Hash en formato Base64
     */
    public static String getHash(byte[] data) {
        try {
            // Obtener instancia de MessageDigest para SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Calcular el hash
            byte[] hashValue = digest.digest(data);

            // Devolver en Base64
            return Base64.getEncoder().encodeToString(hashValue);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica si un hash coincide con los datos
     * @param data Datos originales
     * @param hash Hash a verificar (en Base64)
     * @return true si coinciden
     */
    public static boolean checkHash(byte[] data, String hash) {
        try {
            // Calcular hash de los datos
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashValue = digest.digest(data);

            // Decodificar hash proporcionado
            byte[] hashBytes = Base64.getDecoder().decode(hash);

            // Comparar byte a byte (como en el PDF)
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