package org.example.Cifrado_Simetrico_AES;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Clave AES fija de EXACTAMENTE 16 bytes (128 bits).
 */
public class KeyManager {

    // Clave de 16 bytes exactos (AES-128)
    private static final String KEY_STRING = "MiClaveAES123456";

    public static SecretKeySpec getKey() {
        byte[] keyBytes = KEY_STRING.getBytes(StandardCharsets.UTF_8);
        // Comprobación de longitud
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("La clave debe tener exactamente 16 bytes");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }
}

