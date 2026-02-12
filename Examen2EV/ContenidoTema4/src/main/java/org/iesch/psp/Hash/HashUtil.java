package org.iesch.psp.Hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtil {

    /**
     * Genera un hash a partir de un mensaje.
     */
    public static String getHash(String msg) {
        try {
            // Convertimos el mensaje en un array de bytes UTF-16LE para compatibilidad [cite: 138]
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);

            // Creamos una instancia de SHA-256 [cite: 139]
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Calculamos el hash [cite: 140]
            byte[] hashValue = digest.digest(msgBytes);

            // Lo convertimos a Base64 para que sea legible y fácil de guardar [cite: 141]
            return Base64.getEncoder().encodeToString(hashValue);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Comprueba si un mensaje coincide con un hash guardado.
     */
    public static boolean checkHash(String msg, String hash) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);
            byte[] hashBytes = Base64.getDecoder().decode(hash);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashValue = digest.digest(msgBytes);

            // Comprobamos si los arrays de bytes son idénticos [cite: 154, 158]
            if (hashValue.length != hashBytes.length) return false;

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

    public static void main(String[] args) {
        String mensaje = "Hola Mundo";
        String miHash = getHash(mensaje);

        System.out.println("Mensaje: " + mensaje);
        System.out.println("Hash generado: " + miHash);

        // Verificación
        boolean coinciden = checkHash(mensaje, miHash);
        System.out.println("¿El hash coincide?: " + coinciden);
    }
}