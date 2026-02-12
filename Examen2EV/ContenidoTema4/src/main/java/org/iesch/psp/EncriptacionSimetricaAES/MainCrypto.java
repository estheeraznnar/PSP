package org.iesch.psp.EncriptacionSimetricaAES;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class MainCrypto {
    // Clave de cifrado (16 bytes = 128 bits) [cite: 555, 556]
    private static final byte[] key = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12,
            0x13, 0x14, 0x15, 0x16
    };

    // Vector de inicialización (IV) [cite: 560, 561]
    private static final byte[] iv = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12,
            0x13, 0x14, 0x15, 0x16
    };

    // Ruta del fichero donde se guardarán los datos cifrados [cite: 565, 566]
    private static final String path = "archivo_cifrado.dat";

    public static void main(String[] args) {
        cifrado();   // [cite: 568]
        descifrado(); // [cite: 569]
    }

    // Método para realizar el cifrado [cite: 574, 575]
    private static void cifrado() {
        try {
            // Creamos stream hacia el fichero [cite: 578]
            FileOutputStream fileOut = new FileOutputStream(path);

            // Creamos el cifrador AES/CBC/PKCS5Padding 
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES"); // [cite: 583]
            IvParameterSpec ivSpec = new IvParameterSpec(iv); // [cite: 584]

            // Inicializamos en modo cifrado [cite: 585]
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // Creamos un flujo cifrado [cite: 586, 587]
            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(cipherOut, "UTF-8")); // [cite: 582, 587]

            // Escribimos el texto en claro [cite: 589]
            writer.write("Este es mi texto en claro");
            writer.newLine(); // [cite: 590]

            // Cerramos flujos [cite: 591, 592, 593, 594]
            writer.close();
            cipherOut.close();
            fileOut.close();
            System.out.println("Fichero cifrado"); // [cite: 595]
        } catch (Exception e) {
            System.out.println("Error en el cifrado"); // [cite: 597]
            e.printStackTrace(); // [cite: 598]
        }
    }

    // Método de descifrado [cite: 603, 604]
    private static void descifrado() {
        try {
            // Stream de lectura del fichero [cite: 607, 608]
            FileInputStream fileIn = new FileInputStream(path);

            // Configuramos el descifrado con los mismos parámetros [cite: 609, 611]
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES"); // [cite: 613]
            IvParameterSpec ivSpec = new IvParameterSpec(iv); // [cite: 614]

            // Inicializamos en modo descifrado [cite: 615]
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            // Flujo de descifrado [cite: 616, 617]
            CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
            BufferedReader reader = new BufferedReader(new InputStreamReader(cipherIn, "UTF-8")); // [cite: 612, 618]

            System.out.println("Texto en claro contenido en el fichero:"); // [cite: 619]
            String line;
            while ((line = reader.readLine()) != null) { // [cite: 620, 621]
                System.out.println(line); // [cite: 622]
            }

            // Cerramos flujos [cite: 624, 625, 626, 627]
            reader.close();
            cipherIn.close();
            fileIn.close();
        } catch (Exception e) {
            System.out.println("Lectura fichero errónea"); // [cite: 629]
            e.printStackTrace(); // [cite: 630]
        }
    }
}