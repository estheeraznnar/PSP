package org.example.Cifrado_Simetrico_AES;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;

/**
 * Cifra y descifra ficheros con AES/CBC/PKCS5Padding.
 *
 * Formato fichero cifrado:
 *   [16 bytes IV aleatorio][datos cifrados]
 */
public class FileCrypto {

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * Cifra un fichero de entrada y lo guarda en salida.
     *
     * 1) Genera IV aleatorio.
     * 2) Escribe IV al inicio del fichero cifrado.
     * 3) Usa CipherOutputStream para cifrar el resto.
     */
    public static void encryptFile(File input, File output) throws Exception {
        SecretKeySpec key = KeyManager.getKey();

        // Generar IV aleatorio de 16 bytes
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Configurar cipher para cifrado
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        // Abrir streams
        try (FileInputStream fis = new FileInputStream(input);
             FileOutputStream fos = new FileOutputStream(output);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            // Escribir IV al inicio del fichero cifrado
            fos.write(iv);

            // Copiar fichero original al cifrado (se cifra automáticamente)
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, read);
            }
        }

        System.out.println(" Fichero cifrado: " + output.getName());
    }

    /**
     * Descifra un fichero cifrado y lo guarda en salida.
     *
     * 1) Lee IV de los primeros 16 bytes.
     * 2) Usa CipherInputStream para descifrar el resto.
     */
    public static void decryptFile(File input, File output) throws Exception {
        SecretKeySpec key = KeyManager.getKey();

        try (FileInputStream fis = new FileInputStream(input);
             FileOutputStream fos = new FileOutputStream(output)) {

            // Leer IV de los primeros 16 bytes del fichero cifrado
            byte[] iv = new byte[16];
            fis.read(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Configurar cipher para descifrado
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            // Crear CipherInputStream que descifrará automáticamente
            try (CipherInputStream cis = new CipherInputStream(fis, cipher)) {

                // Copiar datos descifrados al fichero de salida
                byte[] buffer = new byte[8192];
                int read;
                while ((read = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            }
        }

        System.out.println(" Fichero descifrado: " + output.getName());
    }
}
