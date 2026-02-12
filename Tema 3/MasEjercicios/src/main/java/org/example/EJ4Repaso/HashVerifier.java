package org.example.EJ4Repaso;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Clase de utilidad para calcular y verificar hashes SHA-256 de ficheros.
 * Usamos SHA-256 como en el tema de hash de la UD4.
 */
public class HashVerifier {

    /**
     * Calcula el hash SHA-256 de un fichero y lo devuelve en Base64.
     *
     * @param file Fichero del que queremos calcular el hash.
     * @return Cadena Base64 que representa el hash SHA-256 del fichero.
     */
    public static String calculateSHA256(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            // Obtenemos una instancia de MessageDigest para SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Leemos el fichero por bloques y vamos actualizando el digest
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }

            // Obtenemos el hash final
            byte[] hashBytes = digest.digest();

            // Lo codificamos en Base64 para poder imprimir y guardar fácilmente
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculando hash de " + file.getName(), e);
        }
    }

    /**
     * Verifica si el hash SHA-256 del fichero coincide con el esperado.
     *
     * @param file         Fichero a comprobar.
     * @param expectedHash Hash esperado (Base64).
     * @return true si coinciden, false en caso contrario.
     */
    public static boolean verifyHash(File file, String expectedHash) {
        String actualHash = calculateSHA256(file);
        return actualHash.equals(expectedHash);
    }
}
